// Dapur Rantau - Client Application Logic

const i18n = {
  EN: {
    tagline: "Budget AI Meal Planner",
    btnLang: "EN",
    ttTitle: "🚨 End-of-Month Survival",
    ttDesc: "Ultra-budget nutritious recipes until next allowance/payday!",
    ttMoneyLabel: "Remaining Money (IDR)",
    ttDaysLabel: "Days Left",
    ttAllowanceSub: "Daily Limit:",
    ttPerDay: "/ day",
    btnTangtuaText: "Find Menu",
    ttTipsHeader: "💡 <strong>Student Survival Tips:</strong>",
    ttTip1: "• Batch cook carbohydrates and protein base for 3 days to eliminate waste.",
    ttTip2: "• Shop at open-air morning markets early morning for discounted bundles.",
    ttTip3: "• Rely on eggs, tofu & tempeh for dense protein at a fraction of the cost.",
    ttRecHeading: "Budget Savior Recipes",
    ttRecBadge: "3 Budget Picks",
    ttDisclaimer: "General balanced-eating guidance only (protein, carbs, vegetables, calories). No medical claims.",
    wpBudgetLabel: "TOTAL WEEKLY BUDGET",
    wpPerDayLabel: "Rp 25,000 / day • 7-Day Balanced Menu",
    wpAiBtn: "<span>⚡</span> Generate AI Plan",
    days: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
    dayPrefix: "Menu for",
    wpBatchLabel: "Batch Cook Only (3-Day)",
    spTitle: "Market Shopping List",
    spBoughtOf: "of",
    spBoughtItems: "items bought",
    spEstLabel: "Estimated Total:",
    spAddBtn: "+ Item",
    spChipAll: "All",
    spChipMarket: "🥬 Market",
    spChipWarung: "🏪 Grocery",
    spChipMini: "🛍️ Minimarket",
    mapTitle: "🗺️ Market & Grocery Radar",
    mapDesc: "Curated stores around university & student dorms in Malang",
    mapAreaBadge: "Area: Lowokwaru / Dinoyo",
    mapLegMarket: '<span class="dot red"></span> Farmers Market',
    mapLegWarung: '<span class="dot green"></span> Local Warung',
    mapLegSuper: '<span class="dot blue"></span> Supermarket',
    mapHeading: "Nearby Markets & Stores",
    mapOpenBtn: "Open Maps ↗",
    fridgeTitle: "Scan Fridge / Pantry",
    fridgeDesc: "Take a photo of leftover ingredients in your dorm. Multimodal Gemini AI will suggest instant zero-waste recipes.",
    fridgeBtn: "<span>📷</span> Choose / Take Ingredient Photo",
    fridgeAiSuccess: "Gemini AI Recognition Result",
    fridgeStatusBadge: "Success",
    fridgeRecHeading: "Zero-Waste Recipe Recommendation",
    navTexts: ["End Month", "Menu", "Shop", "Map", "Scan"],
    obModalTitle: "⚙️ Preferences & Dorm Budget",
    obBudgetLabel: "Daily Budget Target (IDR)",
    obKitchenLabel: "Cooking Tools Available",
    obOpt1: "Rice Cooker Only",
    obOpt2: "Single Burner Stove",
    obOpt3: "Full Kitchen",
    obCityLabel: "City / Location",
    obSaveBtn: "Save Preferences",
    modalCost: "Est. Cost:",
    modalCalories: "Calories",
    modalSteps: "Instructions:",
    modalStep: "Step",
    modalSwapTitle: "💡 Smart Cost-Saving Swap (Zero Waste):",
    modalSwapWord: "Swap",
    modalWithWord: "with"
  },
  ID: {
    tagline: "Menu Hemat & Bergizi",
    btnLang: "ID",
    ttTitle: "🚨 Mode Tanggal Tua",
    ttDesc: "Menu paling hemat untuk sisa uang saku hingga kiriman/gaji tiba!",
    ttMoneyLabel: "Sisa Uang (IDR)",
    ttDaysLabel: "Jumlah Hari",
    ttAllowanceSub: "Jatah Belanja Maksimal:",
    ttPerDay: "/ hari",
    btnTangtuaText: "Cari Menu",
    ttTipsHeader: "💡 <strong>Trik Bertahan Hidup Anak Rantau:</strong>",
    ttTip1: "• Masak nasi & lauk dasar sekaligus (batch cook) untuk kurangi boros gas/listrik.",
    ttTip2: "• Belanja sayur di pasar tradisional jam 6 pagi untuk dapat harga termurah.",
    ttTip3: "• Andalkan telur, tahu & tempe untuk pemenuhan protein harian yang padat gizi.",
    ttRecHeading: "Menu Penyelamat Hemat & Bergizi",
    ttRecBadge: "3 Rekomendasi",
    ttDisclaimer: "Perkiraan gizi dan harga dihitung berdasarkan rata-rata pasar lokal Malang. Bukan nasihat medis.",
    wpBudgetLabel: "TARGET ANGGARAN MINGGUAN",
    wpPerDayLabel: "Rp 25.000 / hari • 7 Hari Menu Bergizi",
    wpAiBtn: "<span>⚡</span> Buat Menu AI",
    days: ["Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"],
    dayPrefix: "Menu Hari",
    wpBatchLabel: "Hanya Masak Sekali (Batch)",
    spTitle: "Daftar Belanja Pasar",
    spBoughtOf: "dari",
    spBoughtItems: "bahan terbeli",
    spEstLabel: "Estimasi Total:",
    spAddBtn: "+ Bahan",
    spChipAll: "Semua",
    spChipMarket: "🥬 Pasar",
    spChipWarung: "🏪 Warung",
    spChipMini: "🛍️ Minimarket",
    mapTitle: "🗺️ Radar Pasar & Warung",
    mapDesc: "Lokasi terkurasi di sekitar kampus & kosan Malang",
    mapAreaBadge: "Area: Lowokwaru / Dinoyo",
    mapLegMarket: '<span class="dot red"></span> Pasar Tradisional',
    mapLegWarung: '<span class="dot green"></span> Warung Sayur',
    mapLegSuper: '<span class="dot blue"></span> Supermarket',
    mapHeading: "Daftar Toko & Pasar Terdekat",
    mapOpenBtn: "Buka Maps ↗",
    fridgeTitle: "Scan Isi Kulkas / Dapur",
    fridgeDesc: "Ambil foto bahan makanan apa saja yang tersisa di kosan. Multimodal Gemini AI akan merekomendasikan resep instan.",
    fridgeBtn: "<span>📷</span> Pilih / Ambil Foto Bahan",
    fridgeAiSuccess: "Hasil Pengenalan Gemini AI",
    fridgeStatusBadge: "Berhasil",
    fridgeRecHeading: "Rekomendasi Resep Daur Ulang",
    navTexts: ["Tgl Tua", "Rencana", "Belanja", "Pasar", "Kulkas"],
    obModalTitle: "⚙️ Preferensi & Budget Kosan",
    obBudgetLabel: "Target Budget Harian (IDR)",
    obKitchenLabel: "Alat Masak Tersedia",
    obOpt1: "Hanya Rice Cooker",
    obOpt2: "Kompor 1 Tungku",
    obOpt3: "Dapur Lengkap",
    obCityLabel: "Kota Domisili",
    obSaveBtn: "Simpan Preferensi",
    modalCost: "Estimasi Biaya:",
    modalCalories: "Kalori",
    modalSteps: "Cara Memasak:",
    modalStep: "Langkah",
    modalSwapTitle: "💡 Trik Substitusi Hemat (Zero Waste):",
    modalSwapWord: "Ganti",
    modalWithWord: "dengan"
  }
};

