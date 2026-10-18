package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

@Composable
fun BalanceBar(
  proteinGrams: Int,
  carbsGrams: Int,
  veggiesGrams: Int,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val total = (proteinGrams + carbsGrams + veggiesGrams).coerceAtLeast(1)
  val proteinRatio = proteinGrams.toFloat() / total
  val carbsRatio = carbsGrams.toFloat() / total
  val veggiesRatio = veggiesGrams.toFloat() / total

  val animatedProtein by animateFloatAsState(targetValue = proteinRatio, animationSpec = tween(500), label = "p")
  val animatedCarbs by animateFloatAsState(targetValue = carbsRatio, animationSpec = tween(500), label = "c")
  val animatedVeggies by animateFloatAsState(targetValue = veggiesRatio, animationSpec = tween(500), label = "v")

  val proteinColor = Color(0xFFE53935)
  val carbsColor = Color(0xFFF59E0B)
  val veggiesColor = Color(0xFF16A34A)

  Column(modifier = modifier.fillMaxWidth()) {
    // Top Row indicators
    Row(
      modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      LegendItem(
        color = proteinColor,
        label = if (language == AppLanguage.ID) "Protein" else "Protein",
        grams = "${proteinGrams}g"
      )
      LegendItem(
        color = carbsColor,
        label = if (language == AppLanguage.ID) "Karbo" else "Carbs",
        grams = "${carbsGrams}g"
      )
      LegendItem(
        color = veggiesColor,
        label = if (language == AppLanguage.ID) "Sayur" else "Veggies",
        grams = "${veggiesGrams}g"
      )
    }

    // Segmented Progress Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(Color(0xFFE5E7EB))
    ) {
      if (animatedProtein > 0f) {
        Box(
          modifier = Modifier
            .weight(animatedProtein.coerceAtLeast(0.05f))
            .height(10.dp)
            .background(proteinColor)
        )
      }
      if (animatedCarbs > 0f) {
        Box(
          modifier = Modifier
            .weight(animatedCarbs.coerceAtLeast(0.05f))
            .height(10.dp)
            .background(carbsColor)
        )
      }
      if (animatedVeggies > 0f) {
        Box(
          modifier = Modifier
            .weight(animatedVeggies.coerceAtLeast(0.05f))
            .height(10.dp)
            .background(veggiesColor)
        )
      }
    }
  }
}

@Composable
private fun LegendItem(
  color: Color,
  label: String,
  grams: String
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Text(
      text = " $label ",
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF4B5563))
    )
    Text(
      text = grams,
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
    )
  }
}
