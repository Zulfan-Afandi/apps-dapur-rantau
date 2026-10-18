package com.example.data.repository

import com.example.data.maps.OsmApiClient
import com.example.data.model.DayPlan
import com.example.data.model.DemoLocationPreset
import com.example.data.model.DemoLocations
import com.example.data.model.Ingredient
import com.example.data.model.IngredientSub
import com.example.data.model.KitchenSetup
import com.example.data.model.LocaleContext
import com.example.data.model.Meal
import com.example.data.model.NearbyStore
import com.example.data.model.PriceConfidence
import com.example.data.model.ShoppingItem
import com.example.data.model.StoreType
import com.example.data.model.TanggalTuaPlan
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class DapurRepository {

  private val _localeContext = MutableStateFlow(
    LocaleContext(
      country = "Indonesia",
      countryCode = "ID",
      city = "Malang",
      currency = "IDR",
      currencySymbol = "Rp",
      isMetric = true,
      latitude = -7.9430,
      longitude = 112.6070,
      cuisine = "Indonesian (Tempe, Nasi, Sayur Segar)"
    )
  )
  val localeContext: StateFlow<LocaleContext> = _localeContext.asStateFlow()

  private val _preferences = MutableStateFlow(
    UserPreferences(
      dailyBudget = 25000.0,
      kitchenSetup = KitchenSetup.ONE_BURNER,
      mealsPerDay = 3,
      cuisineOverride = "Local",
      isOnboarded = true,
      batchCookPreferred = true
    )
  )
  val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

  private val _weeklyPlan = MutableStateFlow<List<DayPlan>>(emptyList())
  val weeklyPlan: StateFlow<List<DayPlan>> = _weeklyPlan.asStateFlow()

  private val _shoppingItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
  val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems.asStateFlow()

  private val _nearbyStores = MutableStateFlow<List<NearbyStore>>(emptyList())
  val nearbyStores: StateFlow<List<NearbyStore>> = _nearbyStores.asStateFlow()

  private val _tanggalTuaPlan = MutableStateFlow<TanggalTuaPlan?>(null)
  val tanggalTuaPlan: StateFlow<TanggalTuaPlan?> = _tanggalTuaPlan.asStateFlow()

  init {
    loadPresetData(DemoLocations.PRESETS[0]) // Default: Malang
  }

  fun updatePreferences(newPrefs: UserPreferences) {
    _preferences.value = newPrefs
  }

  fun updateLocaleContext(newLocale: LocaleContext) {
    _localeContext.value = newLocale
    // Recompute distances for existing stores
    _nearbyStores.value = _nearbyStores.value.map { store ->
      store.copy(
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(
          newLocale.latitude, newLocale.longitude,
          store.latitude, store.longitude
        )
      )
    }.sortedBy { it.distanceKm }
  }

  fun applyDemoPreset(preset: DemoLocationPreset) {
    _localeContext.value = LocaleContext(
      country = preset.country,
      countryCode = if (preset.currency == "IDR") "ID" else if (preset.currency == "INR") "IN" else "GB",
      city = preset.city,
      currency = preset.currency,
      currencySymbol = preset.currencySymbol,
      isMetric = preset.isMetric,
      latitude = preset.latitude,
      longitude = preset.longitude,
      cuisine = preset.cuisine
    )
    _preferences.value = _preferences.value.copy(
      dailyBudget = preset.defaultBudget,
      cuisineOverride = "Local"
    )
    loadPresetData(preset)
  }

  fun setNearbyStores(stores: List<NearbyStore>) {
    _nearbyStores.value = stores.sortedBy { it.distanceKm }
  }

  fun setWeeklyPlan(plan: List<DayPlan>) {
    _weeklyPlan.value = plan
    rebuildShoppingListFromPlan(plan)
  }

  fun toggleShoppingItem(itemId: String) {
    _shoppingItems.value = _shoppingItems.value.map {
      if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it
    }
  }

  fun updateActualPrice(itemId: String, actualPrice: Double?) {
    _shoppingItems.value = _shoppingItems.value.map {
      if (it.id == itemId) it.copy(actualPrice = actualPrice) else it
    }
  }

  fun addCustomShoppingItem(name: String, quantity: String, estPrice: Double, storeType: StoreType) {
    val newItem = ShoppingItem(
      id = UUID.randomUUID().toString(),
      name = name,
      category = storeType,
      quantity = quantity,
      estimatedPrice = estPrice,
      confidence = PriceConfidence.HIGH
    )
    _shoppingItems.value = _shoppingItems.value + newItem
  }

  fun deleteShoppingItem(itemId: String) {
    _shoppingItems.value = _shoppingItems.value.filterNot { it.id == itemId }
  }

  fun setTanggalTuaPlan(plan: TanggalTuaPlan) {
    _tanggalTuaPlan.value = plan
  }

  private fun loadPresetData(preset: DemoLocationPreset) {
    val meals = getMealsForPreset(preset)
    val days = listOf(
      Pair("Senin", "Monday"),
      Pair("Selasa", "Tuesday"),
      Pair("Rabu", "Wednesday"),
      Pair("Kamis", "Thursday"),
      Pair("Jumat", "Friday"),
      Pair("Sabtu", "Saturday"),
      Pair("Minggu", "Sunday")
    )

    val plans = days.mapIndexed { index, (idDay, enDay) ->
      val s = (index * 3) % meals.size
      val dayMeals = listOf(
        meals[s % meals.size].copy(mealType = "BREAKFAST"),
        meals[(s + 1) % meals.size].copy(mealType = "LUNCH"),
        meals[(s + 2) % meals.size].copy(mealType = "DINNER")
      )
      DayPlan(
        dayNameId = idDay,
        dayNameEn = enDay,
        dayIndex = index,
        meals = dayMeals,
        dailyBudget = preset.defaultBudget
      )
    }

    _weeklyPlan.value = plans
    _nearbyStores.value = getSampleStoresForPreset(preset)
    rebuildShoppingListFromPlan(plans)
    _tanggalTuaPlan.value = getSampleTanggalTuaForPreset(preset)
  }

  private fun rebuildShoppingListFromPlan(plans: List<DayPlan>) {
    val itemMap = mutableMapOf<String, ShoppingItem>()
    plans.forEach { day ->
      day.meals.forEach { meal ->
        meal.ingredients.forEach { ing ->
          if (!itemMap.containsKey(ing.name)) {
            itemMap[ing.name] = ShoppingItem(
              id = UUID.randomUUID().toString(),
              name = ing.name,
              nameEn = ing.nameEn,
              category = ing.category,
              quantity = ing.amount,
              quantityEn = ing.amountEn,
              estimatedPrice = ing.estPrice,
              confidence = ing.confidence
            )
          }
        }
      }
    }
    _shoppingItems.value = itemMap.values.toList()
  }

  private fun getMealsForPreset(preset: DemoLocationPreset): List<Meal> {
    return when (preset.city) {
      "Mumbai" -> listOf(
        Meal(
          id = "in-1",
          name = "Dal Tadka & Steamed Jeera Rice",
          nameEn = "Yellow Lentil Tadka with Jeera Rice",
          mealType = "LUNCH",
          description = "Creamy toor dal tempered with cumin, garlic, and fresh green chilies with aromatic basmati rice.",
          descriptionEn = "Classic comforting protein-rich yellow lentils tempered with cumin seeds and garlic.",
          estimatedCost = 65.0,
          proteinGrams = 18,
          carbsGrams = 55,
          veggiesGrams = 20,
          approxCalories = 410,
          toolsNeeded = listOf("Pressure Cooker / Pot"),
          prepTimeMinutes = 20,
          isBatchCook = true,
          batchTip = "Dal can be prepared in double batch and stays fresh for 3 days in the fridge.",
          ingredients = listOf(
            Ingredient(name = "Toor / Moong Dal 150g", nameEn = "Yellow Lentils", amount = "150g", estPrice = 25.0, category = StoreType.GROCERY_CONVENIENCE, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Jeera & Aromatics", nameEn = "Cumin & Garlic", amount = "1 pack", estPrice = 10.0, category = StoreType.FARMERS_MARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Rice 200g", nameEn = "Rice", amount = "200g", estPrice = 15.0, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH)
          ),
          instructions = listOf("Wash dal and boil with turmeric and salt.", "Temper cumin, garlic, and chilies in oil.", "Pour tempering into dal and serve with hot rice."),
          instructionsEn = listOf("Wash dal and boil with turmeric and salt.", "Temper cumin, garlic, and chilies in oil.", "Pour tempering into dal and serve with hot rice."),
          substitutions = listOf(
            IngredientSub("Paneer", "Moong Dal", "Lentils are 60% cheaper with identical protein", "Lentils are 60% cheaper with identical protein", "Save ₹40")
          )
        ),
        Meal(
          id = "in-2",
          name = "Egg Bhurji with Warm Chapati",
          nameEn = "Spiced Scrambled Eggs with Flatbread",
          mealType = "DINNER",
          description = "Spicy Mumbai street-style scrambled eggs with chopped onions, tomatoes, coriander and wheat rotis.",
          descriptionEn = "Spicy Mumbai street-style scrambled eggs with chopped onions, tomatoes, and wheat rotis.",
          estimatedCost = 55.0,
          proteinGrams = 16,
          carbsGrams = 42,
          veggiesGrams = 25,
          approxCalories = 380,
          toolsNeeded = listOf("Tawa / Frying Pan"),
          prepTimeMinutes = 10,
          isBatchCook = false,
          batchTip = "",
          ingredients = listOf(
            Ingredient(name = "2 Eggs", nameEn = "2 Eggs", amount = "2 pcs", estPrice = 16.0, category = StoreType.GROCERY_CONVENIENCE, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Onion, Tomato, Green Chili", nameEn = "Fresh Veggies", amount = "1 set", estPrice = 15.0, category = StoreType.FARMERS_MARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Atta Flour (for 3 Rotis)", nameEn = "Wheat Flour", amount = "100g", estPrice = 12.0, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH)
          ),
          instructions = listOf("Sauté onions and chilies until golden.", "Whisk 2 eggs and pour into pan.", "Scramble gently and serve with warm rotis."),
          instructionsEn = listOf("Sauté onions and chilies until golden.", "Whisk 2 eggs and pour into pan.", "Scramble gently and serve with warm rotis.")
        ),
        Meal(
          id = "in-3",
          name = "Aloo Gobi Matar Home Curry",
          nameEn = "Potato, Cauliflower & Green Peas Curry",
          mealType = "LUNCH",
          description = "Homestyle dry vegetable curry rich in fiber and vitamin C, paired with steamed rice or phulkas.",
          descriptionEn = "Nutritious spiced potato and cauliflower sabzi with green peas.",
          estimatedCost = 50.0,
          proteinGrams = 12,
          carbsGrams = 48,
          veggiesGrams = 60,
          approxCalories = 340,
          toolsNeeded = listOf("Pan"),
          prepTimeMinutes = 15,
          isBatchCook = true,
          batchTip = "Cook 2 portions at night to pack for next day's lunch.",
          ingredients = listOf(
            Ingredient(name = "Cauliflower 1 small", nameEn = "Cauliflower", amount = "1 pc", estPrice = 20.0, category = StoreType.FARMERS_MARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Potatoes & Peas", nameEn = "Potatoes & Peas", amount = "150g", estPrice = 18.0, category = StoreType.FARMERS_MARKET, confidence = PriceConfidence.HIGH)
          ),
          instructions = listOf("Cut florets and dice potatoes.", "Sauté spices and vegetables covered on low heat for 12 mins.", "Garnish with fresh coriander."),
          instructionsEn = listOf("Cut florets and dice potatoes.", "Sauté spices and vegetables covered on low heat for 12 mins.", "Garnish with fresh coriander.")
        )
      )
      "London" -> listOf(
        Meal(
          id = "uk-1",
          name = "Jacket Potato with Baked Beans & Cheddar",
          nameEn = "Crispy Jacket Potato with Beans and Cheese",
          mealType = "LUNCH",
          description = "Oven or microwave fluffy jacket potato topped with warm baked beans in tomato sauce and mature cheddar.",
          descriptionEn = "Classic affordable British staple packed with high fiber and vegetarian protein.",
          estimatedCost = 1.95,
          proteinGrams = 19,
          carbsGrams = 62,
          veggiesGrams = 30,
          approxCalories = 430,
          toolsNeeded = listOf("Microwave / Oven"),
          prepTimeMinutes = 12,
          isBatchCook = false,
          batchTip = "Microwave potato for 8 mins, finish 2 mins under grill for crispy skin.",
          ingredients = listOf(
            Ingredient(name = "Baking Potato 1 large", nameEn = "Baking Potato", amount = "1 large", estPrice = 0.45, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Baked Beans 1 can", nameEn = "Baked Beans", amount = "1 tin (400g)", estPrice = 0.50, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Cheddar Cheese", nameEn = "Cheddar", amount = "40g", estPrice = 0.60, category = StoreType.GROCERY_CONVENIENCE, confidence = PriceConfidence.MEDIUM)
          ),
          instructions = listOf("Prick potato and microwave for 7-8 minutes.", "Warm baked beans in saucepan.", "Split potato, spoon hot beans and top with grated cheddar."),
          instructionsEn = listOf("Prick potato and microwave for 7-8 minutes.", "Warm baked beans in saucepan.", "Split potato, spoon hot beans and top with grated cheddar.")
        ),
        Meal(
          id = "uk-2",
          name = "Hearty Root Veg & Red Lentil Soup with Crusty Bread",
          nameEn = "Hearty Lentil & Carrot Stew",
          mealType = "DINNER",
          description = "Warming winter soup made from red split lentils, carrots, leeks and vegetable stock.",
          descriptionEn = "Warming high-protein stew using pantry red lentils and market vegetables.",
          estimatedCost = 2.10,
          proteinGrams = 21,
          carbsGrams = 54,
          veggiesGrams = 55,
          approxCalories = 390,
          toolsNeeded = listOf("Stock Pot"),
          prepTimeMinutes = 20,
          isBatchCook = true,
          batchTip = "Makes 4 big portions. Freeze in tupperware for quick weekday dinners.",
          ingredients = listOf(
            Ingredient(name = "Red Split Lentils", nameEn = "Red Lentils", amount = "150g", estPrice = 0.40, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Carrots & Leeks (Market pack)", nameEn = "Carrots & Leeks", amount = "300g", estPrice = 0.80, category = StoreType.FARMERS_MARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Crusty Bread Rolls", nameEn = "Bread", amount = "2 rolls", estPrice = 0.60, category = StoreType.GROCERY_CONVENIENCE, confidence = PriceConfidence.HIGH)
          ),
          instructions = listOf("Chop carrots and leeks, gently sauté in oil.", "Rinse lentils, add with 600ml vegetable stock.", "Simmer 20 minutes until lentils soften into thick broth."),
          instructionsEn = listOf("Chop carrots and leeks, gently sauté in oil.", "Rinse lentils, add with 600ml vegetable stock.", "Simmer 20 minutes until lentils soften into thick broth.")
        ),
        Meal(
          id = "uk-3",
          name = "One-Pot Tomato Chickpea Pasta",
          nameEn = "Mediterranean Chickpea & Garlic Pasta",
          mealType = "DINNER",
          description = "Penne pasta cooked directly in passata garlic sauce with protein-packed canned chickpeas and spinach.",
          descriptionEn = "Ultra quick 15-minute one-pot student meal.",
          estimatedCost = 2.20,
          proteinGrams = 18,
          carbsGrams = 58,
          veggiesGrams = 35,
          approxCalories = 420,
          toolsNeeded = listOf("Saucepan"),
          prepTimeMinutes = 15,
          isBatchCook = false,
          batchTip = "",
          ingredients = listOf(
            Ingredient(name = "Penne Pasta", nameEn = "Penne Pasta", amount = "120g", estPrice = 0.35, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Chickpeas 1 can", nameEn = "Chickpeas", amount = "1 tin", estPrice = 0.55, category = StoreType.SUPERMARKET, confidence = PriceConfidence.HIGH),
            Ingredient(name = "Passata & Garlic", nameEn = "Tomato Passata", amount = "200ml", estPrice = 0.65, category = StoreType.GROCERY_CONVENIENCE, confidence = PriceConfidence.HIGH)
          ),
          instructions = listOf("Boil pasta until al dente.", "Stir in passata, drained chickpeas and dried herbs.", "Heat through for 3 minutes and season with black pepper."),
          instructionsEn = listOf("Boil pasta until al dente.", "Stir in passata, drained chickpeas and dried herbs.", "Heat through for 3 minutes and season with black pepper.")
        )
      )
      else -> listOf(
        // Default: Malang, Indonesia
        Meal(
          id = "id-1",
          name = "Sayur Bening Bayam Jagung Manis + Tempe Goreng",
          nameEn = "Clear Spinach & Corn Soup with Crispy Tempeh",
          mealType = "LUNCH",
          description = "Sayur bayam segar dengan jagung manis pipil dan tempe goreng gurih berprotein tinggi.",
          descriptionEn = "Refreshing clear spinach soup with sweet corn and seasoned crispy tempeh.",
          estimatedCost = 6500.0,
          proteinGrams = 18,
          carbsGrams = 40,
          veggiesGrams = 50,
          approxCalories = 360,
          toolsNeeded = listOf("Panci / Magic Com", "Wajan"),
          prepTimeMinutes = 15,
          isBatchCook = true,
          batchTip = "Tempe bisa dimarinasi bumbu ketumbar sekaligus untuk 3 hari.",
          batchTipEn = "Tempeh can be marinated in coriander spices in advance for 3 days.",
          ingredients = listOf(
            Ingredient("Bayam Segar 1 ikat", "Fresh Spinach", "1 ikat", "1 bunch", 2500.0, StoreType.FARMERS_MARKET, PriceConfidence.HIGH),
            Ingredient("Jagung Manis 1 buah", "Sweet Corn", "1 buah", "1 pc", 3000.0, StoreType.FARMERS_MARKET, PriceConfidence.HIGH),
            Ingredient("Tempe 1/2 papan", "Tempeh half block", "1/2 papan", "1/2 block", 2000.0, StoreType.FARMERS_MARKET, PriceConfidence.HIGH)
          ),
          instructions = listOf("Petik daun bayam, cuci bersih.", "Didihkan air bersama bawang merah dan temu kunci.", "Masukkan jagung sampai empuk, tambahkan bayam 1 menit."),
          instructionsEn = listOf("Wash spinach thoroughly.", "Boil water with shallots and fingerroot.", "Add sweet corn, cook until tender, add spinach for 1 min.")
        ),
        Meal(
          id = "id-2",
          name = "Nasi Telur Dadar Padang Bumbu Daun Bawang",
          nameEn = "Thick Padang Style Scallion Omelet with Rice",
          mealType = "BREAKFAST",
          description = "Telur dadar tebal khas Padang dengan irisan daun bawang, cabai merah, dan tepi renyah gurih.",
          descriptionEn = "Fluffy thick Padang omelet with scallions, chili, and crispy aromatic edges.",
          estimatedCost = 5000.0,
          proteinGrams = 15,
          carbsGrams = 38,
          veggiesGrams = 15,
          approxCalories = 380,
          toolsNeeded = listOf("Wajan / Teflon"),
          prepTimeMinutes = 10,
          isBatchCook = false,
          batchTip = "",
          batchTipEn = "",
          ingredients = listOf(
            Ingredient("Telur Ayam 2 butir", "2 Eggs", "2 butir", "2 pcs", 5000.0, StoreType.GROCERY_CONVENIENCE, PriceConfidence.HIGH),
            Ingredient("Daun Bawang 1 batang", "1 Scallion", "1 batang", "1 stalk", 1000.0, StoreType.FARMERS_MARKET, PriceConfidence.HIGH)
          ),
          instructions = listOf("Iris halus daun bawang.", "Kocok lepas 2 telur dengan sedikit tepung dan garam.", "Goreng dalam minyak panas sampai bersarang dan matang."),
          instructionsEn = listOf("Slice scallions.", "Whisk 2 eggs with a pinch of flour and salt.", "Fry in hot oil until fluffy and golden.")
        ),
        Meal(
          id = "id-3",
          name = "Orek Tempe Basah Manis Gurih",
          nameEn = "Sweet & Savory Braised Tempeh",
          mealType = "DINNER",
          description = "Tempe potong dadu dimasak kecap manis, cabai, dan lengkuas. Tahan disimpan 3 hari!",
          descriptionEn = "Diced tempeh braised with sweet soy sauce and mild chilies. Tastes even better the next day!",
          estimatedCost = 4500.0,
          proteinGrams = 19,
          carbsGrams = 35,
          veggiesGrams = 15,
          approxCalories = 330,
          toolsNeeded = listOf("Wajan / Rice Cooker"),
          prepTimeMinutes = 15,
          isBatchCook = true,
          batchTip = "Bisa dimasak 1 papan tempe untuk lauk 3 hari ke depan.",
          batchTipEn = "Can cook a full block of tempeh for side dishes over the next 3 days.",
          ingredients = listOf(
            Ingredient("Tempe 1 papan", "1 Block Tempeh", "1 papan", "1 block", 4000.0, StoreType.FARMERS_MARKET, PriceConfidence.HIGH),
            Ingredient("Kecap Manis & Cabai", "Sweet Soy & Chili", "Secukupnya", "To taste", 1500.0, StoreType.GROCERY_CONVENIENCE, PriceConfidence.HIGH)
          ),
          instructions = listOf("Goreng tempe setengah matang.", "Tumis bawang dan cabai.", "Masukkan tempe dan kecap manis hingga meresap."),
          instructionsEn = listOf("Fry tempeh cubes lightly.", "Sauté aromatics.", "Add sweet soy sauce and simmer until caramelized.")
        )
      )
    }
  }

  fun getSampleStoresForPreset(preset: DemoLocationPreset): List<NearbyStore> {
    return listOf(
      NearbyStore(
        id = "mlg-1",
        name = "Pasar Dinoyo",
        type = StoreType.FARMERS_MARKET,
        area = "Dinoyo, Lowokwaru",
        address = "Jl. MT Haryono No. 167, Dinoyo, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9430, 112.6070),
        latitude = -7.9430,
        longitude = 112.6070,
        openingHours = "05:00 - 17:00",
        bestForItems = listOf("Tempe & Tahu Segar", "Bayam, Kangkung & Jagung", "Bumbu Ulek Basah"),
        bestForItemsEn = listOf("Fresh Tempeh & Tofu", "Spinach, Water Spinach & Corn", "Fresh Ground Spices"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-2",
        name = "Pasar Besar Malang",
        type = StoreType.FARMERS_MARKET,
        area = "Klojen",
        address = "Jl. Pasar Besar, Sukoharjo, Kec. Klojen, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9868, 112.6322),
        latitude = -7.9868,
        longitude = 112.6322,
        openingHours = "04:00 - 18:00",
        bestForItems = listOf("Daging Sapi & Ayam Segar", "Beras Karungan", "Ikan Basah & Rempah Komplit"),
        bestForItemsEn = listOf("Fresh Beef & Chicken", "Bulk Rice Sacks", "Fresh Fish & Whole Spices"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-3",
        name = "Pasar Oro-Oro Dowo",
        type = StoreType.FARMERS_MARKET,
        area = "Oro-oro Dowo, Klojen",
        address = "Jl. Guntur No. 20, Oro-oro Dowo, Kec. Klojen, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9698, 112.6258),
        latitude = -7.9698,
        longitude = 112.6258,
        openingHours = "05:30 - 14:00",
        bestForItems = listOf("Sayur Organik Bersih", "Daging Ayam Filet", "Bumbu Rempah Segar"),
        bestForItemsEn = listOf("Clean Organic Veggies", "Chicken Fillet", "Fresh Herb Seasonings"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-4",
        name = "Pasar Blimbing",
        type = StoreType.FARMERS_MARKET,
        area = "Blimbing",
        address = "Jl. Borobudur No. 1, Blimbing, Kec. Blimbing, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9412, 112.6455),
        latitude = -7.9412,
        longitude = 112.6455,
        openingHours = "05:00 - 16:00",
        bestForItems = listOf("Sayur Mayur Segar Murah", "Tahu Tempe Sanan", "Cabai & Bawang Grosir"),
        bestForItemsEn = listOf("Affordable Fresh Veggies", "Authentic Sanan Tempeh", "Wholesale Chili & Shallots"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-5",
        name = "Pasar Tawangmangu",
        type = StoreType.FARMERS_MARKET,
        area = "Lowokwaru",
        address = "Jl. Tawangmangu, Lowokwaru, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9602, 112.6315),
        latitude = -7.9602,
        longitude = 112.6315,
        openingHours = "05:00 - 13:00",
        bestForItems = listOf("Ikan Asin & Teri", "Sayuran Sop & Asem", "Bumbu Dapur Rumahan"),
        bestForItemsEn = listOf("Dried & Salted Fish", "Soup & Tamarind Veggies", "Pantry Seasoning"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-6",
        name = "Warung Sayur Bu Siti Dinoyo",
        type = StoreType.GROCERY_CONVENIENCE,
        area = "Dinoyo, Lowokwaru",
        address = "Jl. Gajayana Gang 4, Dinoyo, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9460, 112.6090),
        latitude = -7.9460,
        longitude = 112.6090,
        openingHours = "06:00 - 12:00",
        bestForItems = listOf("Paket Sayur Sop Rp 3.000", "Telur Satuan", "Bumbu Racik Sachet"),
        bestForItemsEn = listOf("Pre-cut Soup Veggie Packs", "Individual Eggs", "Seasoning Sachets"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-7",
        name = "Warung Sayur & Sembako Mergan",
        type = StoreType.GROCERY_CONVENIENCE,
        area = "Sukun",
        address = "Jl. Raya Langsep No. 42, Mergan, Kec. Sukun, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9890, 112.6185),
        latitude = -7.9890,
        longitude = 112.6185,
        openingHours = "06:00 - 18:00",
        bestForItems = listOf("Minyak Goreng Ecer", "Bawang Merah & Putih", "Tempe Mendoan"),
        bestForItemsEn = listOf("Cooking Oil by the Glass", "Shallots & Garlic", "Mendoan Tempeh"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-8",
        name = "Warung Sembako Barokah Soekarno Hatta",
        type = StoreType.GROCERY_CONVENIENCE,
        area = "Mojolangu, Lowokwaru",
        address = "Jl. Soekarno Hatta No. 88, Jatimulyo, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9425, 112.6178),
        latitude = -7.9425,
        longitude = 112.6178,
        openingHours = null, // Hidden because hours vary significantly by owner
        bestForItems = listOf("Beras Eceran 1-2kg", "Gula Pasir & Garam", "Kecap & Saus"),
        bestForItemsEn = listOf("Loose Rice 1-2kg", "Sugar & Salt", "Soy Sauce & Condiments"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-9",
        name = "Superindo Tlogomas",
        type = StoreType.SUPERMARKET,
        area = "Tlogomas, Lowokwaru",
        address = "Jl. Raya Tlogomas No. 37, Tlogomas, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9350, 112.5970),
        latitude = -7.9350,
        longitude = 112.5970,
        openingHours = "08:00 - 22:00",
        phone = "+62 341 578920",
        bestForItems = listOf("Minyak Goreng Pouch 2L", "Daging & Fillet Segar", "Buah & Sayur Higienis"),
        bestForItemsEn = listOf("2L Pouch Cooking Oil", "Fresh Meat & Fillets", "Hygienic Fruit & Veggies"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-10",
        name = "Lai Lai Market Buah & Sayur",
        type = StoreType.SUPERMARKET,
        area = "Klojen",
        address = "Jl. Semeru No. 34, Oro-oro Dowo, Kec. Klojen, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9775, 112.6240),
        latitude = -7.9775,
        longitude = 112.6240,
        openingHours = "08:30 - 21:30",
        bestForItems = listOf("Sayuran Segar Premium", "Bumbu Masak Lengkap & Import", "Buah Segar"),
        bestForItemsEn = listOf("Premium Fresh Greens", "Import & Gourmet Spices", "Fresh Fruits"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-11",
        name = "Indomaret Plus Gajayana",
        type = StoreType.MINIMARKET,
        area = "Dinoyo, Lowokwaru",
        address = "Jl. Gajayana No. 22, Dinoyo, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9445, 112.6080),
        latitude = -7.9445,
        longitude = 112.6080,
        openingHours = "24 Jam",
        bestForItems = listOf("Telur Ayam Pack", "Minyak Kemasan 1L", "Bumbu Instant"),
        bestForItemsEn = listOf("Carton of Eggs", "1L Bottled Oil", "Instant Seasoning Packs"),
        isSampleData = true
      ),
      NearbyStore(
        id = "mlg-12",
        name = "Alfamart Soekarno Hatta",
        type = StoreType.MINIMARKET,
        area = "Jatimulyo, Lowokwaru",
        address = "Jl. Soekarno Hatta No. 12, Kec. Lowokwaru, Kota Malang",
        distanceKm = OsmApiClient.calculateHaversineDistanceKm(preset.latitude, preset.longitude, -7.9480, 112.6160),
        latitude = -7.9480,
        longitude = 112.6160,
        openingHours = "24 Jam",
        bestForItems = listOf("Mi Instan & Bihun", "Beras 5kg", "Susu & Mentega"),
        bestForItemsEn = listOf("Instant Noodles & Rice Vermicelli", "5kg Rice Bags", "Milk & Butter"),
        isSampleData = true
      )
    )
  }

  private fun getSampleTanggalTuaForPreset(preset: DemoLocationPreset): TanggalTuaPlan {
    val daily = preset.defaultBudget * 0.4
    val meals = getMealsForPreset(preset).take(2)
    return TanggalTuaPlan(
      remainingBudget = daily * 3,
      daysLeft = 3,
      dailyAllowance = daily,
      meals = meals,
      survivalTips = when (preset.city) {
        "Mumbai" -> listOf(
          "Moong dal khichdi provides complete protein and carbohydrates for under ₹30.",
          "Buy onions and potatoes in 2kg batches at local sabzi mandis for 40% wholesale discount."
        )
        "London" -> listOf(
          "Bake jacket potatoes and bulk up with own-brand baked beans (35p) and grated cheddar.",
          "Yellow-sticker reductions at supermarkets after 7pm frequently offer bread and veggies at 70% off."
        )
        else -> listOf(
          "Orek tempe dan tahu bumbu kecap tahan 3 hari di wadah tertutup.",
          "Beli sayur paketan di pasar tradisional jam 6-7 pagi untuk diskon maksimal."
        )
      },
      survivalTipsEn = when (preset.city) {
        "Mumbai" -> listOf(
          "Moong dal khichdi provides complete protein and carbohydrates for under ₹30.",
          "Buy onions and potatoes in 2kg batches at local sabzi mandis for 40% wholesale discount."
        )
        "London" -> listOf(
          "Bake jacket potatoes and bulk up with own-brand baked beans (35p) and grated cheddar.",
          "Yellow-sticker reductions at supermarkets after 7pm frequently offer bread and veggies at 70% off."
        )
        else -> listOf(
          "Sweet soy braised tempeh and tofu keeps fresh for 3 days in a sealed container.",
          "Buy bundled vegetable packs at traditional morning markets at 6-7 AM for maximum discounts."
        )
      }
    )
  }
}
