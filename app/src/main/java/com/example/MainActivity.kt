package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DemoLocations
import com.example.ui.components.KukiMascot
import com.example.ui.screens.FridgeScanScreen
import com.example.ui.screens.MapNearbyScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.ShoppingListScreen
import com.example.ui.screens.TanggalTuaScreen
import com.example.ui.screens.WeeklyPlanScreen
import com.example.ui.theme.AppStrings
import com.example.ui.theme.DapurRantauTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.DapurViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: DapurViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DapurRantauTheme {
        val uiState by viewModel.uiState.collectAsState()

        MainAppContent(
          uiState = uiState,
          viewModel = viewModel
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
  uiState: com.example.ui.viewmodel.DapurUiState,
  viewModel: DapurViewModel
) {
  var showDemoDropdown by remember { mutableStateOf(false) }

  // Check if Recipe Detail is opened
  if (uiState.selectedMealDetail != null) {
    RecipeDetailScreen(
      meal = uiState.selectedMealDetail,
      localeContext = uiState.localeContext,
      language = uiState.language,
      onBack = { viewModel.closeMealDetail() }
    )
    return
  }

  // Check if Onboarding is requested
  if (uiState.showOnboarding) {
    OnboardingScreen(
      currentPreferences = uiState.preferences,
      localeContext = uiState.localeContext,
      language = uiState.language,
      onComplete = { viewModel.completeOnboarding(it) },
      onApplyPreset = { viewModel.applyDemoPreset(it) },
      onClose = { viewModel.closeOnboarding() }
    )
    return
  }

  // Handle system back button for bottom sheet and secondary tabs
  BackHandler(enabled = uiState.selectedStoreDetail != null) {
    viewModel.selectStoreDetail(null)
  }

  BackHandler(enabled = uiState.currentTab != AppTab.WEEKLY_PLAN) {
    viewModel.setTab(AppTab.WEEKLY_PLAN)
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Color(0xFFFFFBF2),
    topBar = {
      CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = Color(0xFFFFFBF2)
        ),
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            KukiMascot(
              state = uiState.mascotState,
              size = 32.dp,
              isPaused = uiState.currentTab == AppTab.MAP_NEARBY
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.appTitle(uiState.language),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = Color(0xFFD32F2F),
                fontSize = 18.sp
              )
            )
          }
        },
        actions = {
          // Demo Location Preset Picker
          Box {
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFEF3C7))
                .clickable { showDemoDropdown = true }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Demo Preset",
                tint = Color(0xFFB45309),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = uiState.localeContext.city,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309),
                  fontSize = 11.sp
                )
              )
            }

            DropdownMenu(
              expanded = showDemoDropdown,
              onDismissRequest = { showDemoDropdown = false }
            ) {
              Text(
                text = AppStrings.demoLocations(uiState.language),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF6B7280)),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
              )
              DemoLocations.PRESETS.forEach { preset ->
                DropdownMenuItem(
                  text = {
                    Column {
                      Text(preset.name, fontWeight = FontWeight.Bold)
                      Text(
                        "${preset.currency} • ${preset.cuisine}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF78716C))
                      )
                    }
                  },
                  onClick = {
                    viewModel.applyDemoPreset(preset)
                    showDemoDropdown = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Language toggle button (ID / EN)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF3F4F6))
              .clickable { viewModel.toggleLanguage() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("language_toggle_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Switch Language",
                tint = Color(0xFF374151),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (uiState.language == AppLanguage.ID) "ID" else "EN",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF374151),
                  fontSize = 11.sp
                )
              )
            }
          }

          // Settings / Preferences re-open
          IconButton(
            onClick = { viewModel.openOnboarding() },
            modifier = Modifier.testTag("settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Settings",
              tint = Color(0xFF78716C),
              modifier = Modifier.size(20.dp)
            )
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
      ) {
        AppTab.entries.forEach { tab ->
          val isSelected = uiState.currentTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { viewModel.setTab(tab) },
            icon = {
              Icon(
                imageVector = when (tab) {
                  AppTab.WEEKLY_PLAN -> if (isSelected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
                  AppTab.SHOPPING_LIST -> if (isSelected) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart
                  AppTab.MAP_NEARBY -> if (isSelected) Icons.Filled.Place else Icons.Outlined.Place
                  AppTab.TANGGAL_TUA -> if (isSelected) Icons.Filled.Savings else Icons.Outlined.Savings
                  AppTab.FRIDGE_SCAN -> if (isSelected) Icons.Filled.CameraAlt else Icons.Outlined.CameraAlt
                },
                contentDescription = if (uiState.language == AppLanguage.ID) tab.labelId else tab.labelEn
              )
            },
            label = {
              Text(
                text = when (tab) {
                  AppTab.WEEKLY_PLAN -> AppStrings.tabPlan(uiState.language)
                  AppTab.SHOPPING_LIST -> AppStrings.tabShopping(uiState.language)
                  AppTab.MAP_NEARBY -> AppStrings.tabMap(uiState.language)
                  AppTab.TANGGAL_TUA -> AppStrings.tabTanggalTua(uiState.language)
                  AppTab.FRIDGE_SCAN -> AppStrings.tabFridge(uiState.language)
                },
                fontSize = 11.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color(0xFFD32F2F),
              selectedTextColor = Color(0xFFD32F2F),
              indicatorColor = Color(0xFFFEE2E2),
              unselectedIconColor = Color(0xFF9CA3AF),
              unselectedTextColor = Color(0xFF6B7280)
            ),
            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (uiState.currentTab) {
        AppTab.WEEKLY_PLAN -> {
          WeeklyPlanScreen(
            weeklyPlan = uiState.weeklyPlan,
            selectedDayIndex = uiState.selectedDayIndex,
            dailyBudget = uiState.preferences.dailyBudget,
            localeContext = uiState.localeContext,
            language = uiState.language,
            mascotState = uiState.mascotState,
            isGenerating = uiState.isGeneratingPlan,
            errorMessage = uiState.generationErrorMessage,
            isBatchCookFilterActive = uiState.isBatchCookFilterActive,
            onSelectDay = { viewModel.setSelectedDay(it) },
            onSelectMeal = { viewModel.openMealDetail(it) },
            onGeneratePlan = { viewModel.generateWeeklyPlan(useGemini = true) },
            onToggleBatchCookFilter = { viewModel.toggleBatchCookFilter() },
            onOpenOnboarding = { viewModel.openOnboarding() }
          )
        }
        AppTab.SHOPPING_LIST -> {
          ShoppingListScreen(
            shoppingItems = uiState.shoppingItems,
            weeklyBudget = uiState.totalWeeklyBudget,
            localeContext = uiState.localeContext,
            language = uiState.language,
            selectedStoreFilter = uiState.selectedStoreTypeFilter,
            onToggleItem = { viewModel.toggleShoppingItem(it) },
            onUpdateActualPrice = { id, price -> viewModel.updateActualPrice(id, price) },
            onAddItem = { name, qty, price, store -> viewModel.addCustomShoppingItem(name, qty, price, store) },
            onDeleteItem = { viewModel.deleteShoppingItem(it) },
            onSetStoreFilter = { viewModel.setStoreFilter(it) }
          )
        }
        AppTab.MAP_NEARBY -> {
          MapNearbyScreen(
            stores = uiState.nearbyStores,
            shoppingItems = uiState.shoppingItems,
            localeContext = uiState.localeContext,
            language = uiState.language,
            selectedStoreFilter = uiState.selectedStoreTypeFilter,
            selectedStoreDetail = uiState.selectedStoreDetail,
            showSearchAreaButton = uiState.showSearchAreaButton,
            isSearchingPlaces = uiState.isSearchingPlaces,
            mapErrorMessage = uiState.mapErrorMessage,
            isUsingSampleData = uiState.isUsingSampleData,
            geolocationDenied = uiState.geolocationDenied,
            mascotState = uiState.mascotState,
            onSetStoreFilter = { viewModel.setStoreFilter(it) },
            onSelectStore = { viewModel.selectStoreDetail(it) },
            onStoreSelectedById = { viewModel.selectStoreById(it) },
            onMapMoved = { lat, lng -> viewModel.onMapMoved(lat, lng) },
            onSearchCurrentArea = { viewModel.searchCurrentArea() },
            onSearchQuery = { viewModel.searchPlaceByQuery(it) },
            onApplyPreset = { viewModel.applyDemoPreset(it) },
            onStoresDiscovered = { viewModel.onStoresDiscovered(it) },
            onGeolocationDenied = { viewModel.onGeolocationDenied() },
            onUserLocationFound = { lat, lng -> viewModel.onUserLocationFound(lat, lng) },
            onRetry = { viewModel.retryMapSearch() },
            onFallbackToSample = { viewModel.fallbackToSampleData() }
          )
        }
        AppTab.TANGGAL_TUA -> {
          TanggalTuaScreen(
            tanggalTuaPlan = uiState.tanggalTuaPlan,
            localeContext = uiState.localeContext,
            language = uiState.language,
            isGenerating = uiState.isGeneratingPlan,
            onGenerate = { money, days -> viewModel.generateTanggalTuaPlan(money, days) },
            onSelectMeal = { viewModel.openMealDetail(it) }
          )
        }
        AppTab.FRIDGE_SCAN -> {
          FridgeScanScreen(
            scanResult = uiState.fridgeScanResult,
            isScanning = uiState.isScanningFridge,
            errorMessage = uiState.scanErrorMessage,
            localeContext = uiState.localeContext,
            language = uiState.language,
            onScanImage = { viewModel.scanFridgeImage(it) },
            onSelectMeal = { viewModel.openMealDetail(it) }
          )
        }
      }
    }
  }
}
