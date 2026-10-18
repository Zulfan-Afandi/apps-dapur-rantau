package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.LocaleContext
import com.example.data.model.Meal
import com.example.ui.components.BalanceBar
import com.example.ui.components.DekTelurAvatar
import com.example.ui.components.MasTempeAvatar
import com.example.ui.components.NutritionDisclaimerCard
import com.example.ui.theme.AppStrings

@Composable
fun RecipeDetailScreen(
  meal: Meal,
  localeContext: LocaleContext,
  language: AppLanguage,
  onBack: () -> Unit
) {
  BackHandler { onBack() }

  val checkedSteps = remember { mutableStateListOf<Int>() }
  val instructions = if (language == AppLanguage.ID) meal.instructions else meal.instructionsEn

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("recipe_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color(0xFF292524)
        )
      }
      Text(
        text = if (language == AppLanguage.ID) "Panduan Masak" else "Cooking Guide",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.weight(1f)
      )
      MasTempeAvatar(size = 32.dp)
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Hero Card
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = if (language == AppLanguage.ID) meal.name else meal.nameEn,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              color = Color(0xFF1F2937)
            )
          )
          Text(
            text = if (language == AppLanguage.ID) meal.description else meal.descriptionEn,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)),
            modifier = Modifier.padding(top = 4.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Meta Row: Cost, Time, Tools
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFFFEF3C7))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Estimasi Biaya" else "Est. Cost",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFFB45309))
              )
              Text(
                text = localeContext.formatPrice(meal.estimatedCost),
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFB45309)
                )
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Waktu Masak" else "Cook Time",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFFB45309))
              )
              Text(
                text = "${meal.prepTimeMinutes} Min",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFB45309)
                )
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Alat Masak" else "Tools",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFFB45309))
              )
              Text(
                text = meal.toolsNeeded.firstOrNull()?.let { AppStrings.formatTool(it, language) } ?: (if (language == AppLanguage.ID) "Wajan" else "Pan"),
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309)
                )
              )
            }
          }

          if (meal.isBatchCook && meal.batchTip.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFDCFCE7))
                .padding(10.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                DekTelurAvatar(size = 24.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (language == AppLanguage.ID)
                    "Tips Batch: ${meal.batchTip}"
                  else
                    "Batch Tip: ${if (meal.batchTipEn.isNotEmpty()) meal.batchTipEn else meal.batchTip}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF15803D),
                    fontSize = 11.sp
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Nutrition bar
          BalanceBar(
            proteinGrams = meal.proteinGrams,
            carbsGrams = meal.carbsGrams,
            veggiesGrams = meal.veggiesGrams,
            language = language
          )
        }
      }

      // Ingredients Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = if (language == AppLanguage.ID) "Bahan & Estimasi Harga Pasar" else "Ingredients & Est. Prices",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
          )
          Spacer(modifier = Modifier.height(10.dp))

          meal.ingredients.forEach { ing ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = if (language == AppLanguage.ID) ing.name else ing.nameEn,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                  text = "${if (language == AppLanguage.ID) ing.amount else ing.amountEn} • ${if (language == AppLanguage.ID) ing.category.labelId else ing.category.labelEn}",
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C), fontSize = 11.sp)
                )
              }
              Text(
                text = localeContext.formatPrice(ing.estPrice),
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFD32F2F)
                )
              )
            }
          }
        }
      }

      // Ingredient Substitutions Card (keeps protein similar when prices rise)
      if (meal.substitutions.isNotEmpty()) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC))),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (language == AppLanguage.ID) "Subtitusi Hemat Protein Seimbang" else "Budget Protein Substitutes",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF15803D)
                )
              )
            }
            Text(
              text = if (language == AppLanguage.ID)
                "Bila harga bahan naik, ganti dengan alternatif berikut tanpa mengurangi asupan protein:"
              else
                "When ingredient prices rise, swap with these to preserve protein intake:",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF166534), fontSize = 11.sp),
              modifier = Modifier.padding(vertical = 6.dp)
            )

            meal.substitutions.forEach { sub ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White)
                  .padding(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "${sub.original} ➔ ${sub.substitute}",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = Color(0xFF15803D)
                    )
                    Text(
                      text = if (language == AppLanguage.ID) sub.reasonId else sub.reasonEn,
                      style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4B5563), fontSize = 11.sp)
                    )
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFDCFCE7))
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = sub.estSavings,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        fontSize = 11.sp
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Step-by-Step Instructions
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = if (language == AppLanguage.ID) "Langkah Memasak Praktis" else "Cooking Steps",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
          )
          Spacer(modifier = Modifier.height(12.dp))

          instructions.forEachIndexed { index, stepText ->
            val isDone = checkedSteps.contains(index)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDone) Color(0xFFF3F4F6) else Color(0xFFFFFBF2))
                .clickable {
                  if (isDone) checkedSteps.remove(index) else checkedSteps.add(index)
                }
                .padding(12.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isDone) Color(0xFF16A34A) else Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (language == AppLanguage.ID) "Langkah ${index + 1}" else "Step ${index + 1}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) Color(0xFF9CA3AF) else Color(0xFFD32F2F)
                  )
                )
                Text(
                  text = stepText,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDone) Color(0xFF9CA3AF) else Color(0xFF1F2937),
                    lineHeight = 18.sp
                  )
                )
              }
            }
          }
        }
      }

      NutritionDisclaimerCard(language = language)

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
