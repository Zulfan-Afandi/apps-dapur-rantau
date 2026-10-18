package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DemoLocationPreset
import com.example.data.model.LocaleContext
import com.example.data.model.MascotState
import com.example.data.model.NearbyStore
import com.example.data.model.ShoppingItem
import com.example.data.model.StoreType
import com.example.ui.components.KukiMascot
import com.example.ui.components.MarketRadarView
import com.example.ui.theme.AppStrings

@Composable
fun MapNearbyScreen(
  stores: List<NearbyStore>,
  shoppingItems: List<ShoppingItem>,
  localeContext: LocaleContext,
  language: AppLanguage,
  selectedStoreFilter: StoreType?,
  selectedStoreDetail: NearbyStore?,
  showSearchAreaButton: Boolean,
  isSearchingPlaces: Boolean,
  mapErrorMessage: String? = null,
  isUsingSampleData: Boolean = false,
  geolocationDenied: Boolean = false,
  mascotState: MascotState = MascotState.IDLE,
  onSetStoreFilter: (StoreType?) -> Unit,
  onSelectStore: (NearbyStore?) -> Unit,
  onStoreSelectedById: (String) -> Unit,
  onMapMoved: (Double, Double) -> Unit,
  onSearchCurrentArea: () -> Unit,
  onSearchQuery: (String) -> Unit,
  onApplyPreset: (DemoLocationPreset) -> Unit,
  onStoresDiscovered: (List<NearbyStore>) -> Unit = {},
  onGeolocationDenied: () -> Unit = {},
  onUserLocationFound: (Double, Double) -> Unit = { _, _ -> },
  onRetry: () -> Unit = {},
  onFallbackToSample: () -> Unit = {}
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var isScanningLocal by remember { mutableStateOf(false) }

  // Search filter and category filter
  val filteredStores = remember(stores, selectedStoreFilter, searchQuery) {
    var result = if (selectedStoreFilter == null) {
      stores
    } else {
      stores.filter { it.type == selectedStoreFilter }
    }
    if (searchQuery.isNotBlank()) {
      val q = searchQuery.trim().lowercase()
      result = result.filter {
        it.name.lowercase().contains(q) ||
        it.area.lowercase().contains(q) ||
        it.address.lowercase().contains(q)
      }
    }
    result
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Location Bar & Search Box
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Search Input Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                  Text(
                    text = if (language == AppLanguage.ID) "Cari nama pasar / warung di Malang..." else "Search market or grocery in Malang...",
                    fontSize = 13.sp
                  )
                },
                leadingIcon = {
                  Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280))
                },
                trailingIcon = {
                  if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                      Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF9CA3AF))
                    }
                  }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                  if (searchQuery.isNotBlank()) onSearchQuery(searchQuery)
                }),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("map_search_input")
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Location Coordinates & Action
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📍 ${localeContext.city}, ${localeContext.country}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1F2937)
                )
              )

              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFF3F4F6))
                  .clickable {
                    isScanningLocal = true
                    onSearchCurrentArea()
                  }
                  .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.MyLocation,
                  contentDescription = null,
                  tint = Color(0xFF0284C7),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (language == AppLanguage.ID) "Pindai Area" else "Scan Area",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7)
                  )
                )
              }
            }
          }
        }
      }

      // 2. Native Compose Radar Scanner (Replaces fragile WebView)
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(24.dp))
        ) {
          MarketRadarView(
            centerLatitude = localeContext.latitude,
            centerLongitude = localeContext.longitude,
            stores = filteredStores,
            selectedStore = selectedStoreDetail,
            isScanning = isSearchingPlaces || isScanningLocal,
            language = language,
            onStoreSelected = { onSelectStore(it) },
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      // 3. Dataset Attribution Banner (Strictly labeled as "Curated demo data (Malang)")
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFEF3C7))
            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "🏷️", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Curated demo data (Malang)",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
              )
            )
            Text(
              text = if (language == AppLanguage.ID)
                "12 titik pasar tradisional, warung sayur, minimarket & supermarket di Kota Malang."
              else
                "12 curated traditional markets, warungs, minimarkets & supermarkets in Malang.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFB45309),
                fontSize = 11.sp
              )
            )
          }
        }
      }

      // 4. Store Type Filter Chips
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            val isAll = selectedStoreFilter == null
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isAll) Color(0xFFD32F2F) else Color.White)
                .border(1.dp, if (isAll) Color(0xFFD32F2F) else Color(0xFFE7E0D3), RoundedCornerShape(12.dp))
                .clickable { onSetStoreFilter(null) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = if (language == AppLanguage.ID) "Semua Tempat (${stores.size})" else "All Places (${stores.size})",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isAll) Color.White else Color(0xFF292524)
                )
              )
            }
          }

          items(StoreType.entries.toTypedArray()) { type ->
            val isSelected = selectedStoreFilter == type
            val count = stores.count { it.type == type }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color(0xFFD32F2F) else Color.White)
                .border(1.dp, if (isSelected) Color(0xFFD32F2F) else Color(0xFFE7E0D3), RoundedCornerShape(12.dp))
                .clickable { onSetStoreFilter(type) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = "${type.emoji} ${if (language == AppLanguage.ID) type.labelId else type.labelEn} ($count)",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Color(0xFF292524)
                )
              )
            }
          }
        }
      }

      // 5. Search Nearby in Google Maps Button (For locations outside dataset)
      item {
        val storeTypeLabel = selectedStoreFilter?.let {
          if (language == AppLanguage.ID) it.labelId else it.labelEn
        } ?: if (language == AppLanguage.ID) "Pasar & Supermarket" else "Markets & Grocery"

        OutlinedButton(
          onClick = {
            val query = Uri.encode("$storeTypeLabel ${localeContext.city}")
            val gmapsUrl = "https://www.google.com/maps/search/?api=1&query=$query"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(gmapsUrl)))
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0284C7)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_nearby_gmaps_button")
        ) {
          Icon(imageVector = Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (language == AppLanguage.ID)
              "Cari '$storeTypeLabel' di Google Maps (Di Luar Dataset)"
            else
              "Search nearby in Google Maps (Outside dataset)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 6. List Header & Count
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (language == AppLanguage.ID) "Daftar Tempat Belanja:" else "Market List:",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            )
            Text(
              text = "${filteredStores.size} ${if (language == AppLanguage.ID) "lokasi diurutkan dari jarak terdekat" else "locations sorted by proximity"}",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280), fontSize = 11.sp)
            )
          }

          // Mascot Mini Reaction Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White)
              .border(1.dp, Color(0xFFE7E0D3), RoundedCornerShape(14.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            KukiMascot(
              state = mascotState,
              size = 28.dp,
              isPaused = false
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.ID) "Siap Belanja" else "Ready",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF4B5563)
              )
            )
          }
        }
      }

      // 7. Store Cards (Sorted by Real Distance)
      items(filteredStores) { store ->
        StoreCardItem(
          store = store,
          localeContext = localeContext,
          language = language,
          onCardClick = { onSelectStore(store) },
          onOpenDirections = {
            // Name-based search query in Google Maps as required
            val query = Uri.encode("${store.name}, ${localeContext.city}")
            val mapUrl = "https://www.google.com/maps/search/?api=1&query=$query"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl)))
          }
        )
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // 8. Bottom Sheet for Marker / Store Click
    if (selectedStoreDetail != null) {
      val store = selectedStoreDetail
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.5f))
          .clickable { onSelectStore(null) }
      ) {
        Card(
          shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .clickable(enabled = false) {}
            .testTag("store_bottom_sheet")
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Emoji, Name, Close
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = store.type.emoji, fontSize = 30.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                  )
                  if (store.address.isNotEmpty()) {
                    Text(
                      text = store.address,
                      style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)),
                      maxLines = 2
                    )
                  }
                  Text(
                    text = "🏷️ Curated demo data (Malang)",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = Color(0xFFB45309),
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  )
                }
              }
              IconButton(onClick = { onSelectStore(null) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real Distance & Attributes (Show "Typical hours" only where confident, otherwise hide!)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F4F6))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📍 ${localeContext.formatDistance(store.distanceKm)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
              )

              // Show Typical hours only if present
              if (!store.openingHours.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(15.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (language == AppLanguage.ID)
                      "Jam buka: ${AppStrings.formatHours(store.openingHours, language)}"
                    else
                      "Typical hours: ${AppStrings.formatHours(store.openingHours, language)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D), fontSize = 11.sp),
                    maxLines = 1
                  )
                }
              }

              // Phone if present
              if (!store.phone.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = store.phone,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7), fontSize = 11.sp),
                    maxLines = 1
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Best for items
            Text(
              text = if (language == AppLanguage.ID) "Bahan yang Cocok Dibeli di Sini:" else "Items to Buy Here:",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF374151))
            )
            Spacer(modifier = Modifier.height(6.dp))

            val bestItems = if (language == AppLanguage.ID) {
              if (store.bestForItems.isNotEmpty()) store.bestForItems
              else {
                val matches = shoppingItems.filter { it.category == store.type }.map { it.name }
                if (matches.isNotEmpty()) matches.take(3) else listOf("Bahan Masakan Harian", "Sayur / Bumbu", "Kebutuhan Dapur")
              }
            } else {
              if (store.bestForItemsEn.isNotEmpty()) store.bestForItemsEn
              else if (store.bestForItems.isNotEmpty()) store.bestForItems
              else {
                val matches = shoppingItems.filter { it.category == store.type }.map { it.nameEn }
                if (matches.isNotEmpty()) matches.take(3) else listOf("Daily Cooking Essentials", "Fresh Veggies / Spices", "Kitchen Staples")
              }
            }

            bestItems.forEach { item ->
              Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("✓", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item, style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1F2937)))
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Directions Button (Opening https://www.google.com/maps/search/?api=1&query=<store name + city>)
            Button(
              onClick = {
                val query = Uri.encode("${store.name}, ${localeContext.city}")
                val mapUrl = "https://www.google.com/maps/search/?api=1&query=$query"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                context.startActivity(browserIntent)
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("directions_button")
            ) {
              Icon(imageVector = Icons.Default.Directions, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = AppStrings.directions(language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun StoreCardItem(
  store: NearbyStore,
  localeContext: LocaleContext,
  language: AppLanguage,
  onCardClick: () -> Unit,
  onOpenDirections: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("store_card_${store.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Text(text = store.type.emoji, fontSize = 24.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = store.name,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
            if (store.area.isNotEmpty() || store.address.isNotEmpty()) {
              Text(
                text = if (store.area.isNotEmpty()) "${store.area} • ${store.address}" else store.address,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280), fontSize = 11.sp),
                maxLines = 1
              )
            }
            Text(
              text = "Curated demo data (Malang)",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFB45309),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        // Real distance badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = localeContext.formatDistance(store.distanceKm),
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB45309)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Attributes: Show Typical hours only where confident, otherwise hide!
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (!store.openingHours.isNullOrBlank()) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = if (language == AppLanguage.ID)
                "Jam buka: ${AppStrings.formatHours(store.openingHours, language)}"
              else
                "Typical hours: ${AppStrings.formatHours(store.openingHours, language)}",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D), fontSize = 11.sp),
              maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          if (!store.phone.isNullOrBlank()) {
            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = store.phone,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7), fontSize = 11.sp),
              maxLines = 1
            )
          }
        }

        Button(
          onClick = onOpenDirections,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
          modifier = Modifier.height(34.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Directions,
            contentDescription = null,
            tint = Color(0xFF1F2937),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = AppStrings.routeShort(language),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937), fontSize = 11.sp)
          )
        }
      }
    }
  }
}
