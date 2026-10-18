package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DayPlan
import com.example.data.model.LocaleContext
import com.example.data.model.MascotState
import com.example.data.model.Meal
import com.example.ui.components.BalanceBar
import com.example.ui.components.DekTelurAvatar
import com.example.ui.components.KukiMascot
import com.example.ui.components.MasTempeAvatar
import com.example.ui.components.MbakCabeAvatar
import com.example.ui.components.NutritionDisclaimerCard
import com.example.ui.theme.AppStrings
import com.example.ui.theme.DapurRantauTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WeeklyPlanScreen(
  weeklyPlan: List<DayPlan>,
  selectedDayIndex: Int,
  dailyBudget: Double,
  localeContext: LocaleContext,
  language: AppLanguage,
  mascotState: MascotState,
  isGenerating: Boolean,
  errorMessage: String?,
  isBatchCookFilterActive: Boolean,
  onSelectDay: (Int) -> Unit,
  onSelectMeal: (Meal) -> Unit,
  onGeneratePlan: () -> Unit,
  onToggleBatchCookFilter: () -> Unit,
  onOpenOnboarding: () -> Unit
) {
  val currentDay = weeklyPlan.getOrNull(selectedDayIndex)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Hero Banner with Mascot & Budget Pulse
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (language == AppLanguage.ID) "Menu Hemat Harian" else "Daily Budget Meal Plan",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD32F2F)
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFEF3C7))
                    .clickable { onOpenOnboarding() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${localeContext.city} • ${localeContext.currency}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFB45309)
                    )
                  )
                }
              }
              Text(
                text = if (language == AppLanguage.ID)
                  "Kuliner ${localeContext.cuisine} hemat, sehat, dan pas di kantong"
                else
                  "Nutritious, affordable ${localeContext.cuisine.replace("Tempe, Nasi, Sayur Segar", "Tempeh, Rice, Fresh Veggies")} cooking",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C)),
                modifier = Modifier.padding(top = 2.dp)
              )
            }
            KukiMascot(state = mascotState, size = 68.dp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Budget Tracker Bar for selected day
          val dayCost = currentDay?.totalEstimatedCost ?: 0.0
          val isUnderBudget = dayCost <= dailyBudget
          val progress = (dayCost.toFloat() / dailyBudget.coerceAtLeast(1.0).toFloat()).coerceIn(0f, 1f)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (language == AppLanguage.ID) "Estimasi Hari Ini:" else "Today's Est. Cost:",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C))
              )
              Text(
                text = localeContext.formatPrice(dayCost),
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  color = if (isUnderBudget) Color(0xFF16A34A) else Color(0xFFD32F2F)
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isUnderBudget) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (isUnderBudget) {
                  if (language == AppLanguage.ID) "Hemat! Sisa ${localeContext.formatPrice(dailyBudget - dayCost)}" else "Under by ${localeContext.formatPrice(dailyBudget - dayCost)}"
                } else {
                  if (language == AppLanguage.ID) "Lebih ${localeContext.formatPrice(dayCost - dailyBudget)}" else "Over by ${localeContext.formatPrice(dayCost - dailyBudget)}"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isUnderBudget) Color(0xFF15803D) else Color(0xFFB91C1C)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (isUnderBudget) Color(0xFF16A34A) else Color(0xFFD32F2F),
            trackColor = Color(0xFFF3F4F6),
          )
        }
      }
    }

    // 7-Day Day Selector Bar
    item {
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        itemsIndexed(weeklyPlan) { index, day ->
          val isSelected = index == selectedDayIndex
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) Color(0xFFD32F2F) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
            modifier = Modifier
              .clickable { onSelectDay(index) }
              .testTag("day_tab_$index")
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (language == AppLanguage.ID) day.dayNameId else day.dayNameEn,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Color(0xFF292524)
                )
              )
              Text(
                text = localeContext.formatPrice(day.totalEstimatedCost),
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  color = if (isSelected) Color(0xFFFEF2F2) else Color(0xFF78716C)
                )
              )
            }
          }
        }
      }
    }

    // Action Chips: Batch Cooking Toggle & Regenerate AI Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = isBatchCookFilterActive,
          onClick = onToggleBatchCookFilter,
          label = {
            Text(
              text = if (language == AppLanguage.ID) "Masak 1x Makan 3x" else "Cook Once, Eat 3x",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
          },
          leadingIcon = {
            MasTempeAvatar(size = 20.dp)
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFFEF3C7),
            selectedLabelColor = Color(0xFFB45309)
          )
        )

        Button(
          onClick = onGeneratePlan,
          enabled = !isGenerating,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
          modifier = Modifier.testTag("regenerate_plan_button")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF78350F)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isGenerating) {
              if (language == AppLanguage.ID) "Meracik AI..." else "Thinking AI..."
            } else {
              if (language == AppLanguage.ID) "Menu AI Baru" else "Remix AI"
            },
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF78350F)
            )
          )
        }
      }
    }

    if (errorMessage != null) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
        ) {
          Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB91C1C)),
            modifier = Modifier.padding(12.dp)
          )
        }
      }
    }

    // Meal Cards for Selected Day
    val mealsToShow = currentDay?.meals?.filter {
      if (isBatchCookFilterActive) it.isBatchCook else true
    } ?: emptyList()

    if (mealsToShow.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            DekTelurAvatar(size = 48.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (language == AppLanguage.ID) "Tidak ada menu sesuai filter." else "No meals matching filter.",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    } else {
      items(mealsToShow) { meal ->
        AnimatedVisibility(
          visible = true,
          enter = fadeIn() + slideInVertically()
        ) {
          MealCard(
            meal = meal,
            localeContext = localeContext,
            language = language,
            onClick = { onSelectMeal(meal) }
          )
        }
      }
    }

    // Small Companions Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        MasTempeAvatar(size = 32.dp)
        Spacer(modifier = Modifier.width(12.dp))
        DekTelurAvatar(size = 32.dp)
        Spacer(modifier = Modifier.width(12.dp))
        MbakCabeAvatar(size = 32.dp)
      }
    }

    // Nutrition Disclaimer Card at the bottom of the plan
    item {
      NutritionDisclaimerCard(language = language)
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun MealCard(
  meal: Meal,
  localeContext: LocaleContext,
  language: AppLanguage,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("meal_card_${meal.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Meal Type tag + Batch cook tag + Price
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFEF3C7))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = AppStrings.formatMealType(meal.mealType, language),
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFFB45309)
              )
            )
          }

          if (meal.isBatchCook) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDCFCE7))
                .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (language == AppLanguage.ID) "3x Makan" else "3x Batch",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF15803D)
                )
              )
            }
          }
        }

        Text(
          text = localeContext.formatPrice(meal.estimatedCost),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            color = Color(0xFFD32F2F)
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Meal Name & Description
      Text(
        text = if (language == AppLanguage.ID) meal.name else meal.nameEn,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = Color(0xFF1F2937)
        )
      )
      Text(
        text = if (language == AppLanguage.ID) meal.description else meal.descriptionEn,
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF6B7280),
          lineHeight = 17.sp
        ),
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
      )

      // Time & Tools
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${meal.prepTimeMinutes} min",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = Color(0xFF4B5563))
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.Restaurant,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = meal.toolsNeeded.firstOrNull()?.let { AppStrings.formatTool(it, language) } ?: (if (language == AppLanguage.ID) "Wajan" else "Pan"),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = Color(0xFF4B5563))
          )
        }

        if (meal.substitutions.isNotEmpty()) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = null,
              tint = Color(0xFF16A34A),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (language == AppLanguage.ID) "Ada Subtitusi" else "Substitutes",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
              )
            )
          }
        }
      }

      // Nutrition Balance Bar
      BalanceBar(
        proteinGrams = meal.proteinGrams,
        carbsGrams = meal.carbsGrams,
        veggiesGrams = meal.veggiesGrams,
        language = language
      )
    }
  }
}

@Preview(name = "Weekly Plan Screen", showBackground = true, showSystemUi = true)
@Composable
fun WeeklyPlanScreenPreview() {
  DapurRantauTheme {
    WeeklyPlanScreen(
      weeklyPlan = emptyList(),
      selectedDayIndex = 0,
      dailyBudget = 25000.0,
      localeContext = LocaleContext(),
      language = AppLanguage.ID,
      mascotState = MascotState.IDLE,
      isGenerating = false,
      errorMessage = null,
      isBatchCookFilterActive = false,
      onSelectDay = {},
      onSelectMeal = {},
      onGeneratePlan = {},
      onToggleBatchCookFilter = {},
      onOpenOnboarding = {}
    )
  }
}

