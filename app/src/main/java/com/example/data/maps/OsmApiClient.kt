package com.example.data.maps

import android.util.Log
import com.example.data.model.NearbyStore
import com.example.data.model.StoreType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class OsmApiClient {

  private val client = OkHttpClient.Builder()
    .connectTimeout(25, TimeUnit.SECONDS)
    .readTimeout(25, TimeUnit.SECONDS)
    .build()

  private val formMediaType = "application/x-www-form-urlencoded; charset=utf-8".toMediaType()

  // In-memory caches to reduce network requests & obey API rate limits
  private val overpassCache = ConcurrentHashMap<String, List<NearbyStore>>()
  private val geocodingCache = ConcurrentHashMap<String, Pair<Double, Double>>()
  private var lastGeocodeTimestamp = 0L

  /**
   * Search city or address using the Nominatim OpenStreetMap API.
   * Debounces & respects max 1 request per second policy with in-memory caching.
   */
  suspend fun searchAddress(query: String): Result<Pair<Double, Double>> = withContext(Dispatchers.IO) {
    val trimmed = query.trim().lowercase()
    if (trimmed.isEmpty()) {
      return@withContext Result.failure(IllegalArgumentException("Empty query"))
    }

    geocodingCache[trimmed]?.let {
      return@withContext Result.success(it)
    }

    // Rate limiting: wait if less than 1 second since last Nominatim call
    val now = System.currentTimeMillis()
    val elapsed = now - lastGeocodeTimestamp
    if (elapsed < 1000) {
      kotlinx.coroutines.delay(1000 - elapsed)
    }
    lastGeocodeTimestamp = System.currentTimeMillis()

    try {
      val encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.toString())
      val url = "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&addressdetails=1&limit=5"
      val request = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "DapurRantau/1.0 (Android; MarketFinder)")
        .get()
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("Nominatim returned HTTP ${response.code}"))
      }

      val arr = JSONArray(body)
      if (arr.length() == 0) {
        return@withContext Result.failure(NoSuchElementException("No locations found for '$query'"))
      }

      val first = arr.getJSONObject(0)
      val lat = first.getDouble("lat")
      val lon = first.getDouble("lon")
      val coords = Pair(lat, lon)
      geocodingCache[trimmed] = coords
      Result.success(coords)
    } catch (e: Exception) {
      Log.e("OsmApiClient", "Nominatim search error: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * Query nearby food markets & grocery stores via Overpass API.
   * Starts at 2km, widening to 5km if fewer than 5 results are returned.
   */
  suspend fun searchNearbyStores(
    latitude: Double,
    longitude: Double,
    initialRadiusMeters: Int = 2000
  ): Result<List<NearbyStore>> = withContext(Dispatchers.IO) {
    val cacheKey = "%.2f_%.2f_%d".format(latitude, longitude, initialRadiusMeters)
    overpassCache[cacheKey]?.let {
      return@withContext Result.success(it)
    }

    try {
      // Step 1: Query initial 2km radius
      var stores = executeOverpassQuery(latitude, longitude, initialRadiusMeters)

      // Step 2: Auto-widen to 5km if fewer than 5 stores found
      if (stores.size < 5 && initialRadiusMeters < 5000) {
        val widenedKey = "%.2f_%.2f_5000".format(latitude, longitude)
        val widened = overpassCache[widenedKey] ?: executeOverpassQuery(latitude, longitude, 5000)
        overpassCache[widenedKey] = widened
        if (widened.size > stores.size) {
          stores = widened
        }
      }

      val sorted = stores.sortedBy { it.distanceKm }
      overpassCache[cacheKey] = sorted
      Result.success(sorted)
    } catch (e: Exception) {
      Log.e("OsmApiClient", "Overpass search error: ${e.message}", e)
      Result.failure(e)
    }
  }

  private fun executeOverpassQuery(
    lat: Double,
    lng: Double,
    radiusMeters: Int
  ): List<NearbyStore> {
    val query = """
      [out:json][timeout:25];
      (
        node["shop"="supermarket"](around:$radiusMeters,$lat,$lng);
        way["shop"="supermarket"](around:$radiusMeters,$lat,$lng);
        node["shop"="convenience"](around:$radiusMeters,$lat,$lng);
        way["shop"="convenience"](around:$radiusMeters,$lat,$lng);
        node["shop"="greengrocer"](around:$radiusMeters,$lat,$lng);
        way["shop"="greengrocer"](around:$radiusMeters,$lat,$lng);
        node["shop"="general"](around:$radiusMeters,$lat,$lng);
        way["shop"="general"](around:$radiusMeters,$lat,$lng);
        node["amenity"="marketplace"](around:$radiusMeters,$lat,$lng);
        way["amenity"="marketplace"](around:$radiusMeters,$lat,$lng);
      );
      out center tags;
    """.trimIndent()

    val formBody = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
    val request = Request.Builder()
      .url("https://overpass-api.de/api/interpreter")
      .addHeader("User-Agent", "DapurRantau/1.0 (Android; MarketFinder)")
      .post(formBody.toRequestBody(formMediaType))
      .build()

    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: ""

    if (!response.isSuccessful) {
      throw Exception("Overpass query failed with HTTP ${response.code}")
    }

    return parseOverpassJson(body, lat, lng)
  }

  private fun parseOverpassJson(jsonText: String, userLat: Double, userLng: Double): List<NearbyStore> {
    val result = mutableListOf<NearbyStore>()
    try {
      val root = JSONObject(jsonText)
      val elements = root.optJSONArray("elements") ?: return emptyList()

      for (i in 0 until elements.length()) {
        val el = elements.getJSONObject(i)
        val tags = el.optJSONObject("tags") ?: JSONObject()

        val rawName = tags.optString("name", "")
          .ifEmpty { tags.optString("name:en", "") }
          .ifEmpty { tags.optString("name:id", "") }

        val lat = if (el.has("lat")) el.optDouble("lat") else el.optJSONObject("center")?.optDouble("lat") ?: 0.0
        val lon = if (el.has("lon")) el.optDouble("lon") else el.optJSONObject("center")?.optDouble("lon") ?: 0.0

        if (lat == 0.0 && lon == 0.0) continue

        val amenity = tags.optString("amenity", "")
        val shop = tags.optString("shop", "")
        val lowerName = rawName.lowercase()

        // Categorize store strictly into the 4 requested types
        val storeType: StoreType = when {
          amenity == "marketplace" || lowerName.contains("pasar") || lowerName.contains("market") ->
            StoreType.FARMERS_MARKET // Traditional Market / Farmers Market

          shop == "convenience" || lowerName.contains("indomaret") || lowerName.contains("alfamart") ||
          lowerName.contains("family mart") || lowerName.contains("circle k") || lowerName.contains("7-eleven") ||
          lowerName.contains("ministop") || lowerName.contains("convenience") ->
            StoreType.MINIMARKET

          shop == "greengrocer" || shop == "general" || lowerName.contains("warung") || lowerName.contains("toko") ||
          lowerName.contains("sayur") ->
            StoreType.GROCERY_CONVENIENCE // Grocery / Warung

          else ->
            StoreType.SUPERMARKET
        }

        val displayName = rawName.ifEmpty {
          when (storeType) {
            StoreType.FARMERS_MARKET -> "Pasar Tradisional / Farmers Market"
            StoreType.MINIMARKET -> "Minimarket"
            StoreType.GROCERY_CONVENIENCE -> "Warung Sayur & Kelontong"
            StoreType.SUPERMARKET -> "Supermarket"
          }
        }

        // Distance strictly computed from real coordinates (never invented)
        val distKm = calculateHaversineDistanceKm(userLat, userLng, lat, lon)

        // Opening hours & phone ONLY if present in OSM tags (never invented)
        val rawHours = tags.optString("opening_hours", "").trim()
        val openingHours = if (rawHours.isNotEmpty()) rawHours else null

        val rawPhone = tags.optString("phone", "").ifEmpty { tags.optString("contact:phone", "") }.trim()
        val phone = if (rawPhone.isNotEmpty()) rawPhone else null

        val addressParts = mutableListOf<String>()
        val street = tags.optString("addr:street", "")
        val housenumber = tags.optString("addr:housenumber", "")
        val city = tags.optString("addr:city", "")
        if (street.isNotEmpty()) {
          addressParts.add(if (housenumber.isNotEmpty()) "$street No. $housenumber" else street)
        }
        if (city.isNotEmpty()) addressParts.add(city)
        val address = addressParts.joinToString(", ")

        val bestFor = when (storeType) {
          StoreType.FARMERS_MARKET -> listOf("Sayuran Segar & Tempe/Tahu", "Bumbu Rempah Basah", "Ikan & Ayam Pasar")
          StoreType.GROCERY_CONVENIENCE -> listOf("Bahan Pokok & Telur", "Minyak Goreng & Kecap", "Bumbu Dapur Harian")
          StoreType.MINIMARKET -> listOf("Telur & Roti", "Bumbu Instan & Minuman", "Kebutuhan Praktis")
          StoreType.SUPERMARKET -> listOf("Beras & Minyak Kemasan", "Daging & Susu", "Bahan Kering & Bumbu Lengkap")
        }

        result.add(
          NearbyStore(
            id = "osm-${el.optString("type")}-${el.optLong("id")}",
            name = displayName,
            type = storeType,
            address = address,
            distanceKm = distKm,
            latitude = lat,
            longitude = lon,
            openingHours = openingHours,
            phone = phone,
            bestForItems = bestFor,
            isSampleData = false
          )
        )
      }
    } catch (e: Exception) {
      Log.e("OsmApiClient", "Failed to parse Overpass json", e)
    }
    return result
  }

  companion object {
    fun calculateHaversineDistanceKm(
      lat1: Double, lon1: Double,
      lat2: Double, lon2: Double
    ): Double {
      val r = 6371.0 // Radius of Earth in kilometers
      val dLat = Math.toRadians(lat2 - lat1)
      val dLon = Math.toRadians(lon2 - lon1)
      val a = sin(dLat / 2.0).pow(2.0) +
              cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
              sin(dLon / 2.0).pow(2.0)
      val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
      return (r * c * 10.0).toInt() / 10.0 // 1 decimal precision
    }
  }
}
