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
<fill in after checking how the project starts, for example: npm install, then npm run dev, with GEMINI_API_KEY in a .env file>

## Notes and limitations
- Prices are estimates and can be edited by the user.
- Nutrition guidance is general, not medical advice.
- Store data in the demo is curated sample data for Malang, Indonesia.
- Free-tier Gemini API usage is rate-limited.

## Team
JajangBakwan (2 people)
