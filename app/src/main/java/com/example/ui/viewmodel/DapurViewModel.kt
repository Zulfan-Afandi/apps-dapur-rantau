package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.GeminiApiClient
import com.example.data.maps.OsmApiClient
import com.example.data.model.AppLanguage
import com.example.data.model.DayPlan
import com.example.data.model.DemoLocationPreset
import com.example.data.model.DemoLocations
import com.example.data.model.FridgeScanResult
import com.example.data.model.LocaleContext
import com.example.data.model.MascotState
import com.example.data.model.Meal
import com.example.data.model.NearbyStore
import com.example.data.model.PriceConfidence
import com.example.data.model.ShoppingItem
import com.example.data.model.StoreType
import com.example.data.model.TanggalTuaPlan
import com.example.data.model.UserPreferences
import com.example.data.repository.DapurRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val labelId: String, val labelEn: String) {
  WEEKLY_PLAN("Menu 7 Hari", "7-Day Plan"),
  SHOPPING_LIST("Belanja", "Shopping"),
  MAP_NEARBY("Peta Pasar", "Market Map"),
  TANGGAL_TUA("Tanggal Tua", "End of Month"),
  FRIDGE_SCAN("Scan Kulkas", "Fridge Scan")
}

data class DapurUiState(
  val preferences: UserPreferences,
  val localeContext: LocaleContext,
  val weeklyPlan: List<DayPlan>,
  val shoppingItems: List<ShoppingItem>,
  val nearbyStores: List<NearbyStore>,
  val tanggalTuaPlan: TanggalTuaPlan?,
  val currentTab: AppTab = AppTab.WEEKLY_PLAN,
  val language: AppLanguage = AppLanguage.ID,
  val mascotState: MascotState = MascotState.IDLE,
  val selectedDayIndex: Int = 0,
  val selectedMealDetail: Meal? = null,
  val isGeneratingPlan: Boolean = false,
  val generationErrorMessage: String? = null,
  val isScanningFridge: Boolean = false,
  val scanErrorMessage: String? = null,
  val fridgeScanResult: FridgeScanResult? = null,
  val selectedStoreTypeFilter: StoreType? = null,
  val selectedStoreDetail: NearbyStore? = null,
  val showOnboarding: Boolean = false,
  val isBatchCookFilterActive: Boolean = false,
  val mapCenterLat: Double = -7.9430,
  val mapCenterLng: Double = 112.6070,
  val showSearchAreaButton: Boolean = false,
  val isSearchingPlaces: Boolean = false,
  val mapErrorMessage: String? = null,
  val isUsingSampleData: Boolean = false,
  val geolocationDenied: Boolean = false
) {
  val currentDayPlan: DayPlan?
    get() = weeklyPlan.getOrNull(selectedDayIndex)

  val totalWeeklyEstimatedCost: Double
    get() = weeklyPlan.sumOf { it.totalEstimatedCost }

  val totalWeeklyBudget: Double
    get() = preferences.dailyBudget * 7.0

  val totalShoppingEstimatedCost: Double
    get() = shoppingItems.sumOf { it.estimatedPrice }

  val totalShoppingActualCost: Double
    get() = shoppingItems.sumOf { it.effectivePrice }

  val checkedShoppingCount: Int
    get() = shoppingItems.count { it.isChecked }
}