const state = {
  language: 'EN', // Default is English
  currentTab: 'tabTanggalTua',
  city: 'Malang',
  dailyBudget: 25000,
  kitchenSetup: 'Kompor 1 Tungku',
  mealsPerDay: 3,
  tangtuaMeals: [],
  weeklyMeals: [],
  selectedDayIndex: 0,
  batchCookFilter: false,
  shoppingFilter: null,
  shoppingItems: [
    { id: 's1', name: 'Telur Ayam 1/2 kg', nameEn: 'Fresh Eggs 500g', price: 14000, store: 'FARMERS_MARKET', storeName: 'Pasar Dinoyo', checked: true },
    { id: 's2', name: 'Tempe 1 Papan', nameEn: 'Tempeh 1 Block', price: 5000, store: 'FARMERS_MARKET', storeName: 'Pasar Dinoyo', checked: true },
    { id: 's3', name: 'Bayam Segar 2 Ikat', nameEn: 'Fresh Spinach 2 Bunches', price: 4000, store: 'FARMERS_MARKET', storeName: 'Pasar Oro-Oro Dowo', checked: false },
    { id: 's4', name: 'Bawang Merah & Putih', nameEn: 'Shallots & Garlic Pack', price: 7000, store: 'GROCERY_CONVENIENCE', storeName: 'Warung Bu Siti', checked: false },
    { id: 's5', name: 'Kecap Manis Sachet', nameEn: 'Sweet Soy Sauce Sachet', price: 2000, store: 'MINIMARKET', storeName: 'Minimarket Dinoyo', checked: true },
    { id: 's6', name: 'Jagung Manis 1 Buah', nameEn: 'Sweet Corn 1 Pc', price: 3000, store: 'FARMERS_MARKET', storeName: 'Pasar Dinoyo', checked: false }
  ],
  stores: [
    { id: 'm1', name: 'Pasar Dinoyo Malang', type: 'FARMERS_MARKET', dist: '450m', hours: '05:00 - 17:00', x: 0.25, y: -0.2 },
    { id: 'm2', name: 'Pasar Oro-Oro Dowo', type: 'FARMERS_MARKET', dist: '1.2 km', hours: '05:30 - 14:00', x: -0.4, y: 0.5 },
    { id: 'm3', name: 'Pasar Blimbing', type: 'FARMERS_MARKET', dist: '2.1 km', hours: '05:00 - 16:00', x: 0.7, y: 0.6 },
    { id: 'm4', name: 'Warung Sayur Bu Siti', type: 'GROCERY_CONVENIENCE', dist: '180m', hours: '06:00 - 12:00', x: 0.1, y: 0.15 },
    { id: 'm5', name: 'Warung Sembako Barokah Suhat', type: 'GROCERY_CONVENIENCE', dist: '650m', hours: '06:00 - 21:00', x: -0.3, y: -0.4 },
    { id: 'm6', name: 'Superindo Dinoyo', type: 'SUPERMARKET', dist: '900m', hours: '08:00 - 22:00', x: 0.5, y: -0.6 }
  ]
};

// Initial setup on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  initClock();
  applyLanguage(state.language);
  handleTangtuaInput();
  renderShoppingList();
  renderStoresList();
  initRadarAnimation();
  loadInitialWeeklyPlan();
});

// Clock in status bar
function initClock() {
  const update = () => {
    const now = new Date();
    const h = String(now.getHours()).padStart(2, '0');
    const m = String(now.getMinutes()).padStart(2, '0');
    const el = document.getElementById('statusBarClock');
    if (el) el.innerText = `${h}:${m}`;
  };
  update();
  setInterval(update, 1000);
}

