// Dapur Rantau - Client Application Logic (Full AI Studio Fidelity)

const i18n = {
  ID: {
    appTitle: "Dapur Rantau",
    tagline: "Menu Hemat & Bergizi",
    tabTanggalTua: "Tgl Tua",
    tabWeeklyPlan: "Menu",
    tabShopping: "Belanja",
    tabMap: "Pasar",
    tabFridge: "Kulkas",
    // Tanggal Tua
    ttTitle: "🚨 Mode Tanggal Tua",
    ttDesc: "Menu paling hemat untuk sisa uang saku hingga kiriman/gaji tiba!",
    ttMoneyLabel: "Sisa Uang (IDR)",
    ttDaysLabel: "Jumlah Hari",
    ttAllowanceLabel: "Jatah Belanja Maksimal:",
    ttPerDay: "/ hari",
    ttFindMenu: "Cari Menu",
    ttGenerating: "Meracik AI...",
    ttTipsTitle: "Trik Bertahan Hidup Anak Rantau:",
    ttTip1: "Masak nasi & karbohidrat sekaligus di rice cooker untuk kurangi boros gas/listrik.",
    ttTip2: "Belanja di pasar tradisional pagi hari untuk dapat sayur segar berharga miring.",
    ttTip3: "Andalkan telur, tahu & tempe untuk pemenuhan protein harian padat gizi.",
    ttRecTitle: "Menu Penyelamat Hemat & Bergizi",
    ttRecCount: "3 Pilihan Hemat",
    ttDisclaimer: "Panduan gizi seimbang umum (protein, karbohidrat, sayur, perkiraan kalori). Bukan saran medis klinis.",
    // Weekly Plan
    wpBudgetLabel: "TARGET ANGGARAN MINGGUAN",
    wpPerDayLabel: "Rp 25.000 / hari • 7 Hari Menu Seimbang",
    wpAiBtn: "Buat Menu AI",
    wpAiBtnGen: "Meracik AI...",
    wpDays: ["Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"],
    wpDayTitle: "Menu Hari",
    wpBatchFilter: "Hanya Masak Sekali (Batch)",
    // Shopping
    spTitle: "Daftar Belanja Pasar",
    spBought: "dari",
    spBoughtEnd: "bahan terbeli",
    spEstTotal: "Estimasi Total:",
    spAddBtn: "+ Bahan",
    spFilterAll: "Semua",
    spFilterMarket: "🥬 Pasar",
    spFilterWarung: "🏪 Warung",
    spFilterMini: "🛍️ Minimarket",
    // Map
    mapTitle: "🗺️ Radar Pasar & Warung",
    mapDesc: "Lokasi terkurasi di sekitar kampus & kosan Malang",
    mapArea: "Area: Sekitar Kampus Dinoyo & Lowokwaru",
    mapLegMarket: "Pasar Tradisional",
    mapLegWarung: "Warung Sayur",
    mapLegSuper: "Supermarket",
    mapStoresHeading: "Daftar Toko & Pasar Terdekat",
    mapOpenMaps: "Buka Maps ↗",
    // Fridge
    fridgeTitle: "Scan Isi Kulkas / Dapur",
    fridgeDesc: "Ambil foto bahan makanan apa saja yang tersisa di kosan. Multimodal Gemini AI akan merekomendasikan resep instan.",
    fridgeBtn: "Pilih / Ambil Foto Bahan",
    fridgeAiSuccess: "Hasil Pengenalan Gemini AI",
    fridgeRecipes: "Rekomendasi Resep Daur Ulang",
    // Modal
    modalRecipeTitle: "Detail Resep",
    modalCost: "Estimasi Biaya:",
    modalCalories: "Kalori",
    modalCookingSteps: "Cara Memasak:",
    modalSubsTitle: "Trik Substitusi Hemat (Zero Waste):",
    // Preferences Modal
    prefTitle: "⚙️ Preferensi & Budget Kosan",
    prefBudgetLabel: "Target Budget Harian (IDR)",
    prefKitchenLabel: "Alat Masak Tersedia",
    prefCityLabel: "Kota Domisili",
    prefSaveBtn: "Simpan Preferensi"
  },
  EN: {
    appTitle: "Dapur Rantau",
    tagline: "Budget AI Meal Planner",
    tabTanggalTua: "End Month",
    tabWeeklyPlan: "Menu",
    tabShopping: "Shop",
    tabMap: "Map",
    tabFridge: "Scan",
    // Tanggal Tua
    ttTitle: "🚨 End-of-Month Survival",
    ttDesc: "Ultra-budget nutritious recipes until next allowance/payday!",
    ttMoneyLabel: "Remaining Money (IDR)",
    ttDaysLabel: "Days Left",
    ttAllowanceLabel: "Daily Limit:",
    ttPerDay: "/ day",
    ttFindMenu: "Find Menu",
    ttGenerating: "Thinking AI...",
    ttTipsTitle: "Student Survival Tips:",
    ttTip1: "Batch cook carbohydrates and protein base for 3 days to eliminate waste.",
    ttTip2: "Shop at open-air morning markets at 6 AM for deeply discounted fresh produce.",
    ttTip3: "Rely on eggs, tofu & tempeh for dense protein at a fraction of the cost.",
    ttRecTitle: "Budget Savior Recipes",
    ttRecCount: "3 Budget Picks",
    ttDisclaimer: "General balanced-eating guidance only (protein, carbs, vegetables, calories). No medical claims.",
    // Weekly Plan
    wpBudgetLabel: "TOTAL WEEKLY BUDGET",
    wpPerDayLabel: "Rp 25,000 / day • 7-Day Balanced Menu",
    wpAiBtn: "Generate AI Plan",
    wpAiBtnGen: "Thinking AI...",
    wpDays: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
    wpDayTitle: "Menu for",
    wpBatchFilter: "Batch Cook Only (3-Day)",
    // Shopping
    spTitle: "Market Shopping List",
    spBought: "of",
    spBoughtEnd: "items checked",
    spEstTotal: "Estimated Total:",
    spAddBtn: "+ Item",
    spFilterAll: "All",
    spFilterMarket: "🥬 Market",
    spFilterWarung: "🏪 Grocery",
    spFilterMini: "🛍️ Minimarket",
    // Map
    mapTitle: "🗺️ Market & Grocery Radar",
    mapDesc: "Curated stores around university & student dorms in Malang",
    mapArea: "Area: Dinoyo & Lowokwaru Campuses",
    mapLegMarket: "Farmers Market",
    mapLegWarung: "Local Warung",
    mapLegSuper: "Supermarket",
    mapStoresHeading: "Nearby Markets & Stores",
    mapOpenMaps: "Open Maps ↗",
    // Fridge
    fridgeTitle: "Scan Fridge / Pantry",
    fridgeDesc: "Take a photo of leftover ingredients in your dorm. Multimodal Gemini AI will suggest instant zero-waste recipes.",
    fridgeBtn: "Choose / Take Ingredient Photo",
    fridgeAiSuccess: "Gemini Vision AI Recognition",
    fridgeRecipes: "Zero-Waste Recipe Recommendations",
    // Modal
    modalRecipeTitle: "Recipe Details",
    modalCost: "Est. Cost:",
    modalCalories: "Calories",
    modalCookingSteps: "Instructions:",
    modalSubsTitle: "Smart Cost-Saving Swap (Zero Waste):",
    // Preferences Modal
    prefTitle: "⚙️ Preferences & Dorm Budget",
    prefBudgetLabel: "Daily Budget Target (IDR)",
    prefKitchenLabel: "Kitchen Setup Available",
    prefCityLabel: "City / Location",
    prefSaveBtn: "Save Preferences"
  }
};

