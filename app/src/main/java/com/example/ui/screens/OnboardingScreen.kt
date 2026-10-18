package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.DapurRantauTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DemoLocationPreset
import com.example.data.model.DemoLocations
import com.example.data.model.KitchenSetup
import com.example.data.model.LocaleContext
import com.example.data.model.MascotState
import com.example.data.model.UserPreferences
import com.example.ui.components.KukiMascot

@Composable
fun OnboardingScreen(
  currentPreferences: UserPreferences,
  localeContext: LocaleContext,
  language: AppLanguage,
  onComplete: (UserPreferences) -> Unit,
  onApplyPreset: (DemoLocationPreset) -> Unit,
  onClose: () -> Unit
) {
  BackHandler { onClose() }

  var step by remember { mutableIntStateOf(1) }
  var budget by remember { mutableDoubleStateOf(currentPreferences.dailyBudget) }
  var budgetInput by remember { mutableStateOf(if (currentPreferences.dailyBudget % 1.0 == 0.0) currentPreferences.dailyBudget.toLong().toString() else currentPreferences.dailyBudget.toString()) }
  var setup by remember { mutableStateOf(currentPreferences.kitchenSetup) }
  var mealsCount by remember { mutableIntStateOf(currentPreferences.mealsPerDay) }
  var cuisineOverride by remember { mutableStateOf(currentPreferences.cuisineOverride) }

  var isVegetarian by remember { mutableStateOf(currentPreferences.isVegetarian) }
  var isVegan by remember { mutableStateOf(currentPreferences.isVegan) }
  var isHalal by remember { mutableStateOf(currentPreferences.isHalal) }
  var isNoPork by remember { mutableStateOf(currentPreferences.isNoPork) }
  var isNoBeef by remember { mutableStateOf(currentPreferences.isNoBeef) }
  var allergies by remember { mutableStateOf(currentPreferences.allergies) }

  val activePreset = DemoLocations.PRESETS.find { it.city.equals(localeContext.city, ignoreCase = true) }
    ?: DemoLocations.PRESETS[0]

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
      .padding(horizontal = 24.dp, vertical = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header with Mascot and Close Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Dapur Rantau",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = Color(0xFFD32F2F)
            )
          )
          Text(
            text = if (language == AppLanguage.ID) "Langkah $step dari 3" else "Step $step of 3",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C))
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          KukiMascot(state = MascotState.WAVING, size = 56.dp)
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(
            onClick = onClose,
            modifier = Modifier.testTag("onboarding_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF78716C)
            )
          }
        }
      }

      // Progress Dots
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        for (i in 1..3) {
          Box(
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .size(if (i == step) 24.dp else 10.dp, 10.dp)
              .clip(CircleShape)
              .background(if (i <= step) Color(0xFFD32F2F) else Color(0xFFE7E0D3))
          )
        }
      }

      AnimatedContent(targetState = step, label = "step_content") { currentStep ->
        when (currentStep) {
          1 -> {
            // STEP 1: Budget in Local Currency
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Budget makan harian di ${localeContext.city}?" else "Daily food budget in ${localeContext.city}?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                textAlign = TextAlign.Center
              )
              Text(
                text = if (language == AppLanguage.ID)
                  "Mata uang: ${localeContext.currency}. Presets disesuaikan dengan negara setempat."
                else
                  "Local currency: ${localeContext.currency}. Presets adapt to country standard.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF78716C)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
              )

              // Location Quick Switcher for Demo
              Text(
                text = if (language == AppLanguage.ID) "Pilih Lokasi Demo:" else "Select Demo Location:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
              )
              Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                DemoLocations.PRESETS.forEach { p ->
                  val isSelected = p.city == localeContext.city
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(if (isSelected) Color(0xFF16A34A) else Color.White)
                      .border(1.dp, if (isSelected) Color(0xFF16A34A) else Color(0xFFE7E0D3), RoundedCornerShape(12.dp))
                      .clickable {
                        onApplyPreset(p)
                        budget = p.defaultBudget
                        budgetInput = if (p.defaultBudget % 1.0 == 0.0) p.defaultBudget.toLong().toString() else p.defaultBudget.toString()
                      }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = p.city,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF292524)
                      )
                    )
                  }
                }
              }

              // Budget Card
              Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = if (language == AppLanguage.ID) "Target Anggaran / Hari" else "Target Daily Budget",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C))
                  )
                  Text(
                    text = localeContext.formatPrice(budget),
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = Color(0xFFD32F2F)
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                  )

                  // Allow typing custom amount
                  OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { input ->
                      budgetInput = input
                      input.toDoubleOrNull()?.let { budget = it }
                    },
                    label = { Text(if (language == AppLanguage.ID) "Ketik Nominal (${localeContext.currency})" else "Custom Amount (${localeContext.currency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1F2937)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedTextColor = Color(0xFF1F2937),
                      unfocusedTextColor = Color(0xFF1F2937),
                      focusedContainerColor = Color.White,
                      unfocusedContainerColor = Color.White,
                      cursorColor = Color(0xFFD32F2F),
                      focusedBorderColor = Color(0xFFD32F2F),
                      unfocusedBorderColor = Color(0xFFD1D5DB),
                      focusedLabelColor = Color(0xFFD32F2F),
                      unfocusedLabelColor = Color(0xFF6B7280)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("custom_budget_input")
                  )

                  Spacer(modifier = Modifier.height(14.dp))

                  // 4 Sensible Presets for this country
                  Text(
                    text = if (language == AppLanguage.ID) "Atau pilih preset cepat:" else "Or choose quick preset:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C), fontSize = 11.sp),
                    modifier = Modifier.fillMaxWidth()
                  )
                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    activePreset.budgetPresets.forEach { presetVal ->
                      val isSelected = budget == presetVal
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(12.dp))
                          .background(if (isSelected) Color(0xFFD32F2F) else Color(0xFFF7F1E5))
                          .clickable {
                            budget = presetVal
                            budgetInput = if (presetVal % 1.0 == 0.0) presetVal.toLong().toString() else presetVal.toString()
                          }
                          .padding(horizontal = 10.dp, vertical = 8.dp)
                      ) {
                        Text(
                          text = localeContext.formatPrice(presetVal),
                          style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF4A3E3D)
                          )
                        )
                      }
                    }
                  }
                }
              }
            }
          }
          2 -> {
            // STEP 2: Kitchen Setup
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Peralatan masak yang tersedia?" else "Your kitchen tools?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                textAlign = TextAlign.Center
              )
              Text(
                text = if (language == AppLanguage.ID)
                  "Resep akan disesuaikan otomatis dengan alat yang kamu punya."
                else
                  "Recipes will strictly adapt to what tools you have available.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF78716C)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
              )

              KitchenSetup.entries.forEach { option ->
                val isSelected = setup == option
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFFEF2F2) else Color.White
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFFD32F2F) else Color(0xFFE7E0D3),
                      shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { setup = option }
                    .testTag("setup_${option.name.lowercase()}")
                ) {
                  Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = when (option) {
                        KitchenSetup.RICE_COOKER_ONLY -> "🍚"
                        KitchenSetup.ONE_BURNER -> "🔥"
                        KitchenSetup.FULL_KITCHEN -> "🍳"
                      },
                      fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = if (language == AppLanguage.ID) option.labelId else option.labelEn,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                      )
                    }
                    if (isSelected) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color(0xFFD32F2F)
                      )
                    }
                  }
                }
              }
            }
          }
          3 -> {
            // STEP 3: Cuisine Override & Dietary Toggles
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Preferensi Masakan & Diet" else "Cuisine & Dietary Toggles",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                textAlign = TextAlign.Center
              )
              Text(
                text = if (language == AppLanguage.ID)
                  "Gemini akan patuh 100% pada pantangan makananmu."
                else
                  "Gemini strictly abides by all dietary restrictions.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF78716C)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
              )

              // Cuisine Input
              OutlinedTextField(
                value = cuisineOverride,
                onValueChange = { cuisineOverride = it },
                label = { Text(if (language == AppLanguage.ID) "Gaya Masakan (misal: Local, Indian, Italian)" else "Cuisine (e.g. Local, Indian, Mexican)") },
                modifier = Modifier.fillMaxWidth().testTag("cuisine_override_input")
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Dietary checkboxes
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.White)
                  .padding(14.dp)
              ) {
                Text(
                  text = if (language == AppLanguage.ID) "Pantangan Diet:" else "Dietary Rules:",
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isVegetarian, onCheckedChange = { isVegetarian = it })
                  Text("Vegetarian", modifier = Modifier.clickable { isVegetarian = !isVegetarian })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isVegan, onCheckedChange = { isVegan = it })
                  Text("Vegan", modifier = Modifier.clickable { isVegan = !isVegan })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isHalal, onCheckedChange = { isHalal = it })
                  Text("Halal", modifier = Modifier.clickable { isHalal = !isHalal })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isNoPork, onCheckedChange = { isNoPork = it })
                  Text(if (language == AppLanguage.ID) "Tanpa Daging Babi (No Pork)" else "No Pork", modifier = Modifier.clickable { isNoPork = !isNoPork })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isNoBeef, onCheckedChange = { isNoBeef = it })
                  Text(if (language == AppLanguage.ID) "Tanpa Daging Sapi (No Beef)" else "No Beef", modifier = Modifier.clickable { isNoBeef = !isNoBeef })
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = allergies,
                  onValueChange = { allergies = it },
                  label = { Text(if (language == AppLanguage.ID) "Alergi (misal: Kacang, Seafood)" else "Allergies (e.g. Peanuts, Dairy)") },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Navigation Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (step > 1) {
          OutlinedButton(
            onClick = { step-- },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("onboarding_back_button")
          ) {
            Text(if (language == AppLanguage.ID) "Kembali" else "Back")
          }
        } else {
          OutlinedButton(
            onClick = onClose,
            shape = RoundedCornerShape(16.dp)
          ) {
            Text(if (language == AppLanguage.ID) "Nanti Saja" else "Skip")
          }
        }

        Button(
          onClick = {
            if (step < 3) {
              step++
            } else {
              onComplete(
                currentPreferences.copy(
                  dailyBudget = budget,
                  kitchenSetup = setup,
                  mealsPerDay = mealsCount,
                  cuisineOverride = cuisineOverride,
                  isVegetarian = isVegetarian,
                  isVegan = isVegan,
                  isHalal = isHalal,
                  isNoPork = isNoPork,
                  isNoBeef = isNoBeef,
                  allergies = allergies,
                  isOnboarded = true
                )
              )
            }
          },
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
          modifier = Modifier.testTag("onboarding_next_button")
        ) {
          Text(
            text = if (step < 3) {
              if (language == AppLanguage.ID) "Lanjut" else "Next"
            } else {
              if (language == AppLanguage.ID) "Mulai Masak!" else "Let's Cook!"
            },
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Preview(name = "Onboarding Screen", showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
  DapurRantauTheme {
    OnboardingScreen(
      currentPreferences = com.example.data.model.UserPreferences(),
      localeContext = LocaleContext(),
      language = AppLanguage.ID,
      onComplete = {},
      onApplyPreset = {},
      onClose = {}
    )
  }
}