// Toggle language
function toggleLanguage() {
  state.language = state.language === 'EN' ? 'ID' : 'EN';
  applyLanguage(state.language);
  handleTangtuaInput();
  renderShoppingList();
  renderStoresList();
  renderWeeklyMeals();
}

// Apply Language strings to existing elements
function applyLanguage(lang) {
  const t = i18n[lang];

  // Header
  const btnLang = document.getElementById('btnLang');
  if (btnLang) btnLang.innerText = t.btnLang;
  const tagline = document.getElementById('headerTagline');
  if (tagline) tagline.innerText = t.tagline;

  // Tanggal Tua elements
  const ttTitle = document.getElementById('ttTitle');
  if (ttTitle) ttTitle.innerText = t.ttTitle;
  const ttDesc = document.getElementById('ttDesc');
  if (ttDesc) ttDesc.innerText = t.ttDesc;
  const ttMoneyLabel = document.getElementById('ttMoneyLabel');
  if (ttMoneyLabel) ttMoneyLabel.innerText = t.ttMoneyLabel;
  const ttDaysLabel = document.getElementById('ttDaysLabel');
  if (ttDaysLabel) ttDaysLabel.innerText = t.ttDaysLabel;
  const ttAllowanceSub = document.getElementById('ttAllowanceSub');
  if (ttAllowanceSub) ttAllowanceSub.innerText = t.ttAllowanceSub;
  const btnTangtuaText = document.getElementById('btnTangtuaText');
  if (btnTangtuaText) btnTangtuaText.innerText = t.btnTangtuaText;
  const ttTipsHeader = document.getElementById('ttTipsHeader');
  if (ttTipsHeader) ttTipsHeader.innerHTML = t.ttTipsHeader;
  const ttTip1 = document.getElementById('ttTip1');
  if (ttTip1) ttTip1.innerText = t.ttTip1;
  const ttTip2 = document.getElementById('ttTip2');
  if (ttTip2) ttTip2.innerText = t.ttTip2;
  const ttTip3 = document.getElementById('ttTip3');
  if (ttTip3) ttTip3.innerText = t.ttTip3;
  const ttRecHeading = document.getElementById('ttRecHeading');
  if (ttRecHeading) ttRecHeading.innerText = t.ttRecHeading;
  const ttRecBadge = document.getElementById('ttRecBadge');
  if (ttRecBadge) ttRecBadge.innerText = t.ttRecBadge;
  const ttDisclaimer = document.getElementById('ttDisclaimer');
  if (ttDisclaimer) ttDisclaimer.innerText = t.ttDisclaimer;

  // Weekly plan elements
  const wpBudgetLabel = document.getElementById('wpBudgetLabel');
  if (wpBudgetLabel) wpBudgetLabel.innerText = t.wpBudgetLabel;
  const wpPerDayLabel = document.getElementById('wpPerDayLabel');
  if (wpPerDayLabel) wpPerDayLabel.innerText = t.wpPerDayLabel;
  const wpAiBtn = document.getElementById('wpAiBtn');
  if (wpAiBtn) wpAiBtn.innerHTML = t.wpAiBtn;
  const wpBatchLabel = document.getElementById('wpBatchLabel');
  if (wpBatchLabel) wpBatchLabel.innerText = t.wpBatchLabel;

  // Day carousel buttons
  const dayButtons = document.querySelectorAll('#daysCarousel .day-chip');
  dayButtons.forEach((btn, idx) => {
    if (t.days[idx]) btn.innerText = t.days[idx];
  });
  const currentDayTitle = document.getElementById('currentDayTitle');
  if (currentDayTitle) currentDayTitle.innerText = `${t.dayPrefix} ${t.days[state.selectedDayIndex]}`;

  // Shopping elements
  const spTitle = document.getElementById('spTitle');
  if (spTitle) spTitle.innerText = t.spTitle;
  const spEstLabel = document.getElementById('spEstLabel');
  if (spEstLabel) spEstLabel.innerText = t.spEstLabel;
  const spAddBtn = document.getElementById('spAddBtn');
  if (spAddBtn) spAddBtn.innerText = t.spAddBtn;
  const spChipAll = document.getElementById('spChipAll');
  if (spChipAll) spChipAll.innerText = t.spChipAll;
  const spChipMarket = document.getElementById('spChipMarket');
  if (spChipMarket) spChipMarket.innerText = t.spChipMarket;
  const spChipWarung = document.getElementById('spChipWarung');
  if (spChipWarung) spChipWarung.innerText = t.spChipWarung;
  const spChipMini = document.getElementById('spChipMini');
  if (spChipMini) spChipMini.innerText = t.spChipMini;

  // Map elements
  const mapTitle = document.getElementById('mapTitle');
  if (mapTitle) mapTitle.innerText = t.mapTitle;
  const mapDesc = document.getElementById('mapDesc');
  if (mapDesc) mapDesc.innerText = t.mapDesc;
  const mapAreaBadge = document.getElementById('mapAreaBadge');
  if (mapAreaBadge) mapAreaBadge.innerText = t.mapAreaBadge;
  const mapLegMarket = document.getElementById('mapLegMarket');
  if (mapLegMarket) mapLegMarket.innerHTML = t.mapLegMarket;
  const mapLegWarung = document.getElementById('mapLegWarung');
  if (mapLegWarung) mapLegWarung.innerHTML = t.mapLegWarung;
  const mapLegSuper = document.getElementById('mapLegSuper');
  if (mapLegSuper) mapLegSuper.innerHTML = t.mapLegSuper;
  const mapHeading = document.getElementById('mapHeading');
  if (mapHeading) mapHeading.innerText = t.mapHeading;
  const mapOpenBtn = document.getElementById('mapOpenBtn');
  if (mapOpenBtn) mapOpenBtn.innerText = t.mapOpenBtn;

  // Fridge elements
  const fridgeTitle = document.getElementById('fridgeTitle');
  if (fridgeTitle) fridgeTitle.innerText = t.fridgeTitle;
  const fridgeDesc = document.getElementById('fridgeDesc');
  if (fridgeDesc) fridgeDesc.innerText = t.fridgeDesc;
  const fridgeBtn = document.getElementById('fridgeBtn');
  if (fridgeBtn) fridgeBtn.innerHTML = t.fridgeBtn;
  const fridgeAiSuccess = document.getElementById('fridgeAiSuccess');
  if (fridgeAiSuccess) fridgeAiSuccess.innerText = t.fridgeAiSuccess;
  const fridgeStatusBadge = document.getElementById('fridgeStatusBadge');
  if (fridgeStatusBadge) fridgeStatusBadge.innerText = t.fridgeStatusBadge;
  const fridgeRecHeading = document.getElementById('fridgeRecHeading');
  if (fridgeRecHeading) fridgeRecHeading.innerText = t.fridgeRecHeading;

  // Nav buttons
  t.navTexts.forEach((text, i) => {
    const el = document.getElementById(`navText${i}`);
    if (el) el.innerText = text;
  });

  // Preferences modal
  const obModalTitle = document.getElementById('obModalTitle');
  if (obModalTitle) obModalTitle.innerText = t.obModalTitle;
  const obBudgetLabel = document.getElementById('obBudgetLabel');
  if (obBudgetLabel) obBudgetLabel.innerText = t.obBudgetLabel;
  const obKitchenLabel = document.getElementById('obKitchenLabel');
  if (obKitchenLabel) obKitchenLabel.innerText = t.obKitchenLabel;
  const obOpt1 = document.getElementById('obOpt1');
  if (obOpt1) obOpt1.innerText = t.obOpt1;
  const obOpt2 = document.getElementById('obOpt2');
  if (obOpt2) obOpt2.innerText = t.obOpt2;
  const obOpt3 = document.getElementById('obOpt3');
  if (obOpt3) obOpt3.innerText = t.obOpt3;
  const obCityLabel = document.getElementById('obCityLabel');
  if (obCityLabel) obCityLabel.innerText = t.obCityLabel;
  const obSaveBtn = document.getElementById('obSaveBtn');
  if (obSaveBtn) obSaveBtn.innerText = t.obSaveBtn;
}