const state = {
  language: 'ID',
  currentTab: 'tabTanggalTua',
  city: 'Malang',
  dailyBudget: 25000,
  kitchenSetup: 'Kompor 1 Tungku',
  mealsPerDay: 3,
  mascotMood: 'idle', // idle, happy, cooking, worried
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

// ==========================================
// KUKI MAGIC COM RICE COOKER SVG GENERATOR
// ==========================================
function renderKukiSvg(mood = 'idle', size = 80) {
  let eyesAndMouth = '';
  let steamElement = '';
  let sparkles = '';
  let spoonElement = '';

  if (mood === 'happy') {
    // Happy arc eyes ^ ^, big smiling open mouth with pink tongue
    eyesAndMouth = `
      <path d="M 33 42 Q 38 35 43 42" stroke="#2C1810" stroke-width="2.6" fill="none" stroke-linecap="round"/>
      <path d="M 57 42 Q 62 35 67 42" stroke="#2C1810" stroke-width="2.6" fill="none" stroke-linecap="round"/>
      <path d="M 44 48 Q 50 57 56 48 Z" fill="#D32F2F" stroke="#2C1810" stroke-width="1.8"/>
      <circle cx="50" cy="52" r="2.5" fill="#FFA4A4"/>
    `;
    sparkles = `
      <g class="kuki-sparkle" transform="translate(18, 20)">
        <polygon points="4,0 5.2,2.8 8,4 5.2,5.2 4,8 2.8,5.2 0,4 2.8,2.8" fill="#F59E0B"/>
      </g>
      <g class="kuki-sparkle" transform="translate(80, 22)">
        <polygon points="4,0 5.2,2.8 8,4 5.2,5.2 4,8 2.8,5.2 0,4 2.8,2.8" fill="#F59E0B"/>
      </g>
    `;
  } else if (mood === 'worried') {
    // Worried round eyes, wavy mouth, and blue sweat tear
    eyesAndMouth = `
      <ellipse cx="38" cy="40" rx="3.6" ry="4.2" fill="#2C1810"/>
      <ellipse cx="62" cy="40" rx="3.6" ry="4.2" fill="#2C1810"/>
      <path d="M 43 49 Q 46 47 50 49 Q 54 51 57 49" stroke="#2C1810" stroke-width="2" fill="none" stroke-linecap="round"/>
      <path d="M 74 34 Q 78 40 74 44 Q 70 40 74 34 Z" fill="#38BDF8"/>
    `;
  } else if (mood === 'cooking') {
    // Focused eyes, smile, wooden spoon, and rising steam
    eyesAndMouth = `
      <ellipse cx="38" cy="40" rx="3.6" ry="4" fill="#2C1810"/>
      <ellipse cx="62" cy="40" rx="3.6" ry="4" fill="#2C1810"/>
      <circle cx="37" cy="39" r="1.2" fill="#FFF"/>
      <circle cx="61" cy="39" r="1.2" fill="#FFF"/>
      <circle cx="50" cy="48" r="2.2" fill="#2C1810"/>
    `;
    steamElement = `
      <g class="kuki-steam">
        <circle cx="64" cy="10" r="3.5" fill="#FFF" opacity="0.85"/>
        <circle cx="68" cy="4" r="4.5" fill="#FFF" opacity="0.7"/>
        <circle cx="62" cy="-2" r="3" fill="#FFF" opacity="0.5"/>
      </g>
    `;
    spoonElement = `
      <g transform="translate(78, 42) rotate(18)">
        <line x1="0" y1="0" x2="16" y2="12" stroke="#B45309" stroke-width="2.5" stroke-linecap="round"/>
        <ellipse cx="18" cy="13" rx="4" ry="5.5" fill="#D97706"/>
      </g>
    `;
  } else {
    // Normal cute blinking eyes and smile
    eyesAndMouth = `
      <g class="kuki-blink-eye">
        <ellipse cx="38" cy="40" rx="3.5" ry="4.2" fill="#2C1810"/>
        <circle cx="36.8" cy="38.5" r="1.3" fill="#FFF"/>
        <ellipse cx="62" cy="40" rx="3.5" ry="4.2" fill="#2C1810"/>
        <circle cx="60.8" cy="38.5" r="1.3" fill="#FFF"/>
      </g>
      <path d="M 44 47 Q 50 52 56 47" stroke="#2C1810" stroke-width="2.2" fill="none" stroke-linecap="round"/>
    `;
  }

  const switchColor = mood === 'cooking' ? '#D32F2F' : '#F59E0B';

  return `
    <svg class="kuki-mascot ${mood}" viewBox="0 0 100 95" width="${size}" height="${size}" xmlns="http://www.w3.org/2000/svg">
      ${sparkles}
      ${steamElement}

      <!-- Feet -->
      <circle cx="35" cy="78" r="4.5" fill="#8B2500"/>
      <circle cx="65" cy="78" r="4.5" fill="#8B2500"/>

      <!-- Rice Cooker Main Body -->
      <rect x="16" y="24" width="68" height="52" rx="16" fill="#FAF7F2" stroke="#4A3E3D" stroke-width="2.6"/>

      <!-- Lower Trim (Chili Red) -->
      <path d="M 19 68 Q 50 75 81 68 L 79 73 Q 50 79 21 73 Z" fill="#D32F2F"/>

      <!-- Gold Ring Between Lid & Body -->
      <rect x="14" y="21" width="72" height="6" rx="3" fill="#F59E0B"/>

      <!-- Red Chili Dome Lid -->
      <path d="M 14 23 C 14 6 86 6 86 23 Z" fill="#E53935" stroke="#4A3E3D" stroke-width="2.4"/>

      <!-- Lid Knob -->
      <rect x="42" y="5" width="16" height="5" rx="2" fill="#8B2500"/>

      <!-- Steam Vent Hole -->
      <circle cx="64" cy="14" r="2.5" fill="#3E2723"/>

      <!-- Side Handles -->
      <rect x="10" y="38" width="6" height="12" rx="2" fill="#8B2500"/>
      <rect x="84" y="38" width="6" height="12" rx="2" fill="#8B2500"/>

      <!-- Front Switch Panel -->
      <rect x="38" y="53" width="24" height="14" rx="3.5" fill="#FEF3C7"/>
      <rect x="42" y="57" width="16" height="6" rx="2" fill="${switchColor}"/>

      <!-- Blushing Cheeks -->
      <circle cx="30" cy="45" r="4.5" fill="#FFA4A4" opacity="0.6"/>
      <circle cx="70" cy="45" r="4.5" fill="#FFA4A4" opacity="0.6"/>

      <!-- Eyes and Mouth -->
      ${eyesAndMouth}

      ${spoonElement}
    </svg>
  `;
}

// ==========================================
// COMPANION AVATARS (MAS TEMPE & DEK TELUR)
// ==========================================
function renderMasTempeSvg(size = 32) {
  return `
    <svg class="avatar-tempe-svg" viewBox="0 0 40 40" width="${size}" height="${size}" xmlns="http://www.w3.org/2000/svg">
      <rect x="5" y="7" width="30" height="26" rx="6" fill="#FDE68A" stroke="#B45309" stroke-width="2"/>
      <!-- Soybeans spots -->
      <circle cx="12" cy="14" r="2" fill="#D97706"/>
      <circle cx="28" cy="15" r="2" fill="#D97706"/>
      <circle cx="13" cy="27" r="1.8" fill="#D97706"/>
      <circle cx="27" cy="26" r="1.8" fill="#D97706"/>
      <!-- Face -->
      <circle cx="17" cy="19" r="1.8" fill="#292524"/>
      <circle cx="23" cy="19" r="1.8" fill="#292524"/>
      <path d="M 18 23 Q 20 25 22 23" stroke="#292524" stroke-width="1.4" fill="none" stroke-linecap="round"/>
    </svg>
  `;
}

// ==========================================
// APP INITIALIZATION & LOCALIZATION
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
  initClock();
  updateMascots('idle');
  updateAllTranslations();
  handleTangtuaInput();
  renderShoppingList();
  renderStoresList();
  initRadarAnimation();
  loadInitialWeeklyPlan();
});

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

