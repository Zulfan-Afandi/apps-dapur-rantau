package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FridgeScanResult
import com.example.data.model.MascotState
import com.example.data.model.Meal
import com.example.ui.components.DekTelurAvatar
import com.example.ui.components.KukiMascot
import com.example.ui.components.MasTempeAvatar
import com.example.ui.components.NutritionDisclaimerCard
import java.io.InputStream

import com.example.data.model.LocaleContext

@Composable
fun FridgeScanScreen(
  scanResult: FridgeScanResult?,
  isScanning: Boolean,
  errorMessage: String?,
  localeContext: LocaleContext,
  language: AppLanguage,
  onScanImage: (Bitmap) -> Unit,
  onSelectMeal: (Meal) -> Unit
) {
  val context = LocalContext.current
  var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

  // Zero-permission modern Android Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    uri?.let {
      try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(it)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        previewBitmap = bitmap
        if (bitmap != null) {
          onScanImage(bitmap)
        }
      } catch (e: Exception) {
        // Fallback
      }
    }
  }

  // Fallback demo preset bitmap generator
  fun triggerSamplePreset(name: String) {
    val conf = Bitmap.Config.ARGB_8888
    val bmp = Bitmap.createBitmap(300, 300, conf)
    val canvas = android.graphics.Canvas(bmp)
    canvas.drawColor(android.graphics.Color.parseColor("#FFFBEB"))
    previewBitmap = bmp
    onScanImage(bmp)
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Hero Card
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
                text = if (language == AppLanguage.ID) "📸 Scan Kulkas & Meja Kos" else "📸 Fridge & Pantry Scan",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFD32F2F)
                )
              )
              Text(
                text = if (language == AppLanguage.ID)
                  "Foto bahan yang ada, AI langsung ciptakan resep lezat!"
                else
                  "Snap a photo of available ingredients for instant recipes!",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C)),
                modifier = Modifier.padding(top = 2.dp)
              )
            }
            KukiMascot(
              state = if (isScanning) MascotState.COOKING else MascotState.IDLE,
              size = 64.dp
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Image preview box if selected
          if (previewBitmap != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                bitmap = previewBitmap!!.asImageBitmap(),
                contentDescription = "Selected Ingredients",
                modifier = Modifier.fillMaxSize()
              )
              if (isScanning) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = if (language == AppLanguage.ID) "Si Kuki sedang mengenali bahan..." else "Analyzing ingredients...",
                      style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          // Action: Pick Visual Media
          Button(
            onClick = {
              photoPickerLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                  ActivityResultContracts.PickVisualMedia.ImageOnly
                )
              )
            },
            enabled = !isScanning,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("pick_fridge_photo_button")
          ) {
            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (language == AppLanguage.ID) "Pilih / Foto Bahan Kulkas" else "Take / Pick Photo",
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Demo Preset Chips (Instant test on emulator)
          Text(
            text = if (language == AppLanguage.ID) "Atau coba bahan khas anak kos:" else "Or try typical dorm ingredients:",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C), fontSize = 11.sp)
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFEF3C7))
                .clickable { triggerSamplePreset("Telur & Tempe") }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (language == AppLanguage.ID) "🥚 2 Telur + Tempe" else "🥚 2 Eggs + Tempeh",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309),
                  fontSize = 11.sp
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDCFCE7))
                .clickable { triggerSamplePreset("Nasi & Bawang") }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (language == AppLanguage.ID) "🍚 Sisa Nasi + Bumbu" else "🍚 Leftover Rice + Garlic",
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

    // Results from Gemini
    if (scanResult != null) {
      // Detected ingredients pills
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (language == AppLanguage.ID) "Bahan Terdeteksi di Foto:" else "Detected Ingredients:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
              )
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(scanResult.detectedIngredients) { ing ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = ing,
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFF374151),
                      fontSize = 12.sp
                    )
                  )
                }
              }
            }

            if (scanResult.cheapMarketAdditions.isNotEmpty()) {
              Spacer(modifier = Modifier.height(12.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFFFEF3C7))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (language == AppLanguage.ID)
                      "Beli tambahan murah di warung: ${scanResult.cheapMarketAdditions.joinToString(", ")}"
                    else
                      "Cheap additions from local market: ${if (scanResult.cheapMarketAdditionsEn.isNotEmpty()) scanResult.cheapMarketAdditionsEn.joinToString(", ") else scanResult.cheapMarketAdditions.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = Color(0xFF92400E),
                      fontSize = 11.sp,
                      lineHeight = 16.sp
                    )
                  )
                }
              }
            }
          }
        }
      }

      // Recommended Recipes list
      item {
        Text(
          text = if (language == AppLanguage.ID) "Resep Rekomendasi Si Kuki" else "AI Suggested Recipes",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
        )
      }

      items(scanResult.suggestedMeals) { meal ->
        MealCard(
          meal = meal,
          localeContext = localeContext,
          language = language,
          onClick = { onSelectMeal(meal) }
        )
      }
    }

    item {
      NutritionDisclaimerCard(language = language)
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