// Tab navigation switcher
function switchNav(tabId, btn) {
  document.querySelectorAll('.tab-page').forEach(page => page.classList.remove('active'));
  document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));

  const target = document.getElementById(tabId);
  if (target) target.classList.add('active');
  btn.classList.add('active');
  state.currentTab = tabId;

  const body = document.getElementById('screenBody');
  if (body) body.scrollTop = 0;
}

// Mode Tanggal Tua live calculation
function handleTangtuaInput() {
  const money = parseFloat(document.getElementById('ttMoney').value) || 0;
  const days = parseInt(document.getElementById('ttDays').value) || 1;
  const daily = days > 0 ? Math.round(money / days) : money;

  const unit = i18n[state.language].ttPerDay;
  document.getElementById('ttDailyAllowance').innerText = `Rp ${daily.toLocaleString('id-ID')} ${unit}`;

  const mascot = document.getElementById('tangtuaMascot');
  if (daily < 10000) {
    mascot.innerText = '😰'; // Worried
  } else if (daily < 20000) {
    mascot.innerText = '🤔'; // Thinking
  } else {
    mascot.innerText = '😋'; // Happy
  }

  // Load default fallback cards if empty
  if (state.tangtuaMeals.length === 0) {
    state.tangtuaMeals = [
      {
        name: "Nasi Telur Ceplok Kecap Mentega",
        nameEn: "Sunny-Side Up Egg with Sweet Butter Soy Glaze",
        estimatedCost: Math.min(6000, daily),
        approxCalories: 410,
        proteinGrams: 14,
        carbsGrams: 55,
        veggiesGrams: 15,
        prepTimeMinutes: 10,
        toolsNeeded: ["Wajan"],
        toolsNeededEn: ["Frying Pan"],
        isBatchCook: false,
        batchTip: "Bisa ditambahkan daun bawang agar lebih harum.",
        batchTipEn: "Add sliced scallions for extra aroma.",
        description: "Klasik andalan anak kos. Telur ceplok renyah dengan siraman mentega kecap manis.",
        descriptionEn: "Dorm student classic. Crispy fried egg drizzled with caramelized sweet butter soy glaze.",
        instructions: ["Goreng telur mata sapi sesuai selera kematangan.", "Lelehkan mentega, tuang kecap manis dan perasan jeruk limau.", "Siramkan ke atas telur dan nasi hangat."],
        instructionsEn: ["Fry egg until edges are golden and crispy.", "Melt butter, stir in sweet soy sauce and fresh lime juice.", "Pour over steaming white rice and fried egg."],
        substitutions: [{ original: "Daging ayam", originalEn: "Chicken breast", substitute: "Telur mata sapi", substituteEn: "Fried egg", reason: "Protein setara, hemat 60%", reasonEn: "Identical protein, 60% cheaper", savings: "Hemat Rp 6.000", savingsEn: "Save Rp 6,000" }]
      },
      {
        name: "Orek Tempe Basah Manis Gurih",
        nameEn: "Sweet & Savory Braised Tempeh Cubes",
        estimatedCost: Math.min(4500, daily),
        approxCalories: 320,
        proteinGrams: 18,
        carbsGrams: 35,
        veggiesGrams: 12,
        prepTimeMinutes: 15,
        toolsNeeded: ["Wajan"],
        toolsNeededEn: ["Frying Pan / Rice Cooker"],
        isBatchCook: true,
        batchTip: "Masak satu papan tempe untuk persediaan makan 3 hari berturut-turut.",
        batchTipEn: "Cook a whole block to keep as side dish for 3 days.",
        description: "Tinggi protein nabati, ramah dompet, dan tahan disimpan 3 hari tanpa kulkas.",
        descriptionEn: "High plant-based protein, ultra wallet-friendly, keeps well for 3 days without fridge.",
        instructions: ["Potong tempe bentuk korek api, goreng setengah matang.", "Tumis bawang merah, bawang putih, dan cabai.", "Beri kecap manis, garam, dan sedikit air, masak sampai meresap."],
        instructionsEn: ["Slice tempeh into strips, fry until half cooked.", "Sauté shallots, garlic, and sliced chilies.", "Add sweet soy sauce, salt, and simmer until deeply caramelized."],
        substitutions: [{ original: "Daging cincang", originalEn: "Minced beef", substitute: "Tempe kedelai", substituteEn: "Soy tempeh", reason: "Tinggi protein nabati, murah meriah", reasonEn: "High plant protein, budget friendly", savings: "Hemat Rp 8.000", savingsEn: "Save Rp 8,000" }]
      },
      {
        name: "Sayur Bening Bayam & Jagung Manis",
        nameEn: "Clear Spinach & Sweet Corn Soup",
        estimatedCost: Math.min(5000, daily),
        approxCalories: 180,
        proteinGrams: 6,
        carbsGrams: 28,
        veggiesGrams: 45,
        prepTimeMinutes: 12,
        toolsNeeded: ["Panci"],
        toolsNeededEn: ["Pot / Rice Cooker"],
        isBatchCook: false,
        batchTip: "Sayur bayam sebaiknya dihabiskan dalam hari yang sama.",
        batchTipEn: "Spinach soup is best consumed on the same day.",
        description: "Menjaga stamina dan serat tubuh anak kos agar tetap bugar saat ujian akhir bulan.",
        descriptionEn: "Hydrating, vitamin-packed soup to keep solo students healthy during exam week.",
        instructions: ["Rebus air bersama irisan kunci dan bawang merah hingga harum.", "Masukkan jagung manis, rebus hingga empuk.", "Masukkan bayam, beri garam dan gula secukupnya. Angkat segera."],
        instructionsEn: ["Boil water with shallots and fingerroot spice until aromatic.", "Add sweet corn chunks and simmer until tender.", "Toss in fresh spinach, season with salt and sugar. Serve hot."],
        substitutions: [{ original: "Sayur brokoli", originalEn: "Broccoli", substitute: "Bayam segar", substituteEn: "Fresh spinach", reason: "Harga pasar tradisional cuma Rp 2.500/ikat", reasonEn: "Traditional market price only Rp 2,500/bunch", savings: "Hemat Rp 5.000", savingsEn: "Save Rp 5,000" }]
      }
    ];
  }
  renderTangtuaMeals();
}

