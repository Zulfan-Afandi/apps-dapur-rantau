package com.example.data.model

enum class KitchenSetup(val labelId: String, val labelEn: String) {
  RICE_COOKER_ONLY("Hanya Rice Cooker", "Rice Cooker Only"),
  ONE_BURNER("Kompor 1 Tungku", "Single Burner Stove"),
  FULL_KITCHEN("Dapur Lengkap", "Full Kitchen")
}

enum class StoreType(val labelId: String, val labelEn: String, val emoji: String) {
  FARMERS_MARKET("Pasar Tradisional", "Traditional / Farmers Market", "🥬"),
  GROCERY_CONVENIENCE("Warung / Sayur", "Grocery / Warung", "🏪"),
  MINIMARKET("Minimarket", "Minimarket", "🛍️"),
  SUPERMARKET("Supermarket", "Supermarket", "🛒");

  companion object {
    val TRADITIONAL_MARKET get() = FARMERS_MARKET
    val GROCERY_WARUNG get() = GROCERY_CONVENIENCE
  }
}

enum class PriceConfidence(val labelId: String, val labelEn: String) {
  HIGH("Estimasi Akurat (Tinggi)", "High Confidence"),
  MEDIUM("Estimasi Wajar (Sedang)", "Medium Confidence"),
  LOW("Estimasi Kasar (Rendah)", "Rough Estimate (Low)")
}

enum class MascotState {
  IDLE,
  COOKING,
  HAPPY,
  WORRIED,
  WAVING
}

enum class AppLanguage {
  ID,
  EN
}

data class LocaleContext(
  val country: String = "Indonesia",
  val countryCode: String = "ID",
  val city: String = "Malang",
  val currency: String = "IDR",
  val currencySymbol: String = "Rp",
  val isMetric: Boolean = true,
  val language: AppLanguage = AppLanguage.ID,
  val latitude: Double = -7.9430,
  val longitude: Double = 112.6070,
  val cuisine: String = "Local"
) {
  fun formatPrice(amount: Double): String {
    return when (currency) {
      "IDR" -> "$currencySymbol ${String.format(java.util.Locale.US, "%,.0f", amount).replace(',', '.')}"
      "INR" -> "$currencySymbol ${String.format(java.util.Locale.US, "%,.0f", amount)}"
      "GBP" -> "$currencySymbol${String.format(java.util.Locale.US, "%.2f", amount)}"
      "USD" -> "$currencySymbol${String.format(java.util.Locale.US, "%.2f", amount)}"
      "EUR" -> "€${String.format(java.util.Locale.US, "%.2f", amount)}"
      else -> "$currencySymbol ${String.format(java.util.Locale.US, "%.2f", amount)}"
    }
  }

  fun formatDistance(km: Double): String {
    return if (isMetric) {
      if (km < 1.0) "${(km * 1000).toInt()} m" else "${String.format(java.util.Locale.US, "%.1f", km)} km"
    } else {
      val miles = km * 0.621371
      if (miles < 0.2) "${(miles * 5280).toInt()} ft" else "${String.format(java.util.Locale.US, "%.1f", miles)} mi"
    }
  }
}

data class DemoLocationPreset(
  val name: String,
  val country: String,
  val city: String,
  val currency: String,
  val currencySymbol: String,
  val latitude: Double,
  val longitude: Double,
  val cuisine: String,
  val cuisineEn: String = cuisine,
  val isMetric: Boolean,
  val language: AppLanguage,
  val defaultBudget: Double,
  val budgetPresets: List<Double>
)

object DemoLocations {
  val PRESETS = listOf(
    DemoLocationPreset(
      name = "Malang (Indonesia)",
      country = "Indonesia",
      city = "Malang",
      currency = "IDR",
      currencySymbol = "Rp",
      latitude = -7.9430,
      longitude = 112.6070,
      cuisine = "Indonesian (Tempe, Nasi, Sayur Segar)",
      cuisineEn = "Indonesian (Tempeh, Rice, Fresh Veggies)",
      isMetric = true,
      language = AppLanguage.ID,
      defaultBudget = 25000.0,
      budgetPresets = listOf(15000.0, 20000.0, 30000.0, 50000.0)
    )
  )
}

