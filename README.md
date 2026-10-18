# Dapur Rantau

An AI meal-planning app for people who live alone far from home (students, young workers) and need to eat healthily on a tight budget.

Built for the Google Cloud AI Builder Cup 2026 (Sustainability & Social Impact), by team JajangBakwan.

## The problem
People living far from home often don't know what to cook, how to cook it, or where to buy ingredients, and they have little money and often only a rice cooker. Eating well becomes expensive and wasteful.

## What it does
- **Budget-first onboarding:** daily budget, kitchen setup, diet limits, cuisine
- **7-day balanced meal plan** from Gemini, with cost per day and a protein / carbs / vegetables balance
- **Recipes and substitutions:** step-by-step, with cheaper swaps
- **Shopping list** grouped by store type, with editable prices
- **Nearby store locator** with one-tap directions in Google Maps
- **Cook once, eat three times** mode and **End-of-month** mode (cheapest healthy menu until payday)
- **Fridge scan:** photo of ingredients, get meal ideas

## Google services used
- Gemini API: meal plans, recipes, multimodal fridge scan, structured JSON output
- Google AI Studio: where the app was built and iterated
- Google Cloud Run: deployment
- Google Maps: directions links

## Links
- Live prototype:
- Demo video (3 min): 
- Pitch deck: 

## Screenshots
<add 3-4 images from a /screenshots folder>

## Run locally

### 1. Web Prototype & API Server (for Cloud Run):
```bash
npm install
npm start
```
Buka browser di `http://localhost:8080`.

### 2. Android App (Jetpack Compose):
Buka proyek ini di Android Studio, lalu jalankan di emulator atau perangkat fisik.

## Deploy to Google Cloud Run (Free Tier / $0)

Deploy prototipe langsung ke Cloud Run dengan Google Cloud CLI:
```bash
gcloud run deploy dapur-rantau \
  --source . \
  --region asia-southeast2 \
  --allow-unauthenticated \
  --set-env-vars GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

Atau via **Google Cloud Console** (Browser):
1. Buka [Google Cloud Run Console](https://console.cloud.google.com/run).
2. Klik **"Create Service"** atau **"Deploy Container"**.
3. Pilih **"Continuously deploy from a repository"** lalu sambungkan repositori GitHub ini.
4. Pilih Build type: **Dockerfile**.
5. Centang **"Allow unauthenticated invocations"** agar juri bisa langsung mengakses.
6. Klik **"Create"**. URL publik `https://dapur-rantau-xxxx.a.run.app` akan otomatis aktif!

## Notes and limitations
- Prices are estimates based on traditional markets in Malang, Indonesia, and can be edited by the user.
- Nutrition guidance is general, not medical advice.
- Cloud Run Free Tier covers up to 2 million requests/month without any charges.

## Team
JajangBakwan (2 people)
