package com.example.data.gemini

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AppLanguage
import com.example.data.model.Ingredient
import com.example.data.model.IngredientSub
import com.example.data.model.KitchenSetup
import com.example.data.model.LocaleContext
import com.example.data.model.Meal
import com.example.data.model.PriceConfidence
import com.example.data.model.StoreType
import com.example.data.model.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiApiClient {

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  private fun getApiKey(): String {
    return try {
      BuildConfig.GEMINI_API_KEY.trim()
    } catch (e: Exception) {
      ""
    }
  }

  fun hasApiKey(): Boolean {
    val key = getApiKey()
    return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
  }

  suspend fun generateWeeklyMealPlan(
    preferences: UserPreferences,
    locale: LocaleContext
  ): Result<List<Meal>> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (!hasApiKey()) {
      return@withContext Result.failure(IllegalStateException("API_KEY_NOT_CONFIGURED"))
    }

    val model = "gemini-3.8-flash"
    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

    val dietaryNotes = buildList {
      if (preferences.isVegetarian) add("Strictly Vegetarian")
      if (preferences.isVegan) add("Strictly Vegan")
      if (preferences.isHalal) add("Halal certified/safe")
      if (preferences.isNoPork) add("No pork / lard")
      if (preferences.isNoBeef) add("No beef")
      if (preferences.allergies.isNotBlank()) add("Avoid allergies: ${preferences.allergies}")
    }.joinToString(", ")

    val effectiveCuisine = if (preferences.cuisineOverride != "Local") preferences.cuisineOverride else locale.cuisine
    val langInstruction = if (locale.language == AppLanguage.ID) "Tuliskan nama, deskripsi, dan langkah dalam Bahasa Indonesia." else "Write all names, descriptions, and instructions in English."
    val unitSystem = if (locale.isMetric) "Metric units (grams, kg, ml)" else "Imperial/Local customary units (oz, lbs, cups)"

    val prompt = """
      Act as an expert budget home cooking planner for single students and workers in ${locale.city}, ${locale.country}.
      Context:
      - Daily Food Budget: ${locale.formatPrice(preferences.dailyBudget)} (${locale.currency})
      - Kitchen Setup: ${preferences.kitchenSetup.labelEn}
      - Meals per day: ${preferences.mealsPerDay}
      - Preferred Regional Cuisine: $effectiveCuisine
      - Dietary requirements: ${if (dietaryNotes.isEmpty()) "None" else dietaryNotes}
      - Unit System: $unitSystem
      - Language: $langInstruction

      Create a diverse 7-meal realistic weekly plan using ingredients easily and cheaply found in local markets/supermarkets in ${locale.city}.
      Each meal must have realistic estimated prices in ${locale.currency}, protein/carbs/veggies breakdown, and a cost-saving substitution when primary protein prices spike.

      Output STRICT JSON array format:
      [
        {
          "name": "Meal name",
          "nameEn": "English meal name",
          "mealType": "LUNCH",
          "description": "Short appetizing description",
          "descriptionEn": "English description",
          "estimatedCost": 15.0,
          "proteinGrams": 22,
          "carbsGrams": 45,
          "veggiesGrams": 40,
          "approxCalories": 420,
          "toolsNeeded": ["Pan", "Knife"],
          "prepTimeMinutes": 15,
          "isBatchCook": true,
          "batchTip": "Can be cooked in bulk for 3 servings",
          "ingredients": [
            {
              "name": "Ingredient name",
              "nameEn": "Ingredient in English",
              "amount": "200g",
              "estPrice": 5.0,
              "category": "FARMERS_MARKET",
              "confidence": "HIGH"
            }
          ],
          "instructions": ["Step 1", "Step 2"],
          "instructionsEn": ["Step 1", "Step 2"],
          "substitutions": [
            {
              "original": "Chicken",
              "substitute": "Lentils / Eggs",
              "reasonId": "Protein sama, jauh lebih hemat",
              "reasonEn": "Maintains identical protein, 40% cheaper",
              "estSavings": "Save 30%"
            }
          ]
        }
      ]
    """.trimIndent()

    try {
      val requestJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
            })
          })
        })
        put("generationConfig", JSONObject().apply {
          put("responseMimeType", "application/json")
          put("temperature", 0.4)
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(requestJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
      }

      val responseObj = JSONObject(body)
      val text = responseObj
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")

      val meals = parseMealsFromJson(text)
      Result.success(meals)
    } catch (e: Exception) {
      Log.e("GeminiApiClient", "Failed to generate meal plan", e)
      Result.failure(e)
    }
  }

  suspend fun scanFridgeAndSuggest(
    bitmap: Bitmap,
    kitchenSetup: KitchenSetup,
    locale: LocaleContext
  ): Result<Pair<List<String>, List<Meal>>> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (!hasApiKey()) {
      return@withContext Result.failure(IllegalStateException("API_KEY_NOT_CONFIGURED"))
    }

    val model = "gemini-3.8-flash"
    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
    val base64Image = bitmapToBase64(bitmap)

    val prompt = """
      Look at this photo of ingredients in a dorm or home pantry in ${locale.city}, ${locale.country}.
      Recognize what items are visible.
      Then create 3 simple, nutritious recipes using these ingredients with ${kitchenSetup.labelEn}.
      Return STRICT JSON:
      {
        "detectedIngredients": ["Item 1", "Item 2"],
        "recipes": [
          {
            "name": "Recipe name",
            "nameEn": "Recipe name in English",
            "mealType": "DINNER",
            "description": "Short description",
            "descriptionEn": "Description",
            "estimatedCost": 10.0,
            "proteinGrams": 15,
            "carbsGrams": 40,
            "veggiesGrams": 20,
            "approxCalories": 360,
            "toolsNeeded": ["Pan"],
            "prepTimeMinutes": 10,
            "isBatchCook": false,
            "batchTip": "",
            "ingredients": [
              {"name": "Item", "nameEn": "Item", "amount": "1 serving", "estPrice": 2.0, "category": "GROCERY_CONVENIENCE", "confidence": "HIGH"}
            ],
            "instructions": ["Step 1", "Step 2"],
            "instructionsEn": ["Step 1", "Step 2"],
            "substitutions": []
          }
        ]
      }
    """.trimIndent()

    try {
      val requestJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
              put(JSONObject().apply {
                put("inlineData", JSONObject().apply {
                  put("mimeType", "image/jpeg")
                  put("data", base64Image)
                })
              })
            })
          })
        })
        put("generationConfig", JSONObject().apply {
          put("responseMimeType", "application/json")
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(requestJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
      }

      val responseObj = JSONObject(body)
      val text = responseObj
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")

      val json = JSONObject(text)
      val detectedList = mutableListOf<String>()
      val detectedArray = json.optJSONArray("detectedIngredients") ?: JSONArray()
      for (i in 0 until detectedArray.length()) {
        detectedList.add(detectedArray.getString(i))
      }

      val recipesArray = json.optJSONArray("recipes") ?: JSONArray()
      val meals = parseMealsFromJsonArray(recipesArray)

      Result.success(Pair(detectedList, meals))
    } catch (e: Exception) {
      Log.e("GeminiApiClient", "Fridge scan error", e)
      Result.failure(e)
    }
  }

  suspend fun generateTanggalTuaPlan(
    remainingBudget: Double,
    daysLeft: Int,
    kitchenSetup: KitchenSetup,
    locale: LocaleContext
  ): Result<List<Meal>> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (!hasApiKey()) {
      return@withContext Result.failure(IllegalStateException("API_KEY_NOT_CONFIGURED"))
    }

    val dailyBudget = if (daysLeft > 0) remainingBudget / daysLeft else remainingBudget
    val model = "gemini-3.1-flash-lite"
    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

    val prompt = """
      Emergency End-of-the-month ultra budget menu planner for students in ${locale.city}, ${locale.country}.
      Remaining money: ${locale.formatPrice(remainingBudget)} for $daysLeft days (Daily limit: ${locale.formatPrice(dailyBudget)}).
      Kitchen: ${kitchenSetup.labelEn}.
      Provide 4 ultra-cheap, highly nutritious, comforting meals using cheap local staples in ${locale.currency}.
      Format STRICT JSON array of Meal objects.
    """.trimIndent()

    try {
      val requestJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
            })
          })
        })
        put("generationConfig", JSONObject().apply {
          put("responseMimeType", "application/json")
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(requestJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
      }

      val responseObj = JSONObject(body)
      val text = responseObj
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")

      val meals = parseMealsFromJson(text)
      Result.success(meals)
    } catch (e: Exception) {
      Log.e("GeminiApiClient", "Tanggal tua generation error", e)
      Result.failure(e)
    }
  }

  private fun parseMealsFromJson(jsonText: String): List<Meal> {
    val clean = jsonText.trim()
    val array = if (clean.startsWith("[")) {
      JSONArray(clean)
    } else if (clean.startsWith("{")) {
      val obj = JSONObject(clean)
      obj.optJSONArray("meals") ?: obj.optJSONArray("recipes") ?: JSONArray()
    } else {
      JSONArray()
    }
    return parseMealsFromJsonArray(array)
  }

  private fun parseMealsFromJsonArray(array: JSONArray): List<Meal> {
    val meals = mutableListOf<Meal>()
    for (i in 0 until array.length()) {
      val obj = array.getJSONObject(i)
      val mealType = obj.optString("mealType", "LUNCH")

      val ingredientsList = mutableListOf<Ingredient>()
      val ingArray = obj.optJSONArray("ingredients") ?: JSONArray()
      for (j in 0 until ingArray.length()) {
        val ingObj = ingArray.getJSONObject(j)
        val storeTypeStr = ingObj.optString("category", "FARMERS_MARKET").uppercase()
        val storeType = when {
          storeTypeStr.contains("FARMER") || storeTypeStr.contains("PASAR") -> StoreType.FARMERS_MARKET
          storeTypeStr.contains("GROCERY") || storeTypeStr.contains("WARUNG") -> StoreType.GROCERY_CONVENIENCE
          else -> StoreType.SUPERMARKET
        }
        val confStr = ingObj.optString("confidence", "MEDIUM").uppercase()
        val confidence = try {
          PriceConfidence.valueOf(confStr)
        } catch (e: Exception) {
          PriceConfidence.MEDIUM
        }

        ingredientsList.add(
          Ingredient(
            name = ingObj.optString("name", "Ingredient"),
            nameEn = ingObj.optString("nameEn", ingObj.optString("name", "Ingredient")),
            amount = ingObj.optString("amount", "1 portion"),
            estPrice = ingObj.optDouble("estPrice", 5.0),
            category = storeType,
            confidence = confidence
          )
        )
      }

      val instructionsList = mutableListOf<String>()
      val insArray = obj.optJSONArray("instructions") ?: JSONArray()
      for (k in 0 until insArray.length()) {
        instructionsList.add(insArray.getString(k))
      }

      val instructionsEnList = mutableListOf<String>()
      val insEnArray = obj.optJSONArray("instructionsEn") ?: JSONArray()
      for (k in 0 until insEnArray.length()) {
        instructionsEnList.add(insEnArray.getString(k))
      }

      val toolsList = mutableListOf<String>()
      val toolsArray = obj.optJSONArray("toolsNeeded") ?: JSONArray()
      for (k in 0 until toolsArray.length()) {
        toolsList.add(toolsArray.getString(k))
      }

      val subsList = mutableListOf<IngredientSub>()
      val subsArray = obj.optJSONArray("substitutions") ?: JSONArray()
      for (s in 0 until subsArray.length()) {
        val sObj = subsArray.getJSONObject(s)
        subsList.add(
          IngredientSub(
            original = sObj.optString("original", "Meat"),
            substitute = sObj.optString("substitute", "Lentils / Eggs"),
            reasonId = sObj.optString("reasonId", "Sama-sama protein, harga hemat"),
            reasonEn = sObj.optString("reasonEn", "High protein, lower cost"),
            estSavings = sObj.optString("estSavings", "Save 30%")
          )
        )
      }

      meals.add(
        Meal(
          id = UUID.randomUUID().toString(),
          name = obj.optString("name", "Budget Meal"),
          nameEn = obj.optString("nameEn", "Budget Meal"),
          mealType = mealType,
          description = obj.optString("description", "Nutritious and budget-friendly."),
          descriptionEn = obj.optString("descriptionEn", "Nutritious and budget-friendly."),
          estimatedCost = obj.optDouble("estimatedCost", 8.0),
          proteinGrams = obj.optInt("proteinGrams", 15),
          carbsGrams = obj.optInt("carbsGrams", 40),
          veggiesGrams = obj.optInt("veggiesGrams", 25),
          approxCalories = obj.optInt("approxCalories", 380),
          toolsNeeded = if (toolsList.isEmpty()) listOf("Pan") else toolsList,
          prepTimeMinutes = obj.optInt("prepTimeMinutes", 15),
          isBatchCook = obj.optBoolean("isBatchCook", false),
          batchTip = obj.optString("batchTip", ""),
          ingredients = ingredientsList,
          instructions = if (instructionsList.isEmpty()) listOf("Prepare ingredients", "Cook until tender", "Serve warm") else instructionsList,
          instructionsEn = if (instructionsEnList.isEmpty()) listOf("Prepare ingredients", "Cook until tender", "Serve warm") else instructionsEnList,
          substitutions = subsList
        )
      )
    }
    return meals
  }

  private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }

  suspend fun generateStoreShoppingAdvice(
    storeTypeLabel: String,
    shoppingItems: List<String>,
    language: AppLanguage
  ): Result<String> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (!hasApiKey()) {
      return@withContext Result.failure(IllegalStateException("API_KEY_NOT_CONFIGURED"))
    }
    val model = "gemini-3.8-flash"
    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

    val itemsList = if (shoppingItems.isEmpty()) "None" else shoppingItems.joinToString(", ")
    val langPrompt = if (language == AppLanguage.ID) "Tuliskan dalam Bahasa Indonesia" else "Write in English"
    val prompt = """
      You are a helpful culinary shopping assistant for Indonesian home cooking.
      Store type: $storeTypeLabel
      User's current shopping list: $itemsList

      In 1 to 2 short sentences, concisely list which items from the user's shopping list above are best or usually purchased at this store type ($storeTypeLabel), along with one quick practical tip.
      RULES:
      - NEVER invent any specific store names, ratings, opening hours, or distances.
      - Only mention items from the user's shopping list if any match. If none match, politely suggest what usually fits this store type.
      - $langPrompt. Keep it under 40 words.
    """.trimIndent()

    try {
      val requestJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
            })
          })
        })
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.3)
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(requestJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: ""
      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP ${response.code}"))
      }
      val responseObj = JSONObject(body)
      val text = responseObj
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")
        .trim()
      Result.success(text)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