data class UserPreferences(
  val dailyBudget: Double = 25000.0,
  val kitchenSetup: KitchenSetup = KitchenSetup.ONE_BURNER,
  val mealsPerDay: Int = 3,
  val cuisineOverride: String = "Local",
  val isVegetarian: Boolean = false,
  val isVegan: Boolean = false,
  val isHalal: Boolean = false,
  val isNoPork: Boolean = false,
  val isNoBeef: Boolean = false,
  val allergies: String = "",
  val isOnboarded: Boolean = true,
  val batchCookPreferred: Boolean = true
)

data class Ingredient(
  val name: String,
  val nameEn: String = name,
  val amount: String,
  val amountEn: String = amount,
  val estPrice: Double,
  val category: StoreType,
  val confidence: PriceConfidence = PriceConfidence.MEDIUM
)

data class IngredientSub(
  val original: String,
  val substitute: String,
  val reasonId: String,
  val reasonEn: String,
  val estSavings: String
)

data class Meal(
  val id: String,
  val name: String,
  val nameEn: String,
  val mealType: String, // BREAKFAST, LUNCH, DINNER
  val description: String,
  val descriptionEn: String,
  val estimatedCost: Double,
  val proteinGrams: Int,
  val carbsGrams: Int,
  val veggiesGrams: Int,
  val approxCalories: Int,
  val toolsNeeded: List<String>,
  val prepTimeMinutes: Int,
  val isBatchCook: Boolean = false,
  val batchTip: String = "",
  val batchTipEn: String = batchTip,
  val ingredients: List<Ingredient>,
  val instructions: List<String>,
  val instructionsEn: List<String>,
  val substitutions: List<IngredientSub> = emptyList()
)

data class DayPlan(
  val dayNameId: String,
  val dayNameEn: String,
  val dayIndex: Int,
  val meals: List<Meal>,
  val dailyBudget: Double
) {
  val totalEstimatedCost: Double get() = meals.sumOf { it.estimatedCost }
  val totalProtein: Int get() = meals.sumOf { it.proteinGrams }
  val totalCarbs: Int get() = meals.sumOf { it.carbsGrams }
  val totalVeggies: Int get() = meals.sumOf { it.veggiesGrams }
  val isUnderBudget: Boolean get() = totalEstimatedCost <= dailyBudget
}

data class ShoppingItem(
  val id: String,
  val name: String,
  val nameEn: String = name,
  val category: StoreType,
  val quantity: String,
  val quantityEn: String = quantity,
  val estimatedPrice: Double,
  val actualPrice: Double? = null,
  val isChecked: Boolean = false,
  val confidence: PriceConfidence = PriceConfidence.MEDIUM,
  val notes: String = ""
) {
  val effectivePrice: Double get() = actualPrice ?: estimatedPrice
}

data class NearbyStore(
  val id: String,
  val name: String,
  val type: StoreType,
  val area: String = "",
  val address: String = "",
  val distanceKm: Double,
  val latitude: Double,
  val longitude: Double,
  val openingHours: String? = null,
  val phone: String? = null,
  val bestForItems: List<String> = emptyList(),
  val bestForItemsEn: List<String> = emptyList(),
  val isSampleData: Boolean = false
)

data class TanggalTuaPlan(
  val remainingBudget: Double,
  val daysLeft: Int,
  val dailyAllowance: Double,
  val meals: List<Meal>,
  val survivalTips: List<String>,
  val survivalTipsEn: List<String> = survivalTips
)

data class FridgeScanResult(
  val detectedIngredients: List<String>,
  val suggestedMeals: List<Meal>,
  val cheapMarketAdditions: List<String>,
  val cheapMarketAdditionsEn: List<String> = cheapMarketAdditions
)