function updateMascots(mood) {
  state.mascotMood = mood;
  // Header Mascot
  const headerMascot = document.getElementById('kukiHeader');
  if (headerMascot) {
    headerMascot.className = "kuki-svg-wrap";
    headerMascot.innerHTML = renderKukiSvg(mood, 38);
  }
  // Tanggal Tua Hero Mascot
  const ttMascot = document.getElementById('tangtuaMascot');
  if (ttMascot) {
    ttMascot.className = "mascot-bubble";
    ttMascot.innerHTML = renderKukiSvg(mood, 68);
  }
}

// Toggle language and re-render everything
function toggleLanguage() {
  state.language = state.language === 'ID' ? 'EN' : 'ID';
  document.getElementById('btnLang').innerText = state.language;
  updateAllTranslations();
  handleTangtuaInput();
  renderShoppingList();
  renderWeeklyMeals();
}

function updateAllTranslations() {
  const t = i18n[state.language];

  // Header
  document.getElementById('headerTagline').innerText = t.tagline;

  // Tabs Nav
  const navTexts = document.querySelectorAll('.nav-text');
  if (navTexts.length >= 5) {
    navTexts[0].innerText = t.tabTanggalTua;
    navTexts[1].innerText = t.tabWeeklyPlan;
    navTexts[2].innerText = t.tabShopping;
    navTexts[3].innerText = t.tabMap;
    navTexts[4].innerText = t.tabFridge;
  }

  // Tanggal Tua Screen
  const ttTitle = document.querySelector('#tabTanggalTua .card-title');
  if (ttTitle) ttTitle.innerText = t.ttTitle;
  const ttDesc = document.querySelector('#tabTanggalTua .card-desc');
  if (ttDesc) ttDesc.innerText = t.ttDesc;
  const ttLabels = document.querySelectorAll('#tabTanggalTua .form-label');
  if (ttLabels.length >= 2) {
    ttLabels[0].innerText = t.ttMoneyLabel;
    ttLabels[1].innerText = t.ttDaysLabel;
  }
  const ttAllowSub = document.querySelector('#tabTanggalTua .allowance-sub');
  if (ttAllowSub) ttAllowSub.innerText = t.ttAllowanceLabel;
  const ttBtnText = document.getElementById('btnTangtuaText');
  if (ttBtnText) ttBtnText.innerText = t.ttFindMenu;
  
  // Tips Header
  const tipsHeader = document.querySelector('#tabTanggalTua .tips-header strong');
  if (tipsHeader) tipsHeader.innerText = t.ttTipsTitle;
  const tipsItems = document.querySelectorAll('#tabTanggalTua .tips-list li');
  if (tipsItems.length >= 3) {
    tipsItems[0].innerHTML = `${renderMasTempeSvg(20)} <span>${t.ttTip1}</span>`;
    tipsItems[1].innerHTML = `• ${t.ttTip2}`;
    tipsItems[2].innerHTML = `• ${t.ttTip3}`;
  }

  const ttRecHeading = document.querySelector('#tabTanggalTua .section-heading span');
  if (ttRecHeading) ttRecHeading.innerText = t.ttRecTitle;
  const ttRecBadge = document.querySelector('#tabTanggalTua .section-heading .badge-tag');
  if (ttRecBadge) ttRecBadge.innerText = t.ttRecCount;

  const ttDisc = document.querySelector('#tabTanggalTua .disclaimer-card p');
  if (ttDisc) ttDisc.innerText = t.ttDisclaimer;

  // Weekly Plan Screen
  const wpSub = document.querySelector('#tabWeeklyPlan .allowance-sub');
  if (wpSub) wpSub.innerText = t.wpBudgetLabel;
  const wpAiBtn = document.querySelector('#tabWeeklyPlan .btn-yellow');
  if (wpAiBtn) wpAiBtn.innerHTML = `<span>⚡</span> ${t.wpAiBtn}`;
  
  const dayChips = document.querySelectorAll('#daysCarousel .day-chip');
  dayChips.forEach((chip, i) => {
    if (t.wpDays[i]) chip.innerText = t.wpDays[i];
  });
  const curDayTitle = document.getElementById('currentDayTitle');
  if (curDayTitle) curDayTitle.innerText = `${t.wpDayTitle} ${t.wpDays[state.selectedDayIndex]}`;
  const batchLabel = document.querySelector('#tabWeeklyPlan .toggle-label span');
  if (batchLabel) batchLabel.innerText = t.wpBatchFilter;

  // Shopping Screen
  const spTitle = document.querySelector('#tabShopping .card-title');
  if (spTitle) spTitle.innerText = t.spTitle;
  const spSub = document.querySelector('#tabShopping .allowance-sub');
  if (spSub) spSub.innerText = t.spEstTotal;
  const spAddBtn = document.querySelector('#tabShopping .btn-green');
  if (spAddBtn) spAddBtn.innerText = t.spAddBtn;

  const filterChips = document.querySelectorAll('#tabShopping .filter-chip');
  if (filterChips.length >= 4) {
    filterChips[0].innerText = t.spFilterAll;
    filterChips[1].innerText = t.spFilterMarket;
    filterChips[2].innerText = t.spFilterWarung;
    filterChips[3].innerText = t.spFilterMini;
  }

  // Map Screen
  const mapTitle = document.querySelector('#tabMap .card-title');
  if (mapTitle) mapTitle.innerText = t.mapTitle;
  const mapDesc = document.querySelector('#tabMap .card-desc');
  if (mapDesc) mapDesc.innerText = t.mapDesc;
  const mapAreaBadge = document.querySelector('#tabMap .radar-overlay-badge');
  if (mapAreaBadge) mapAreaBadge.innerText = t.mapArea;
  const mapHead = document.querySelector('#tabMap .section-heading span');
  if (mapHead) mapHead.innerText = t.mapStoresHeading;
  const mapOpen = document.querySelector('#tabMap .btn-text');
  if (mapOpen) mapOpen.innerText = t.mapOpenMaps;

  // Fridge Screen
  const fridgeTitle = document.querySelector('#tabFridge .card-title');
  if (fridgeTitle) fridgeTitle.innerText = t.fridgeTitle;
  const fridgeDesc = document.querySelector('#tabFridge .card-desc');
  if (fridgeDesc) fridgeDesc.innerText = t.fridgeDesc;
  const fridgeBtn = document.querySelector('#tabFridge .btn-purple');
  if (fridgeBtn) fridgeBtn.innerHTML = `<span>📷</span> ${t.fridgeBtn}`;

  // Preferences Modal
  const prefTitle = document.querySelector('#onboardingModal .modal-title');
  if (prefTitle) prefTitle.innerText = t.prefTitle;
  const prefBtn = document.querySelector('#onboardingModal .btn-red');
  if (prefBtn) prefBtn.innerText = t.prefSaveBtn;
}

