const express = require('express');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 8080;

app.use(cors());
app.use(express.json({ limit: '15mb' }));
app.use(express.static(path.join(__dirname, 'public')));

const GEMINI_API_KEY = process.env.GEMINI_API_KEY || '';

// Health check endpoint for Cloud Run
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'ok', service: 'dapur-rantau-cloud-run' });
});

app.get('/api/status', (req, res) => {
  res.json({
    hasApiKey: Boolean(GEMINI_API_KEY && GEMINI_API_KEY !== 'MY_GEMINI_API_KEY'),
    environment: process.env.NODE_ENV || 'production',
    service: 'Dapur Rantau - Cloud Run Prototype',
    challenge: 'Google Cloud AI Builder Cup 2026 (Sustainability & Social Impact)'
  });
});

// Helper for calling Gemini REST API
async function callGemini(prompt, systemInstruction = '', model = 'gemini-2.5-flash') {
  if (!GEMINI_API_KEY || GEMINI_API_KEY === 'MY_GEMINI_API_KEY') {
    return null;
  }

  const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${GEMINI_API_KEY}`;
  
  const payload = {
    contents: [
      {
        parts: [{ text: prompt }]
      }
    ],
    generationConfig: {
      responseMimeType: 'application/json',
      temperature: 0.4
    }
  };

  if (systemInstruction) {
    payload.systemInstruction = {
      parts: [{ text: systemInstruction }]
    };
  }

  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Gemini API Error (${response.status}): ${errorText}`);
  }

  const data = await response.json();
  const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
  if (!text) {
    throw new Error('Empty response from Gemini API');
  }

  return JSON.parse(text);
}

// 1. Generate 7-day meal plan
app.post('/api/generate-plan', async (req, res) => {
  const { budget = 25000, kitchenSetup = 'Rice Cooker & 1 Kompor', mealsPerDay = 3, cuisine = 'Jawa Timur / Indonesia', allergies = '', city = 'Malang' } = req.body;

  const prompt = `
Act as an expert budget home cooking and nutrition planner for single Indonesian students living in ${city}.
Context:
- Daily Budget: Rp ${Number(budget).toLocaleString('id-ID')}
- Kitchen Tools: ${kitchenSetup}
- Meals/Day: ${mealsPerDay}
- Cuisine: ${cuisine}
- Dietary Restrictions/Allergies: ${allergies || 'None'}
- Challenge Goal: Sustainability & Social Impact (zero waste, cook in bulk / batch cooking, affordable healthy protein like tempe, tahu, telur, and seasonal local greens).

Create a 7-meal realistic plan. Each meal must include affordable price estimates in IDR, protein/carbs/veggies breakdown, preparation steps, ingredients with market store categories (FARMERS_MARKET, GROCERY_CONVENIENCE, MINIMARKET), and a smart cost-saving ingredient substitution.

Return STRICT JSON array:
[
  {
    "id": "meal-1",
    "name": "Nasi Goreng Kampung Rice Cooker",
    "mealType": "Sarapan / Siang",
    "description": "Nasi goreng hemat praktis dengan telur orak-arik dan sawi hijau.",
    "estimatedCost": 7500,
    "proteinGrams": 14,
    "carbsGrams": 48,
    "veggiesGrams": 25,
    "approxCalories": 390,
    "toolsNeeded": ["Rice Cooker"],
    "prepTimeMinutes": 15,
    "isBatchCook": true,
    "batchTip": "Bisa dibuat dari sisa nasi semalam untuk kurangi sampah makanan.",
    "ingredients": [
      { "name": "Beras / Nasi sisa", "amount": "1 porsi", "estPrice": 2000, "category": "GROCERY_CONVENIENCE" },
      { "name": "Telur ayam", "amount": "1 butir", "estPrice": 2500, "category": "FARMERS_MARKET" },
      { "name": "Sawi hijau", "amount": "1 ikat kecil", "estPrice": 1500, "category": "FARMERS_MARKET" },
      { "name": "Bumbu bawang & kecap", "amount": "secukupnya", "estPrice": 1500, "category": "GROCERY_CONVENIENCE" }
    ],
    "instructions": [
      "Siapkan nasi sisa yang sudah dingin.",
      "Kocok telur, masak di rice cooker dengan mode Cook hingga setengah matang.",
      "Masukkan nasi, potongan sawi, kecap, dan garam. Aduk merata hingga harum."
    ],
    "substitutions": [
      {
        "original": "Daging ayam suwir",
        "substitute": "Telur / Tempe iris",
        "reason": "Protein setara, hemat hingga 40%",
        "savings": "Hemat Rp 5.000"
      }
    ]
  }
]`;

  try {
    const aiResult = await callGemini(prompt);
    if (aiResult) {
      return res.json({ source: 'gemini', meals: Array.isArray(aiResult) ? aiResult : (aiResult.meals || []) });
    }
  } catch (err) {
    console.warn('Gemini call failed or no key, falling back to curated plan:', err.message);
  }

  // Fallback curated sample
  res.json({
    source: 'curated_demo',
    meals: getSampleWeeklyMeals(budget)
  });
});