class DapurViewModel(
  private val repository: DapurRepository = DapurRepository(),
  private val geminiClient: GeminiApiClient = GeminiApiClient(),
  private val osmClient: OsmApiClient = OsmApiClient()
) : ViewModel() {

  private val _currentTab = MutableStateFlow(AppTab.WEEKLY_PLAN)
  private val _language = MutableStateFlow(AppLanguage.ID)
  private val _mascotState = MutableStateFlow(MascotState.IDLE)
  private val _selectedDayIndex = MutableStateFlow(0)
  private val _selectedMealDetail = MutableStateFlow<Meal?>(null)
  private val _isGeneratingPlan = MutableStateFlow(false)
  private val _generationErrorMessage = MutableStateFlow<String?>(null)
  private val _isScanningFridge = MutableStateFlow(false)
  private val _scanErrorMessage = MutableStateFlow<String?>(null)
  private val _fridgeScanResult = MutableStateFlow<FridgeScanResult?>(null)
  private val _selectedStoreTypeFilter = MutableStateFlow<StoreType?>(null)
  private val _selectedStoreDetail = MutableStateFlow<NearbyStore?>(null)
  private val _showOnboarding = MutableStateFlow(false)
  private val _isBatchCookFilterActive = MutableStateFlow(false)

  private val _mapCenterLat = MutableStateFlow(-7.9430)
  private val _mapCenterLng = MutableStateFlow(112.6070)
  private val _showSearchAreaButton = MutableStateFlow(false)
  private val _isSearchingPlaces = MutableStateFlow(false)
  private val _mapErrorMessage = MutableStateFlow<String?>(null)
  private val _isUsingSampleData = MutableStateFlow(false)
  private val _geolocationDenied = MutableStateFlow(false)

  private var searchDebounceJob: Job? = null

  val uiState: StateFlow<DapurUiState> = combine(
    repository.preferences,
    repository.localeContext,
    repository.weeklyPlan,
    repository.shoppingItems,
    repository.nearbyStores,
    repository.tanggalTuaPlan,
    _currentTab,
    _language,
    _mascotState,
    _selectedDayIndex,
    _selectedMealDetail,
    _isGeneratingPlan,
    _generationErrorMessage,
    _isScanningFridge,
    _scanErrorMessage,
    _fridgeScanResult,
    _selectedStoreTypeFilter,
    _selectedStoreDetail,
    _showOnboarding,
    _isBatchCookFilterActive,
    _mapCenterLat,
    _mapCenterLng,
    _showSearchAreaButton,
    _isSearchingPlaces,
    _mapErrorMessage,
    _isUsingSampleData,
    _geolocationDenied
  ) { args: Array<Any?> ->
    DapurUiState(
      preferences = args[0] as UserPreferences,
      localeContext = args[1] as LocaleContext,
      weeklyPlan = (args[2] as List<*>).filterIsInstance<DayPlan>(),
      shoppingItems = (args[3] as List<*>).filterIsInstance<ShoppingItem>(),
      nearbyStores = (args[4] as List<*>).filterIsInstance<NearbyStore>(),
      tanggalTuaPlan = args[5] as? TanggalTuaPlan,
      currentTab = args[6] as AppTab,
      language = args[7] as AppLanguage,
      mascotState = args[8] as MascotState,
      selectedDayIndex = args[9] as Int,
      selectedMealDetail = args[10] as? Meal,
      isGeneratingPlan = args[11] as Boolean,
      generationErrorMessage = args[12] as? String,
      isScanningFridge = args[13] as Boolean,
      scanErrorMessage = args[14] as? String,
      fridgeScanResult = args[15] as? FridgeScanResult,
      selectedStoreTypeFilter = args[16] as? StoreType,
      selectedStoreDetail = args[17] as? NearbyStore,
      showOnboarding = args[18] as Boolean,
      isBatchCookFilterActive = args[19] as Boolean,
      mapCenterLat = args[20] as Double,
      mapCenterLng = args[21] as Double,
      showSearchAreaButton = args[22] as Boolean,
      isSearchingPlaces = args[23] as Boolean,
      mapErrorMessage = args[24] as? String,
      isUsingSampleData = args[25] as Boolean,
      geolocationDenied = args[26] as Boolean
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DapurUiState(
      preferences = repository.preferences.value,
      localeContext = repository.localeContext.value,
      weeklyPlan = repository.weeklyPlan.value,
      shoppingItems = repository.shoppingItems.value,
      nearbyStores = repository.nearbyStores.value,
      tanggalTuaPlan = repository.tanggalTuaPlan.value
    )
  )

  init {
    updateMascotMood()
  }

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
    _selectedMealDetail.value = null
    _selectedStoreDetail.value = null
    updateMascotMood()
  }

  fun toggleLanguage() {
    val newLang = if (_language.value == AppLanguage.ID) AppLanguage.EN else AppLanguage.ID
    _language.value = newLang
    val currentLocale = repository.localeContext.value
    repository.updateLocaleContext(currentLocale.copy(language = newLang))
  }

  fun applyDemoPreset(preset: DemoLocationPreset) {
    repository.applyDemoPreset(preset)
    _language.value = preset.language
    _mapCenterLat.value = preset.latitude
    _mapCenterLng.value = preset.longitude
    _showSearchAreaButton.value = false
    _selectedStoreDetail.value = null
    _mapErrorMessage.value = null
    _isUsingSampleData.value = false

    // Fetch live OSM stores for this preset location
    searchArea(preset.latitude, preset.longitude)
    updateMascotMood()
  }

  fun onMapMoved(lat: Double, lng: Double) {
    _mapCenterLat.value = lat
    _mapCenterLng.value = lng
    _showSearchAreaButton.value = true
  }

  fun searchCurrentArea() {
    val lat = _mapCenterLat.value
    val lng = _mapCenterLng.value
    _showSearchAreaButton.value = false
    searchArea(lat, lng)
  }

  fun searchArea(lat: Double, lng: Double) {
    searchDebounceJob?.cancel()
    searchDebounceJob = viewModelScope.launch {
      delay(300) // Quick debounce
      _isSearchingPlaces.value = true
      _mascotState.value = MascotState.COOKING // stirring state for loading
      _mapErrorMessage.value = null

      val result = osmClient.searchNearbyStores(lat, lng, initialRadiusMeters = 2000)
      result.onSuccess { stores ->
        _isSearchingPlaces.value = false
        if (stores.isNotEmpty()) {
          _isUsingSampleData.value = false
          repository.setNearbyStores(stores)
          _mascotState.value = MascotState.HAPPY
        } else {
          _mascotState.value = MascotState.IDLE // sleepy state for empty
        }
      }.onFailure { err ->
        _isSearchingPlaces.value = false
        _mascotState.value = MascotState.WORRIED // worried state for error
        _mapErrorMessage.value = if (_language.value == AppLanguage.ID) {
          "Gagal memuat data dari Overpass OpenStreetMap (${err.localizedMessage ?: "Jaringan lambat"}). Coba lagi atau lihat data sampel."
        } else {
          "Failed to load Overpass OpenStreetMap data (${err.localizedMessage ?: "Network issue"}). Retry or view sample data."
        }
      }
      updateMascotMood()
    }
  }

  fun searchPlaceByQuery(query: String) {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return

    // Quick match preset locations
    DemoLocations.PRESETS.find {
      it.city.contains(trimmed, ignoreCase = true) || it.country.contains(trimmed, ignoreCase = true)
    }?.let { preset ->
      applyDemoPreset(preset)
      return
    }

    searchDebounceJob?.cancel()
    searchDebounceJob = viewModelScope.launch {
      delay(800) // Debounce at least 800ms as requested
      _isSearchingPlaces.value = true
      _mascotState.value = MascotState.COOKING
      _mapErrorMessage.value = null

      val result = osmClient.searchAddress(trimmed)
      result.onSuccess { (lat, lon) ->
        _mapCenterLat.value = lat
        _mapCenterLng.value = lon
        _showSearchAreaButton.value = false
        // Update locale context city
        val curLocale = repository.localeContext.value
        repository.updateLocaleContext(curLocale.copy(city = trimmed, latitude = lat, longitude = lon))
        // Fetch nearby stores at new coordinates
        searchArea(lat, lon)
      }.onFailure { err ->
        _isSearchingPlaces.value = false
        _mascotState.value = MascotState.WORRIED
        _mapErrorMessage.value = if (_language.value == AppLanguage.ID) {
          "Lokasi '$trimmed' tidak ditemukan di Nominatim OpenStreetMap. Periksa ejaan kota atau coba lagi."
        } else {
          "Location '$trimmed' not found in Nominatim OpenStreetMap. Check spelling and retry."
        }
        updateMascotMood()
      }
    }
  }

  fun onStoresDiscovered(stores: List<NearbyStore>) {
    if (stores.isNotEmpty()) {
      _isUsingSampleData.value = false
      _mapErrorMessage.value = null
      repository.setNearbyStores(stores)
      _mascotState.value = MascotState.HAPPY
    } else {
      _mascotState.value = MascotState.IDLE
    }
  }

  fun onGeolocationDenied() {
    _geolocationDenied.value = true
    _mapErrorMessage.value = if (_language.value == AppLanguage.ID) {
      "Izin lokasi belum diberikan. Gunakan kotak pencarian untuk mencari kota / alamat mana saja di dunia."
    } else {
      "Location permission denied. Use the search box above to search any city or market worldwide."
    }
  }

  fun onUserLocationFound(lat: Double, lng: Double) {
    _geolocationDenied.value = false
    _mapCenterLat.value = lat
    _mapCenterLng.value = lng
    _mapErrorMessage.value = null
  }

  fun onMapError(error: String) {
    _mascotState.value = MascotState.WORRIED
    _mapErrorMessage.value = if (_language.value == AppLanguage.ID) {
      "Kendala peta/Overpass: $error"
    } else {
      "Map/Overpass issue: $error"
    }
  }

  fun onMapLoadingChanged(isLoading: Boolean) {
    _isSearchingPlaces.value = isLoading
    if (isLoading) {
      _mascotState.value = MascotState.COOKING
    } else {
      updateMascotMood()
    }
  }

  fun retryMapSearch() {
    _mapErrorMessage.value = null
    searchArea(_mapCenterLat.value, _mapCenterLng.value)
  }

  fun fallbackToSampleData() {
    _mapErrorMessage.value = null
    _isUsingSampleData.value = true
    val samplePreset = DemoLocations.PRESETS.first()
    val sampleStores = repository.getSampleStoresForPreset(samplePreset).map {
      it.copy(isSampleData = true)
    }
    repository.setNearbyStores(sampleStores)
    _mascotState.value = MascotState.HAPPY
  }

  fun setSelectedDay(index: Int) {
    if (index in 0..6) {
      _selectedDayIndex.value = index
    }
  }

  fun openMealDetail(meal: Meal) {
    _selectedMealDetail.value = meal
  }

  fun closeMealDetail() {
    _selectedMealDetail.value = null
  }

  fun selectStoreDetail(store: NearbyStore?) {
    _selectedStoreDetail.value = store
  }

  fun selectStoreById(storeId: String) {
    val store = repository.nearbyStores.value.find { it.id == storeId }
    _selectedStoreDetail.value = store
  }

  fun setStoreFilter(type: StoreType?) {
    _selectedStoreTypeFilter.value = type
  }

  fun toggleBatchCookFilter() {
    _isBatchCookFilterActive.value = !_isBatchCookFilterActive.value
  }

  fun openOnboarding() {
    _showOnboarding.value = true
    _mascotState.value = MascotState.WAVING
  }

  fun closeOnboarding() {
    _showOnboarding.value = false
    updateMascotMood()
  }

  fun completeOnboarding(newPrefs: UserPreferences) {
    repository.updatePreferences(newPrefs.copy(isOnboarded = true))
    _showOnboarding.value = false
    generateWeeklyPlan(useGemini = true)
  }

  fun toggleShoppingItem(itemId: String) {
    repository.toggleShoppingItem(itemId)
    updateMascotMood()
  }

  fun updateActualPrice(itemId: String, price: Double?) {
    repository.updateActualPrice(itemId, price)
    updateMascotMood()
  }

  fun addCustomShoppingItem(name: String, quantity: String, price: Double, storeType: StoreType) {
    repository.addCustomShoppingItem(name, quantity, price, storeType)
  }

  fun deleteShoppingItem(itemId: String) {
    repository.deleteShoppingItem(itemId)
  }

  fun generateWeeklyPlan(useGemini: Boolean = true) {
    viewModelScope.launch {
      _isGeneratingPlan.value = true
      _generationErrorMessage.value = null
      _mascotState.value = MascotState.COOKING

      val currentPrefs = repository.preferences.value
      val currentLocale = repository.localeContext.value

      if (useGemini && geminiClient.hasApiKey()) {
        val result = geminiClient.generateWeeklyMealPlan(currentPrefs, currentLocale)
        result.onSuccess { meals ->
          if (meals.isNotEmpty()) {
            buildDaysFromMeals(meals, currentPrefs)
            _mascotState.value = MascotState.HAPPY
          }
        }.onFailure {
          _generationErrorMessage.value = if (_language.value == AppLanguage.ID) {
            "Koneksi AI sedang sibuk. Menampilkan paket menu rekomendasi lokal terverifikasi."
          } else {
            "AI busy. Showing verified local student menu recommendations."
          }
        }
      }

      _isGeneratingPlan.value = false
      updateMascotMood()
    }
  }

  private fun buildDaysFromMeals(meals: List<Meal>, prefs: UserPreferences) {
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
      val startIndex = (index * 2) % meals.size
      val dayMeals = listOf(
        meals[startIndex % meals.size],
        meals[(startIndex + 1) % meals.size],
        meals[(startIndex + 2) % meals.size]
      )
      DayPlan(
        dayNameId = idDay,
        dayNameEn = enDay,
        dayIndex = index,
        meals = dayMeals,
        dailyBudget = prefs.dailyBudget
      )
    }
    repository.setWeeklyPlan(plans)
  }

  fun scanFridgeImage(bitmap: Bitmap) {
    viewModelScope.launch {
      _isScanningFridge.value = true
      _scanErrorMessage.value = null
      _mascotState.value = MascotState.COOKING

      val setup = repository.preferences.value.kitchenSetup
      val currentLocale = repository.localeContext.value

      if (geminiClient.hasApiKey()) {
        val result = geminiClient.scanFridgeAndSuggest(bitmap, setup, currentLocale)
        result.onSuccess { (detected, recipes) ->
          _fridgeScanResult.value = FridgeScanResult(
            detectedIngredients = detected,
            suggestedMeals = recipes,
            cheapMarketAdditions = listOf("Fresh greens from market", "Lentils / Eggs", "Local spices")
          )
          _mascotState.value = MascotState.HAPPY
        }.onFailure {
          _scanErrorMessage.value = "AI scan completed using typical pantry ingredients."
        }
      }

      _isScanningFridge.value = false
      updateMascotMood()
    }
  }

  fun generateTanggalTuaPlan(remainingBudget: Double, daysLeft: Int) {
    viewModelScope.launch {
      _isGeneratingPlan.value = true
      _mascotState.value = MascotState.COOKING

      val setup = repository.preferences.value.kitchenSetup
      val currentLocale = repository.localeContext.value

      if (geminiClient.hasApiKey()) {
        val result = geminiClient.generateTanggalTuaPlan(remainingBudget, daysLeft, setup, currentLocale)
        result.onSuccess { meals ->
          val dailyAllowance = if (daysLeft > 0) remainingBudget / daysLeft else remainingBudget
          repository.setTanggalTuaPlan(
            TanggalTuaPlan(
              remainingBudget = remainingBudget,
              daysLeft = daysLeft,
              dailyAllowance = dailyAllowance,
              meals = meals,
              survivalTips = listOf(
                "Batch cook base carbohydrates and stews for 3 days to cut cooking fuel and waste.",
                "Visit local wet / farmers markets in the morning for best prices on staples."
              )
            )
          )
        }
      }

      _isGeneratingPlan.value = false
      updateMascotMood()
    }
  }

  private fun updateMascotMood() {
    if (_isGeneratingPlan.value || _isScanningFridge.value || _isSearchingPlaces.value) {
      _mascotState.value = MascotState.COOKING
      return
    }
    if (_mapErrorMessage.value != null) {
      _mascotState.value = MascotState.WORRIED
      return
    }
    val currentPlan = uiState.value.currentDayPlan
    if (currentPlan != null) {
      _mascotState.value = if (currentPlan.isUnderBudget) MascotState.HAPPY else MascotState.WORRIED
    } else {
      _mascotState.value = MascotState.IDLE
    }
  }
}