// Switch Tab
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
  const t = i18n[state.language];
  const money = parseFloat(document.getElementById('ttMoney').value) || 0;
  const days = parseInt(document.getElementById('ttDays').value) || 1;
  const daily = days > 0 ? Math.round(money / days) : money;

  const formatted = `Rp ${daily.toLocaleString('id-ID')} ${t.ttPerDay}`;
  document.getElementById('ttDailyAllowance').innerText = formatted;

  // Update Mascot mood based on allowance
  if (daily < 10000) {
    updateMascots('worried');
  } else if (daily < 20000) {
    updateMascots('idle');
  } else {
    updateMascots('happy');
  }

  // Populate meals if empty
  if (state.tangtuaMeals.length === 0) {
    state.tangtuaMeals = [
      {
        name: "Nasi Telur Ceplok Kecap Mentega",
        nameEn: "Sunny-Side Up Egg with Sweet Butter Soy Sauce",
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
        batchTipEn: "Add scallions for richer aroma.",
        description: "Klasik andalan anak kos. Telur ceplok renyah dengan siraman mentega kecap manis.",
        descriptionEn: "Dorm student classic. Crispy fried egg drizzled with caramelized sweet butter soy glaze.",
        instructions: ["Goreng telur mata sapi sesuai selera kematangan.", "Lelehkan mentega, tuang kecap manis dan perasan jeruk limau.", "Siramkan ke atas telur dan nasi hangat."],
        instructionsEn: ["Fry egg until edges are golden and crispy.", "Melt butter, stir in sweet soy sauce and fresh lime squeeze.", "Pour over steaming white rice and egg."],
        substitutions: [{ original: "Daging ayam", substitute: "Telur mata sapi", reason: "Protein setara, hemat 60%", savings: "Hemat Rp 6.000" }]
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
        substitutions: [{ original: "Daging cincang", substitute: "Tempe kedelai", reason: "Tinggi protein nabati, murah meriah", savings: "Hemat Rp 8.000" }]
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
        substitutions: [{ original: "Sayur brokoli", substitute: "Bayam segar", reason: "Harga pasar tradisional cuma Rp 2.500/ikat", savings: "Hemat Rp 5.000" }]
      }
    ];
  }
  renderTangtuaMeals();
}