// 2. Mode Tanggal Tua (End-of-Month Survival)
app.post('/api/tangtua-plan', async (req, res) => {
  const { remainingBudget = 25000, daysLeft = 3, city = 'Malang' } = req.body;
  const dailyAllowance = daysLeft > 0 ? Math.round(remainingBudget / daysLeft) : remainingBudget;

  const prompt = `
Emergency End-of-the-month ultra-budget menu planner for students in ${city}, Indonesia.
Remaining money: Rp ${Number(remainingBudget).toLocaleString('id-ID')} for ${daysLeft} days (Daily limit: Rp ${dailyAllowance.toLocaleString('id-ID')}).
Provide 3 ultra-cheap, highly nutritious survival meals with tempe, tahu, telur, and cheap market vegetables.
Return STRICT JSON array of meal objects like:
[
  {
    "name": "Orek Tempe Basah Gurih",
    "estimatedCost": 4500,
    "approxCalories": 310,
    "proteinGrams": 16,
    "carbsGrams": 32,
    "veggiesGrams": 10,
    "prepTimeMinutes": 12,
    "toolsNeeded": ["Wajan / Kompor 1 Tungku"],
    "isBatchCook": true,
    "batchTip": "Tahan 3 hari dalam wadah tertutup.",
    "description": "Lauk protein murah meriah penolong akhir bulan, manis gurih nagih.",
    "instructions": ["Potong tempe dadu, goreng sebentar.", "Tumis bawang & cabai, beri kecap dan air sedikit.", "Masukkan tempe, aduk hingga bumbu meresap."]
  }
]`;

  try {
    const aiResult = await callGemini(prompt, '', 'gemini-2.5-flash');
    if (aiResult) {
      return res.json({
        source: 'gemini',
        dailyAllowance,
        meals: Array.isArray(aiResult) ? aiResult : (aiResult.meals || [])
      });
    }
  } catch (err) {
    console.warn('Tanggal tua Gemini fallback:', err.message);
  }

  res.json({
    source: 'curated_demo',
    dailyAllowance,
    meals: [
      {
        name: "Nasi Telur Ceplok Kecap Mentega",
        estimatedCost: Math.min(6000, dailyAllowance),
        approxCalories: 410,
        proteinGrams: 14,
        carbsGrams: 55,
        veggiesGrams: 15,
        prepTimeMinutes: 10,
        toolsNeeded: ["Wajan"],
        isBatchCook: false,
        batchTip: "Bisa ditambahkan daun bawang agar lebih harum.",
        description: "Klasik andalan anak kos. Telur ceplok renyah dengan siraman mentega kecap manis.",
        instructions: ["Goreng telur mata sapi sesuai selera kematangan.", "Lelehkan mentega, tuang kecap manis dan sedikit perasan jeruk nipis.", "Siram ke atas nasi hangat dan telur."]
      },
      {
        name: "Orek Tempe Basah Manis Gurih",
        estimatedCost: Math.min(4500, dailyAllowance),
        approxCalories: 320,
        proteinGrams: 18,
        carbsGrams: 35,
        veggiesGrams: 12,
        prepTimeMinutes: 15,
        toolsNeeded: ["Wajan"],
        isBatchCook: true,
        batchTip: "Masak satu papan tempe untuk persediaan makan 3 hari berturut-turut.",
        description: "Tinggi protein nabati, murah, dan tahan lama tanpa kulkas sekalipun.",
        instructions: ["Potong tempe bentuk korek api, goreng setengah matang.", "Tumis bawang merah, bawang putih, dan irisan cabai.", "Beri kecap manis, garam, dan sedikit air, masak sampai meresap."]
      },
      {
        name: "Sayur Bening Bayam & Jagung Manis",
        estimatedCost: Math.min(5000, dailyAllowance),
        approxCalories: 180,
        proteinGrams: 6,
        carbsGrams: 28,
        veggiesGrams: 45,
        prepTimeMinutes: 12,
        toolsNeeded: ["Panci"],
        isBatchCook: false,
        batchTip: "Sayur bayam sebaiknya dihabiskan dalam satu hari.",
        description: "Menjaga stamina dan serat tubuh agar tidak jatuh sakit saat akhir bulan.",
        instructions: ["Rebus air bersama irisan kunci dan bawang merah hingga harum.", "Masukkan potongan jagung manis, rebus hingga empuk.", "Masukkan daun bayam, garam, dan gula secukupnya. Angkat segera."]
      }
    ]
  });
});