// Generate Tanggal Tua menu via Backend / Gemini API
async function generateTangtuaMenu() {
  const btn = document.getElementById('btnTangtuaGenerate');
  const btnText = document.getElementById('btnTangtuaText');
  const money = parseFloat(document.getElementById('ttMoney').value) || 25000;
  const days = parseInt(document.getElementById('ttDays').value) || 3;

  btnText.innerText = state.language === 'EN' ? "Thinking AI..." : "Meracik AI...";
  btn.style.opacity = "0.7";
  btn.disabled = true;

  try {
    const res = await fetch('/api/tangtua-plan', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ remainingBudget: money, daysLeft: days, city: state.city })
    });
    const data = await res.json();
    if (data.meals && data.meals.length > 0) {
      state.tangtuaMeals = data.meals;
      renderTangtuaMeals();
    }
  } catch (err) {
    console.error('Error fetching tanggal tua menu:', err);
  } finally {
    btnText.innerText = state.language === 'EN' ? "Find Menu" : "Cari Menu";
    btn.style.opacity = "1";
    btn.disabled = false;
  }
}

function renderTangtuaMeals() {
  const container = document.getElementById('tangtuaMealsContainer');
  if (!container) return;

  const isEn = state.language === 'EN';

  container.innerHTML = state.tangtuaMeals.map((meal, idx) => {
    const pWidth = meal.proteinGrams || 20;
    const cWidth = meal.carbsGrams || 50;
    const vWidth = meal.veggiesGrams || 30;
    const total = pWidth + cWidth + vWidth;

    const pPct = Math.round((pWidth / total) * 100);
    const cPct = Math.round((cWidth / total) * 100);
    const vPct = 100 - pPct - cPct;

    const mealName = isEn && meal.nameEn ? meal.nameEn : meal.name;
    const timeLabel = isEn ? `${meal.prepTimeMinutes || 15} Mins` : `${meal.prepTimeMinutes || 15} Menit`;
    const batchLabel = isEn ? "🔁 Batch Cook (3x)" : "🔁 Masak 1x Makan 3x";
    const swapLabel = isEn ? "💡 Cost-Saving Swap" : "💡 Ada Subtitusi Hemat";

    return `
      <div class="meal-card" onclick="openRecipeDetail('tangtua', ${idx})">
        <div class="meal-top">
          <div class="meal-thumb">${idx === 0 ? '🍳' : (idx === 1 ? '🥢' : '🥬')}</div>
          <div class="meal-info">
            <div class="meal-name">${mealName}</div>
            <div class="meal-meta">Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 350} kcal</div>
            <div class="meal-tags-row">
              <span class="badge-tag">${timeLabel}</span>
              ${meal.isBatchCook ? `<span class="badge-tag green">${batchLabel}</span>` : ''}
              ${meal.substitutions && meal.substitutions.length ? `<span class="badge-tag amber">${swapLabel}</span>` : ''}
            </div>
          </div>
        </div>
        <div class="balance-bar-wrap">
          <div class="b-prot" style="width: ${pPct}%;"></div>
          <div class="b-carb" style="width: ${cPct}%;"></div>
          <div class="b-veg" style="width: ${vPct}%;"></div>
        </div>
        <div class="balance-legend">
          <span>🍗 Protein ${pPct}%</span>
          <span>🍚 ${isEn ? 'Carbs' : 'Karbo'} ${cPct}%</span>
          <span>🥦 ${isEn ? 'Veggies' : 'Sayur'} ${vPct}%</span>
        </div>
      </div>
    `;
  }).join('');
}