async function generateTangtuaMenu() {
  const t = i18n[state.language];
  const btn = document.getElementById('btnTangtuaGenerate');
  const btnText = document.getElementById('btnTangtuaText');
  const money = parseFloat(document.getElementById('ttMoney').value) || 25000;
  const days = parseInt(document.getElementById('ttDays').value) || 3;

  updateMascots('cooking');
  btnText.innerText = t.ttGenerating;
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
    btnText.innerText = t.ttFindMenu;
    btn.style.opacity = "1";
    btn.disabled = false;
    handleTangtuaInput(); // restores happy/worried mood
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

    const name = isEn && meal.nameEn ? meal.nameEn : meal.name;
    const batchTag = isEn ? "🔁 Batch Cook (3 Servings)" : "🔁 Masak 1x Makan 3x";
    const swapTag = isEn ? "💡 Cost-Saving Swap" : "💡 Ada Subtitusi Hemat";

    return `
      <div class="meal-card" onclick="openRecipeDetail('tangtua', ${idx})">
        <div class="meal-top">
          <div class="meal-thumb">${idx === 0 ? '🍳' : (idx === 1 ? '🥢' : '🥬')}</div>
          <div class="meal-info">
            <div class="meal-name">${name}</div>
            <div class="meal-meta">Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 350} kcal</div>
            <div class="meal-tags-row">
              <span class="badge-tag">${meal.prepTimeMinutes || 15} ${isEn ? 'Mins' : 'Menit'}</span>
              ${meal.isBatchCook ? `<span class="badge-tag green">${batchTag}</span>` : ''}
              ${meal.substitutions && meal.substitutions.length ? `<span class="badge-tag amber">${swapTag}</span>` : ''}
            </div>
          </div>
        </div>
        <div class="balance-bar-wrap">
          <div class="b-prot" style="width: ${pPct}%;"></div>
          <div class="b-carb" style="width: ${cPct}%;"></div>
          <div class="b-veg" style="width: ${vPct}%;"></div>
        </div>
        <div class="balance-legend">
          <span>🍗 ${isEn ? 'Protein' : 'Protein'} ${pPct}%</span>
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

async function generateWeeklyPlan() {
  const t = i18n[state.language];
  const btn = document.querySelector('.btn-yellow');
  if (btn) {
    btn.innerHTML = `<span>⏳</span> ${t.wpAiBtnGen}`;
    btn.disabled = true;
  }

  await loadInitialWeeklyPlan();

  if (btn) {
    btn.innerHTML = `<span>⚡</span> ${t.wpAiBtn}`;
    btn.disabled = false;
  }
}

function selectDay(dayIdx, chip) {
  state.selectedDayIndex = dayIdx;
  document.querySelectorAll('.day-chip').forEach(c => c.classList.remove('active'));
  chip.classList.add('active');

  const t = i18n[state.language];
  document.getElementById('currentDayTitle').innerText = `${t.wpDayTitle} ${t.wpDays[dayIdx]}`;
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
    container.innerHTML = `<div style="text-align:center; padding:24px; color:#6B7280; font-size:12px;">${isEn ? 'No batch cooking meals found for this filter.' : 'Tidak ada menu batch cook untuk filter ini.'}</div>`;
    return;
  }

  container.innerHTML = meals.map((meal, idx) => {
    const name = isEn && meal.nameEn ? meal.nameEn : meal.name;
    const batchTag = isEn ? "🔁 3-Day Batch" : "🔁 Tahan 3 Hari";

    return `
      <div class="meal-card" onclick="openRecipeDetail('weekly', ${idx})">
        <div class="meal-top">
          <div class="meal-thumb">${idx % 2 === 0 ? '🍲' : '🍗'}</div>
          <div class="meal-info">
            <div class="meal-name">${name}</div>
            <div class="meal-meta">Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 380} kcal</div>
            <div class="meal-tags-row">
              <span class="badge-tag">${meal.prepTimeMinutes || 15} ${isEn ? 'Mins' : 'Menit'}</span>
              ${meal.isBatchCook ? `<span class="badge-tag green">${batchTag}</span>` : ''}
              <span class="badge-tag">${meal.toolsNeeded ? meal.toolsNeeded.join(', ') : 'Pan'}</span>
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

  const t = i18n[state.language];
  const isEn = state.language === 'EN';

  let items = state.shoppingItems;
  if (state.shoppingFilter) {
    items = items.filter(it => it.store === state.shoppingFilter);
  }

  const checkedCount = state.shoppingItems.filter(i => i.checked).length;
  const totalAll = state.shoppingItems.reduce((acc, it) => acc + it.price, 0);

  document.getElementById('shoppingCheckedCount').innerText = `${checkedCount} ${t.spBought} ${state.shoppingItems.length} ${t.spBoughtEnd}`;
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
  const name = prompt(isEn ? "New grocery item name:" : "Nama bahan belanja baru:");
  if (name && name.trim()) {
    const priceStr = prompt(isEn ? "Estimated price in IDR (e.g. 5000):" : "Perkiraan harga (contoh: 5000):", "5000");
    const price = parseInt(priceStr) || 5000;
    state.shoppingItems.push({
      id: 's' + Date.now(),
      name: name.trim(),
      nameEn: name.trim(),
      price: price,
      store: 'FARMERS_MARKET',
      storeName: 'Pasar Dinoyo Malang',
      checked: false
    });
    renderShoppingList();
  }
}

