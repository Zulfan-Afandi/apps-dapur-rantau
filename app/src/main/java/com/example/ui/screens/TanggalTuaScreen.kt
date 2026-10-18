package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.LocaleContext
import com.example.data.model.MascotState
import com.example.data.model.Meal
import com.example.data.model.TanggalTuaPlan
import com.example.ui.components.KukiMascot
import com.example.ui.components.MasTempeAvatar
import com.example.ui.components.NutritionDisclaimerCard

@Composable
fun TanggalTuaScreen(
  tanggalTuaPlan: TanggalTuaPlan?,
  localeContext: LocaleContext,
  language: AppLanguage,
  isGenerating: Boolean,
  onGenerate: (Double, Int) -> Unit,
  onSelectMeal: (Meal) -> Unit
) {
  var moneyInput by remember { mutableStateOf(tanggalTuaPlan?.remainingBudget?.toString() ?: (localeContext.currency == "IDR").let { if (it) "25000" else "15" }) }
  var daysInput by remember { mutableStateOf(tanggalTuaPlan?.daysLeft?.toString() ?: "3") }

  val money = moneyInput.toDoubleOrNull() ?: 20.0
  val days = daysInput.toIntOrNull() ?: 3
  val dailyAllowance = if (days > 0) money / days else money

  val mascotMood = when {
    isGenerating -> MascotState.COOKING
    dailyAllowance < (if (localeContext.currency == "IDR") 10000.0 else 5.0) -> MascotState.WORRIED
    else -> MascotState.HAPPY
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Hero Card: Survival Budget Calculator
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
              Text(
                text = if (language == AppLanguage.ID) "🚨 Mode Tanggal Tua" else "🚨 End-of-Month Survival",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFD32F2F)
                )
              )
              Text(
                text = if (language == AppLanguage.ID)
                  "Menu paling hemat untuk sisa uang saku hingga kiriman/gaji tiba!"
                else
                  "Ultra-budget nutritious recipes until next payday!",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C)),
                modifier = Modifier.padding(top = 2.dp)
              )
            }
            KukiMascot(state = mascotMood, size = 64.dp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Two inputs: Remaining Money & Days left
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedTextField(
              value = moneyInput,
              onValueChange = { moneyInput = it },
              label = { Text(if (language == AppLanguage.ID) "Sisa Uang (${localeContext.currency})" else "Remaining (${localeContext.currency})") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f).testTag("tangtua_money_input")
            )
            OutlinedTextField(
              value = daysInput,
              onValueChange = { daysInput = it.filter { c -> c.isDigit() } },
              label = { Text(if (language == AppLanguage.ID) "Jumlah Hari" else "Days Left") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(0.7f).testTag("tangtua_days_input")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Daily Allowance Result Pill
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFFFEF3C7))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (language == AppLanguage.ID) "Jatah Belanja Maksimal:" else "Daily Limit:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF78716C))
              )
              Text(
                text = "${localeContext.formatPrice(dailyAllowance)} ${if (language == AppLanguage.ID) "/ hari" else "/ day"}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFB45309)
                )
              )
            }

            Button(
              onClick = { onGenerate(money, days) },
              enabled = !isGenerating,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
              modifier = Modifier.testTag("generate_tangtua_button")
            ) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (language == AppLanguage.ID) "Cari Menu" else "Find Menu")
            }
          }
        }
      }
    }

    // Survival Tips Section
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A))),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            MasTempeAvatar(size = 28.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (language == AppLanguage.ID) "Trik Bertahan Hidup Anak Rantau:" else "Student Survival Tips:",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
            )
          }
          Spacer(modifier = Modifier.height(8.dp))

          val tips = if (language == AppLanguage.ID) {
            tanggalTuaPlan?.survivalTips ?: listOf(
              "Masak karbohidrat dan lauk dasar sekaligus untuk 3 hari untuk kurangi limbah.",
              "Belanja di pasar tradisional pagi-pagi untuk dapat sayur murah berkualitas.",
              "Gunakan telur, tahu, dan tempe untuk protein tinggi dengan harga hemat."
            )
          } else {
            tanggalTuaPlan?.survivalTipsEn ?: listOf(
              "Batch cook carbohydrates and protein base for 3 days to eliminate waste.",
              "Shop at local open-air markets early morning for discounted bundles.",
              "Use eggs, lentils, or tofu for satisfying high protein at low cost."
            )
          }

          tips.forEach { tip ->
            Row(modifier = Modifier.padding(vertical = 3.dp)) {
              Text("💡", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = tip,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F), lineHeight = 16.sp)
              )
            }
          }
        }
      }
    }

    // Tanggal Tua Menu Cards
    item {
      Text(
        text = if (language == AppLanguage.ID) "Menu Penyelamat Hemat & Bergizi" else "Budget Savior Recipes",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
      )
    }

    val meals = tanggalTuaPlan?.meals ?: emptyList()
    items(meals) { meal ->
      MealCard(
        meal = meal,
        localeContext = localeContext,
        language = language,
        onClick = { onSelectMeal(meal) }
      )
    }

    item {
      NutritionDisclaimerCard(language = language)
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