// 3. Multimodal Fridge Scan
app.post('/api/fridge-scan', async (req, res) => {
  const { imageBase64 } = req.body;

  if (!imageBase64) {
    return res.status(400).json({ error: 'Image base64 is required' });
  }

  if (!GEMINI_API_KEY || GEMINI_API_KEY === 'MY_GEMINI_API_KEY') {
    return res.json({
      source: 'curated_demo',
      detectedIngredients: ['Telur Ayam', 'Tempe', 'Bawang Merah', 'Kecap Manis', 'Nasi'],
      recipe: {
        name: 'Orak-Arik Tempe Telur Kecap',
        description: 'Menu cepat memanfaatkan sisa tempe dan telur di kulkas kosan.',
        estimatedCost: 7000,
        time: '12 Menit'
      }
    });
  }

  try {
    const cleanBase64 = imageBase64.replace(/^data:image\/\w+;base64,/, '');
    const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${GEMINI_API_KEY}`;
    
    const payload = {
      contents: [
        {
          parts: [
            { text: "Identify food/kitchen ingredients in this photo and recommend 2 quick, nutritious Indonesian student recipes with estimated cost in IDR. Return STRICT JSON with 'detectedIngredients' (array) and 'recipes' (array of {name, description, estimatedCost, prepTimeMinutes})." },
            {
              inlineData: {
                mimeType: "image/jpeg",
                data: cleanBase64
              }
            }
          ]
        }
      ],
      generationConfig: {
        responseMimeType: "application/json"
      }
    };

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await response.json();
    const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
    const result = JSON.parse(text);
    res.json({ source: 'gemini', ...result });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Sample weekly fallback
function getSampleWeeklyMeals(dailyBudget) {
  return [
    {
      id: "meal-1",
      name: "Tumis Tahu Tauge Saus Tiram",
      mealType: "Senin - Makan Siang",
      description: "Tahu sutra digoreng renyah ditumis dengan tauge segar renyah bernutrisi tinggi.",
      estimatedCost: 8000,
      proteinGrams: 16,
      carbsGrams: 30,
      veggiesGrams: 45,
      approxCalories: 340,
      toolsNeeded: ["Wajan"],
      prepTimeMinutes: 12,
      isBatchCook: false,
      batchTip: "Tauge dimasak sebentar agar tetap renyah.",
      ingredients: [
        { name: "Tahu putih", amount: "2 buah", estPrice: 3000, category: "FARMERS_MARKET" },
        { name: "Tauge segar", amount: "1 bungkus", estPrice: 2000, category: "FARMERS_MARKET" },
        { name: "Bumbu & saus tiram", amount: "1 sachet", estPrice: 3000, category: "MINIMARKET" }
      ],
      instructions: [
        "Potong tahu kotak-kotak kecil, goreng hingga berkulit.",
        "Tumis bawang putih dan cabai merah iris sampai harum.",
        "Masukkan tahu dan tauge, beri saus tiram, kecap asin, dan sedikit air.",
        "Aduk cepat selama 2 menit, lalu angkat."
      ],
      substitutions: [
        { original: "Daging cincang", substitute: "Tahu putih", reason: "Biaya 70% lebih murah, ramah kolesterol", savings: "Hemat Rp 8.000" }
      ]
    },
    {
      id: "meal-2",
      name: "Ayam Suwir Sambal Matah Rice Cooker",
      mealType: "Selasa - Makan Siang",
      description: "Dada ayam direbus empuk, disuwir dengan siraman sambal matah harum minyak kelapa.",
      estimatedCost: 14000,
      proteinGrams: 32,
      carbsGrams: 42,
      veggiesGrams: 20,
      approxCalories: 480,
      toolsNeeded: ["Rice Cooker / Panci"],
      prepTimeMinutes: 20,
      isBatchCook: true,
      batchTip: "Ayam suwir bisa dimasak untuk 2 hari.",
      ingredients: [
        { name: "Dada ayam", amount: "150 gr", estPrice: 8000, category: "FARMERS_MARKET" },
        { name: "Bawang merah & cabai", amount: "secukupnya", estPrice: 3000, category: "FARMERS_MARKET" },
        { name: "Batang serai & jeruk limau", amount: "1 btg", estPrice: 1500, category: "GROCERY_CONVENIENCE" },
        { name: "Nasi putih", amount: "1 porsi", estPrice: 1500, category: "GROCERY_CONVENIENCE" }
      ],
      instructions: [
        "Rebus ayam di rice cooker hingga matang, lalu suwir-suwir.",
        "Iris tipis bawang merah, serai, cabai rawit, dan daun jeruk.",
        "Panaskan minyak, siramkan ke irisan bumbu, beri garam dan air perasan jeruk limau.",
        "Campurkan sambal matah dengan ayam suwir hangat."
      ],
      substitutions: [
        { original: "Dada ayam fillet", substitute: "Jamur tiram suwir", reason: "Pilihan nabati gurih dan hemat 50%", savings: "Hemat Rp 5.000" }
      ]
    },
    {
      id: "meal-3",
      name: "Sup Telur Tomat Kuah Hangat",
      mealType: "Rabu - Makan Malam",
      description: "Sup ringan kaya likopen tomat dan protein telur yang menenangkan lambung.",
      estimatedCost: 6500,
      proteinGrams: 13,
      carbsGrams: 25,
      veggiesGrams: 38,
      approxCalories: 280,
      toolsNeeded: ["Panci"],
      prepTimeMinutes: 10,
      isBatchCook: false,
      batchTip: "Cocok disantap hangat saat musim hujan di Malang.",
      ingredients: [
        { name: "Tomat matang", amount: "2 buah", estPrice: 2000, category: "FARMERS_MARKET" },
        { name: "Telur ayam", amount: "1 butir", estPrice: 2500, category: "FARMERS_MARKET" },
        { name: "Bawang putih & daun bawang", amount: "secukupnya", estPrice: 2000, category: "GROCERY_CONVENIENCE" }
      ],
      instructions: [
        "Tumis bawang putih dan potongan tomat hingga tomat layu dan mengeluarkan air.",
        "Tambahkan air matang 400ml, didihkan perlahan.",
        "Tuang kocokan telur sambil diaduk memutar hingga membentuk serabut telur cantik.",
        "Beri kaldu bubuk, garam, dan merica. Taburi daun bawang."
      ],
      substitutions: [
        { original: "Bakso sapi", substitute: "Telur serabut", reason: "Biaya lebih terkontrol, rendah lemak jenuh", savings: "Hemat Rp 4.000" }
      ]
    }
  ];
}

// Catch-all route to serve the SPA
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Dapur Rantau server listening on port ${PORT}`);
});