// Initial Weekly Plan loader
async function loadInitialWeeklyPlan() {
  try {
    const res = await fetch('/api/generate-plan', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        budget: state.dailyBudget,
        kitchenSetup: state.kitchenSetup,
        mealsPerDay: state.mealsPerDay,
        city: state.city
      })
    });
    const data = await res.json();
    if (data.meals && data.meals.length > 0) {
      state.weeklyMeals = data.meals;
      renderWeeklyMeals();
    }
  } catch (e) {
    console.error('Failed to load initial weekly plan:', e);
  }
}

// Generate weekly plan via button
async function generateWeeklyPlan() {
  const btn = document.getElementById('wpAiBtn');
  if (btn) {
    btn.innerHTML = `<span>⏳</span> ${state.language === 'EN' ? 'Thinking AI...' : 'Meracik AI...'}`;
    btn.disabled = true;
  }

  await loadInitialWeeklyPlan();

  if (btn) {
    btn.innerHTML = `<span>⚡</span> ${state.language === 'EN' ? 'Generate AI Plan' : 'Buat Menu AI'}`;
    btn.disabled = false;
  }
}

function selectDay(dayIdx, chip) {
  state.selectedDayIndex = dayIdx;
  document.querySelectorAll('#daysCarousel .day-chip').forEach(c => c.classList.remove('active'));
  chip.classList.add('active');

  const t = i18n[state.language];
  document.getElementById('currentDayTitle').innerText = `${t.dayPrefix} ${t.days[dayIdx]}`;
  renderWeeklyMeals();
}

function toggleBatchCookFilter() {
  state.batchCookFilter = document.getElementById('batchCookFilter').checked;
  renderWeeklyMeals();
}

function renderWeeklyMeals() {
  const container = document.getElementById('weeklyMealsContainer');
  if (!container) return;

  const isEn = state.language === 'EN';

  let meals = state.weeklyMeals;
  if (state.batchCookFilter) {
    meals = meals.filter(m => m.isBatchCook);
  }

  if (meals.length === 0) {
    container.innerHTML = `<div style="text-align:center; padding:24px; color:#6B7280; font-size:12px;">${isEn ? 'No batch cooking meals for this filter.' : 'Tidak ada menu batch cook untuk filter ini.'}</div>`;
    return;
  }

  container.innerHTML = meals.map((meal, idx) => {
    const mealName = isEn && meal.nameEn ? meal.nameEn : meal.name;
    const timeLabel = isEn ? `${meal.prepTimeMinutes || 15} Mins` : `${meal.prepTimeMinutes || 15} Menit`;
    const batchLabel = isEn ? "🔁 3-Day Batch" : "🔁 Tahan 3 Hari";
    const tools = isEn && meal.toolsNeededEn ? meal.toolsNeededEn.join(', ') : (meal.toolsNeeded ? meal.toolsNeeded.join(', ') : 'Pan');

    return `
      <div class="meal-card" onclick="openRecipeDetail('weekly', ${idx})">
        <div class="meal-top">
          <div class="meal-thumb">${idx % 2 === 0 ? '🍲' : '🍗'}</div>
          <div class="meal-info">
            <div class="meal-name">${mealName}</div>
            <div class="meal-meta">Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 380} kcal</div>
            <div class="meal-tags-row">
              <span class="badge-tag">${timeLabel}</span>
              ${meal.isBatchCook ? `<span class="badge-tag green">${batchLabel}</span>` : ''}
              <span class="badge-tag">${tools}</span>
            </div>
          </div>
        </div>
        <div class="balance-bar-wrap">
          <div class="b-prot" style="width: 30%;"></div>
          <div class="b-carb" style="width: 45%;"></div>
          <div class="b-veg" style="width: 25%;"></div>
        </div>
      </div>
    `;
  }).join('');
}

// Shopping list handlers
function renderShoppingList() {
  const container = document.getElementById('shoppingListContainer');
  if (!container) return;

  const isEn = state.language === 'EN';
  const t = i18n[state.language];

  let items = state.shoppingItems;
  if (state.shoppingFilter) {
    items = items.filter(it => it.store === state.shoppingFilter);
  }

  const checkedCount = state.shoppingItems.filter(i => i.checked).length;
  const totalAll = state.shoppingItems.reduce((acc, it) => acc + it.price, 0);

  document.getElementById('shoppingCheckedCount').innerText = `${checkedCount} ${t.spBoughtOf} ${state.shoppingItems.length} ${t.spBoughtItems}`;
  document.getElementById('shoppingTotalPrice').innerText = `Rp ${totalAll.toLocaleString('id-ID')}`;

  container.innerHTML = items.map(item => `
    <div class="shopping-item-card ${item.checked ? 'checked' : ''}">
      <input 
        type="checkbox" 
        class="shopping-check" 
        ${item.checked ? 'checked' : ''} 
        onchange="toggleShoppingItem('${item.id}')"
      />
      <div style="flex: 1;">
        <div class="shopping-name" style="font-size: 12px; font-weight: 700;">${isEn && item.nameEn ? item.nameEn : item.name}</div>
        <div style="font-size: 10px; color: #6B7280;">${item.storeName}</div>
      </div>
      <div style="font-size: 12px; font-weight: 800; color: #B45309;">
        Rp ${item.price.toLocaleString('id-ID')}
      </div>
    </div>
  `).join('');
}

