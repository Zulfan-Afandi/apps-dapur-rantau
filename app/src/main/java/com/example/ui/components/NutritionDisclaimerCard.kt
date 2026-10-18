package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

@Composable
fun NutritionDisclaimerCard(
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFFFEF3C7).copy(alpha = 0.8f))
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Info,
      contentDescription = "Nutrition Info",
      tint = Color(0xFFD97706),
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = if (language == AppLanguage.ID) {
        "Panduan gizi seimbang umum (protein, karbohidrat, sayur, perkiraan kalori). Bukan saran medis klinis."
      } else {
        "General balanced-eating guidance only (protein, carbs, vegetables, rough calories). No medical claims."
      },
      style = MaterialTheme.typography.bodySmall.copy(
        fontSize = 11.sp,
        lineHeight = 15.sp,
        color = Color(0xFF92400E)
      )
    )
  }
}