// Stores list rendering
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
      
      const detected = data.detectedIngredients || ['Telur Ayam', 'Tempe', 'Bawang Merah', 'Kecap Manis'];
      ingList.innerHTML = detected.map(item => `<span class="badge-tag green">✅ ${item}</span>`).join('');

      const recipes = data.recipes || [data.recipe || {
        name: isEn ? 'Tempeh & Egg Scramble with Soy Glaze' : 'Orak-Arik Tempe Telur Kecap',
        description: isEn ? 'Quick zero-waste dorm recipe utilizing leftover tempeh and eggs.' : 'Menu cepat memanfaatkan sisa tempe dan telur di kulkas kosan.',
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
  const instructionsHtml = rawInstructions.map((step, i) => `<li style="margin-bottom:6px;"><strong>${isEn ? 'Step' : 'Langkah'} ${i+1}:</strong> ${step}</li>`).join('');

  const desc = isEn && meal.descriptionEn ? meal.descriptionEn : (meal.description || '');

  const subsHtml = meal.substitutions && meal.substitutions.length ? `
    <div class="tips-card" style="margin-top:12px;">
      <div class="tips-header"><strong>💡 ${t.modalSubsTitle}</strong></div>
      <p style="font-size:11px; color:#78350F;">
        ${isEn ? 'Swap' : 'Ganti'} <em>${meal.substitutions[0].original}</em> ${isEn ? 'with' : 'dengan'} <strong>${meal.substitutions[0].substitute}</strong>. 
        ${meal.substitutions[0].reason || ''} (${meal.substitutions[0].savings || 'Save 30%'}).
      </p>
    </div>
  ` : '';

  body.innerHTML = `
    <div style="font-size:12px; color:#16A34A; font-weight:800; margin-bottom:8px;">
      ${t.modalCost} Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 350} ${t.modalCalories}
    </div>
    <p style="font-size:12px; color:#4B5563; line-height:1.4; margin-bottom:12px;">
      ${desc}
    </p>

    <div style="font-size:12px; font-weight:800; margin-bottom:6px;">${t.modalCookingSteps}</div>
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