function toggleShoppingItem(id) {
  const it = state.shoppingItems.find(i => i.id === id);
  if (it) {
    it.checked = !it.checked;
    renderShoppingList();
  }
}

function filterShopping(type, btn) {
  state.shoppingFilter = type;
  document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
  btn.classList.add('active');
  renderShoppingList();
}

function openAddShoppingModal() {
  const isEn = state.language === 'EN';
  const promptName = isEn ? "New grocery item name:" : "Nama bahan belanja baru:";
  const promptPrice = isEn ? "Estimated price (e.g. 5000):" : "Perkiraan harga (contoh: 5000):";
  const name = prompt(promptName);
  if (name && name.trim()) {
    const priceStr = prompt(promptPrice, "5000");
    const price = parseInt(priceStr) || 5000;
    state.shoppingItems.push({
      id: 's' + Date.now(),
      name: name.trim(),
      nameEn: name.trim(),
      price: price,
      store: 'FARMERS_MARKET',
      storeName: isEn ? 'Traditional Market' : 'Pasar Tradisional Malang',
      checked: false
    });
    renderShoppingList();
  }
}

// Stores List rendering
function renderStoresList() {
  const container = document.getElementById('storesListContainer');
  if (!container) return;

  const isEn = state.language === 'EN';

  container.innerHTML = state.stores.map(st => `
    <div class="store-card">
      <div>
        <div class="store-name">${st.name}</div>
        <div class="store-sub">${st.type === 'FARMERS_MARKET' ? (isEn ? '🥬 Traditional Market' : '🥬 Pasar Tradisional') : (st.type === 'GROCERY_CONVENIENCE' ? (isEn ? '🏪 Neighborhood Warung' : '🏪 Warung Sayur') : '🛍️ Supermarket')} • ${isEn ? 'Hours' : 'Jam'}: ${st.hours}</div>
      </div>
      <a 
        href="https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(st.name + ' Malang')}" 
        target="_blank" 
        rel="noopener" 
        class="badge-tag"
        style="text-decoration:none; background:#E0E7FF; color:#4338CA;"
      >
        📍 ${st.dist}
      </a>
    </div>
  `).join('');
}

// Animated Radar Scanner Canvas
let radarAngle = 0;
function initRadarAnimation() {
  const canvas = document.getElementById('radarCanvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');

  function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    const cx = canvas.width / 2;
    const cy = canvas.height / 2;
    const maxR = Math.min(cx, cy) - 10;

    // Rings
    ctx.strokeStyle = 'rgba(56, 189, 248, 0.25)';
    ctx.lineWidth = 1;
    [0.35, 0.7, 1.0].forEach(factor => {
      ctx.beginPath();
      ctx.arc(cx, cy, maxR * factor, 0, Math.PI * 2);
      ctx.stroke();
    });

    // Crosshairs
    ctx.beginPath();
    ctx.moveTo(cx, 10); ctx.lineTo(cx, canvas.height - 10);
    ctx.moveTo(10, cy); ctx.lineTo(canvas.width - 10, cy);
    ctx.stroke();

    // Store Blips
    state.stores.forEach(st => {
      const bx = cx + st.x * (maxR * 0.9);
      const by = cy + st.y * (maxR * 0.9);
      ctx.beginPath();
      ctx.arc(bx, by, 4, 0, Math.PI * 2);
      ctx.fillStyle = st.type === 'FARMERS_MARKET' ? '#EF4444' : (st.type === 'GROCERY_CONVENIENCE' ? '#10B981' : '#3B82F6');
      ctx.fill();
    });

    // Rotating Sweep line
    radarAngle += 0.035;
    const sweepX = cx + Math.cos(radarAngle) * maxR;
    const sweepY = cy + Math.sin(radarAngle) * maxR;

    const grad = ctx.createRadialGradient(cx, cy, 0, cx, cy, maxR);
    grad.addColorStop(0, 'rgba(56, 189, 248, 0.5)');
    grad.addColorStop(1, 'rgba(56, 189, 248, 0)');

    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.arc(cx, cy, maxR, radarAngle - 0.4, radarAngle);
    ctx.closePath();
    ctx.fillStyle = 'rgba(56, 189, 248, 0.15)';
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(sweepX, sweepY);
    ctx.strokeStyle = '#38bdf8';
    ctx.lineWidth = 1.5;
    ctx.stroke();

    requestAnimationFrame(draw);
  }
  requestAnimationFrame(draw);
}

