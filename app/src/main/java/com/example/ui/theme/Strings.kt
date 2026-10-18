package com.example.ui.theme

import com.example.data.model.AppLanguage
import com.example.data.model.StoreType

object AppStrings {

  fun appTitle(lang: AppLanguage) = "Dapur Rantau"

  fun subtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Masak hemat, sehat, dan belanja pasar di mana saja"
    AppLanguage.EN -> "Budget, healthy home cooking & local market finder"
  }

  fun tabPlan(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Menu"
    AppLanguage.EN -> "Menu"
  }

  fun tabShopping(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Belanja"
    AppLanguage.EN -> "Shop"
  }

  fun tabMap(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Peta"
    AppLanguage.EN -> "Map"
  }

  fun tabTanggalTua(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Tgl Tua"
    AppLanguage.EN -> "End Month"
  }

  fun tabFridge(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Scan"
    AppLanguage.EN -> "Scan"
  }

  fun searchThisArea(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Cari di Area Ini"
    AppLanguage.EN -> "Search this area"
  }

  fun useMyLocation(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Gunakan Lokasi Saya"
    AppLanguage.EN -> "Use my location"
  }

  fun demoLocations(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Pilih Demo Kota Global"
    AppLanguage.EN -> "Demo Global Locations"
  }

  fun openNow(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Buka Sekarang"
    AppLanguage.EN -> "Open Now"
  }

  fun closed(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Tutup"
    AppLanguage.EN -> "Closed"
  }

  fun directions(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Buka Rute di Google Maps"
    AppLanguage.EN -> "Get Directions"
  }

  fun routeShort(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Rute"
    AppLanguage.EN -> "Directions"
  }

  fun estConfidence(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Akurasi Estimasi:"
    AppLanguage.EN -> "Estimate Confidence:"
  }

  fun nutritionDisclaimer(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Panduan gizi seimbang umum (protein, karbohidrat, sayur, perkiraan kalori). Bukan saran medis klinis."
    AppLanguage.EN -> "General balanced-eating guidance only (protein, carbs, vegetables, rough calories). No medical claims."
  }

  fun formatMealType(mealType: String, lang: AppLanguage): String {
    return when (mealType.uppercase()) {
      "BREAKFAST", "SARAPAN" -> if (lang == AppLanguage.ID) "Sarapan" else "Breakfast"
      "LUNCH", "MAKAN SIANG" -> if (lang == AppLanguage.ID) "Makan Siang" else "Lunch"
      "DINNER", "MAKAN MALAM" -> if (lang == AppLanguage.ID) "Makan Malam" else "Dinner"
      else -> mealType
    }
  }

  fun formatTool(tool: String, lang: AppLanguage): String {
    if (lang == AppLanguage.ID) return tool
    return when (tool.lowercase().trim()) {
      "panci / magic com" -> "Pot / Rice Cooker"
      "wajan", "wajan / teflon" -> "Frying Pan / Skillet"
      "wajan / rice cooker" -> "Pan / Rice Cooker"
      "panci" -> "Pot"
      "magic com" -> "Rice Cooker"
      "tawa / frying pan" -> "Tawa / Frying Pan"
      "pressure cooker / pot" -> "Pressure Cooker / Pot"
      "microwave / oven" -> "Microwave / Oven"
      "stock pot" -> "Stock Pot"
      "saucepan" -> "Saucepan"
      else -> tool
    }
  }

  fun formatHours(hours: String?, lang: AppLanguage): String? {
    if (hours == null) return null
    return if (lang == AppLanguage.EN) {
      hours.replace("24 Jam", "24 Hours")
           .replace("Jam", "Hours")
    } else {
      hours.replace("24 Hours", "24 Jam")
    }
  }

  fun formatUnit(amount: String, lang: AppLanguage): String {
    if (lang == AppLanguage.ID) return amount
    return amount
      .replace("1 ikat", "1 bunch")
      .replace("ikat", "bunch")
      .replace("1 buah", "1 pc")
      .replace("buah", "pcs")
      .replace("1/2 papan", "1/2 block")
      .replace("1 papan", "1 block")
      .replace("papan", "block")
      .replace("2 butir", "2 pcs")
      .replace("butir", "pcs")
      .replace("1 batang", "1 stalk")
      .replace("batang", "stalks")
      .replace("Secukupnya", "To taste")
      .replace("secukupnya", "to taste")
  }
}
