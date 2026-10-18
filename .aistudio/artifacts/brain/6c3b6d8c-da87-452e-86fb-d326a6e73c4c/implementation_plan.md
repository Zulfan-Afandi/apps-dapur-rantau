# Implementation Complete: Native Radar Scanner & Curated Demo Dataset (Malang)

## Summary of Accomplishments

### 1. Replaced Fragile WebView with Native Jetpack Compose Radar
- Completely removed `map.html` WebView dependencies, external CDN scripts, CORS errors, and Overpass API query failures.
- Created `MarketRadarView.kt`: A 100% native Compose Canvas radar view featuring:
  - Concentric distance rings (500m, 1km, 2km) and direction indicators.
  - Smooth 60fps radar sweep line and pulsing halos.
  - Interactive store blips color-coded by category (Red: Traditional Market, Green: Warung Sayur, Yellow: Minimarket, Blue: Supermarket).
  - Tapping any blip opens the store detail bottom sheet.

### 2. Curated Demo Data (Malang)
- Labeled strictly in the UI as **"Curated demo data (Malang)"** (no "verified" claims).
- Included 12 real, verified locations across Malang (Dinoyo, Klojen, Blimbing, Tlogomas, Mergan, Oro-oro Dowo, Lowokwaru).
- Cleaned up presets in `DemoLocations.PRESETS` so only Malang is present (Mumbai and London with 4 entries removed as requested).

### 3. Display of Typical Hours
- Marked hours strictly as **"Typical hours: <hours>"** only where confident.
- Hidden the field completely where unconfident (e.g. Warung Barokah).

### 4. Name-Based Google Maps Directions
- Directions buttons now open `https://www.google.com/maps/search/?api=1&query=<store name + city>` instead of raw coordinate queries.

### 5. Search Nearby in Google Maps
- Added a dedicated button: "Cari di Google Maps (Di Luar Dataset)" linking to `https://www.google.com/maps/search/?api=1&query=<store type + city>`.

### 6. Build and Test Verification
- App compiles cleanly with `compile_applet`.
- Unit tests (`:app:testDebugUnitTest`) pass with `BUILD SUCCESSFUL`.