// Multimodal Fridge Scan upload handler
async function handleFridgeImage(e) {
  const file = e.target.files[0];
  if (!file) return;

  const isEn = state.language === 'EN';
  const reader = new FileReader();
  reader.onload = async (event) => {
    const base64 = event.target.result;
    const resultCard = document.getElementById('fridgeResultCard');
    const ingList = document.getElementById('detectedIngredientsList');
    const recipeDiv = document.getElementById('fridgeRecipeResult');

    resultCard.style.display = 'block';
    ingList.innerHTML = `<span>⏳ ${isEn ? 'Analyzing with Gemini Vision AI...' : 'Menganalisis dengan Gemini Vision...'}</span>`;
    recipeDiv.innerHTML = '';

    try {
      const res = await fetch('/api/fridge-scan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageBase64: base64 })
      });
      const data = await res.json();
      
      const detected = data.detectedIngredients || (isEn ? ['Eggs', 'Tempeh', 'Shallots', 'Sweet Soy Sauce'] : ['Telur Ayam', 'Tempe', 'Bawang Merah', 'Kecap Manis']);
      ingList.innerHTML = detected.map(item => `<span class="badge-tag green">✅ ${item}</span>`).join('');

      const recipes = data.recipes || [data.recipe || {
        name: isEn ? 'Tempeh & Egg Scramble with Soy Glaze' : 'Orak-Arik Tempe Telur Kecap',
        description: isEn ? 'Quick zero-waste recipe using leftover tempeh and eggs in the dorm.' : 'Menu cepat memanfaatkan sisa tempe dan telur di kulkas kosan.',
        estimatedCost: 7000,
        prepTimeMinutes: 12
      }];

      recipeDiv.innerHTML = recipes.map(r => `
        <div class="meal-card" style="margin-top:8px;">
          <div class="meal-name">${r.name}</div>
          <div class="meal-meta">Rp ${Number(r.estimatedCost).toLocaleString('id-ID')} • ${r.prepTimeMinutes || 12} ${isEn ? 'Mins' : 'Menit'}</div>
          <p style="font-size:11px; color:#4B5563; margin-top:4px;">${r.description}</p>
        </div>
      `).join('');
    } catch (err) {
      ingList.innerHTML = `<span style="color:#EF4444;">${isEn ? 'AI service unreachable. Displaying fallback recipe.' : 'Gagal menghubungi AI. Menampilkan saran default.'}</span>`;
    }
  };
  reader.readAsDataURL(file);
}

// Recipe detail modal
function openRecipeDetail(type, idx) {
  const isEn = state.language === 'EN';
  const t = i18n[state.language];
  const list = type === 'tangtua' ? state.tangtuaMeals : state.weeklyMeals;
  const meal = list[idx];
  if (!meal) return;

  const mealName = isEn && meal.nameEn ? meal.nameEn : meal.name;
  document.getElementById('modalRecipeName').innerText = mealName;
  const body = document.getElementById('modalRecipeBody');

  const rawInstructions = isEn && meal.instructionsEn ? meal.instructionsEn : (meal.instructions || ["Prepare ingredients.", "Cook until well seasoned.", "Serve warm."]);
  const instructionsHtml = rawInstructions.map((step, i) => `<li style="margin-bottom:6px;"><strong>${t.modalStep} ${i+1}:</strong> ${step}</li>`).join('');

  const desc = isEn && meal.descriptionEn ? meal.descriptionEn : (meal.description || '');

  let subsHtml = '';
  if (meal.substitutions && meal.substitutions.length) {
    const sub = meal.substitutions[0];
    const orig = isEn && sub.originalEn ? sub.originalEn : sub.original;
    const rep = isEn && sub.substituteEn ? sub.substituteEn : sub.substitute;
    const rsn = isEn && sub.reasonEn ? sub.reasonEn : sub.reason;
    const svg = isEn && sub.savingsEn ? sub.savingsEn : sub.savings;

    subsHtml = `
      <div class="tips-card" style="margin-top:12px;">
        <div class="tips-header"><strong>${t.modalSwapTitle}</strong></div>
        <p style="font-size:11px; color:#78350F;">
          ${t.modalSwapWord} <em>${orig}</em> ${t.modalWithWord} <strong>${rep}</strong>. 
          ${rsn || ''} (${svg || 'Save 30%'}).
        </p>
      </div>
    `;
  }

  body.innerHTML = `
    <div style="font-size:12px; color:#16A34A; font-weight:800; margin-bottom:8px;">
      ${t.modalCost} Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 350} ${t.modalCalories}
    </div>
    <p style="font-size:12px; color:#4B5563; line-height:1.4; margin-bottom:12px;">
      ${desc}
    </p>

    <div style="font-size:12px; font-weight:800; margin-bottom:6px;">${t.modalSteps}</div>
    <ol style="font-size:11px; color:#374151; padding-left:16px; line-height:1.45;">
      ${instructionsHtml}
    </ol>
    ${subsHtml}
  `;

  document.getElementById('recipeModal').classList.add('active');
}

function closeRecipeModal() {
  document.getElementById('recipeModal').classList.remove('active');
}

// Onboarding modal
function openOnboarding() {
  document.getElementById('obBudget').value = state.dailyBudget;
  document.getElementById('obCity').value = state.city;
  document.getElementById('obKitchen').value = state.kitchenSetup;
  document.getElementById('onboardingModal').classList.add('active');
}

function closeOnboardingModal() {
  document.getElementById('onboardingModal').classList.remove('active');
}

function savePreferences() {
  state.dailyBudget = parseFloat(document.getElementById('obBudget').value) || 25000;
  state.city = document.getElementById('obCity').value || 'Malang';
  state.kitchenSetup = document.getElementById('obKitchen').value;

  document.getElementById('currentCityText').innerText = state.city;
  document.getElementById('weeklyTotalBudget').innerText = `Rp ${(state.dailyBudget * 7).toLocaleString('id-ID')}`;

  closeOnboardingModal();
  handleTangtuaInput();
  loadInitialWeeklyPlan();
}

function closeModalOnBackdrop(e, modalId) {
  if (e.target.id === modalId) {
    document.getElementById(modalId).classList.remove('active');
  }
}
