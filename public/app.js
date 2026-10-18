// Dapur Rantau - Client Application Logic

const state = {
  language: 'ID',
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
    { id: 's1', name: 'Telur Ayam 1/2 kg', price: 14000, store: 'FARMERS_MARKET', storeName: 'Pasar Dinoyo', checked: true },
    { id: 's2', name: 'Tempe 1 Papan', price: 5000, store: 'FARMERS_MARKET', storeName: 'Pasar Dinoyo', checked: true },
    { id: 's3', name: 'Bayam Segar 2 Ikat', price: 4000, store: 'FARMERS_MARKET', storeName: 'Pasar Oro-Oro Dowo', checked: false },
    { id: 's4', name: 'Bawang Merah & Putih', price: 7000, store: 'GROCERY_CONVENIENCE', storeName: 'Warung Bu Siti', checked: false },
    { id: 's5', name: 'Kecap Manis Sachet', price: 2000, store: 'MINIMARKET', storeName: 'Minimarket Dinoyo', checked: true },
    { id: 's6', name: 'Jagung Manis 1 Buah', price: 3000, store: 'FARMERS_MARKET', storeName: 'Pasar Dinoyo', checked: false }
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

// Language toggle (ID / EN)
function toggleLanguage() {
  state.language = state.language === 'ID' ? 'EN' : 'ID';
  document.getElementById('btnLang').innerText = state.language;
  document.getElementById('headerTagline').innerText = state.language === 'ID' ? 'Menu Hemat & Bergizi' : 'Budget AI Meal Planner';
  handleTangtuaInput();
  renderShoppingList();
  renderWeeklyMeals();
}

// Tab navigation switcher
function switchNav(tabId, btn) {
  document.querySelectorAll('.tab-page').forEach(page => page.classList.remove('active'));
  document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));

  const target = document.getElementById(tabId);
  if (target) target.classList.add('active');
  btn.classList.add('active');
  state.currentTab = tabId;

  // Scroll to top
  const body = document.getElementById('screenBody');
  if (body) body.scrollTop = 0;
}

// Mode Tanggal Tua live calculation
function handleTangtuaInput() {
  const money = parseFloat(document.getElementById('ttMoney').value) || 0;
  const days = parseInt(document.getElementById('ttDays').value) || 1;
  const daily = days > 0 ? Math.round(money / days) : money;

  const formatted = `Rp ${daily.toLocaleString('id-ID')} / hari`;
  document.getElementById('ttDailyAllowance').innerText = formatted;

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
        estimatedCost: Math.min(6000, daily),
        approxCalories: 410,
        proteinGrams: 14,
        carbsGrams: 55,
        veggiesGrams: 15,
        prepTimeMinutes: 10,
        toolsNeeded: ["Wajan"],
        isBatchCook: false,
        batchTip: "Bisa ditambahkan daun bawang agar lebih harum.",
        description: "Klasik andalan anak kos. Telur ceplok renyah dengan siraman mentega kecap manis.",
        instructions: ["Goreng telur mata sapi sesuai selera kematangan.", "Lelehkan mentega, tuang kecap manis dan perasan jeruk limau.", "Siramkan ke atas telur dan nasi hangat."],
        substitutions: [{ original: "Daging ayam", substitute: "Telur mata sapi", reason: "Protein setara, hemat 60%", savings: "Hemat Rp 6.000" }]
      },
      {
        name: "Orek Tempe Basah Manis Gurih",
        estimatedCost: Math.min(4500, daily),
        approxCalories: 320,
        proteinGrams: 18,
        carbsGrams: 35,
        veggiesGrams: 12,
        prepTimeMinutes: 15,
        toolsNeeded: ["Wajan"],
        isBatchCook: true,
        batchTip: "Masak satu papan tempe untuk persediaan makan 3 hari berturut-turut.",
        description: "Tinggi protein nabati, ramah dompet, dan tahan disimpan 3 hari tanpa kulkas.",
        instructions: ["Potong tempe bentuk korek api, goreng setengah matang.", "Tumis bawang merah, bawang putih, dan cabai.", "Beri kecap manis, garam, dan sedikit air, masak sampai meresap."],
        substitutions: [{ original: "Daging cincang", substitute: "Tempe kedelai", reason: "Tinggi protein nabati, murah meriah", savings: "Hemat Rp 8.000" }]
      },
      {
        name: "Sayur Bening Bayam & Jagung Manis",
        estimatedCost: Math.min(5000, daily),
        approxCalories: 180,
        proteinGrams: 6,
        carbsGrams: 28,
        veggiesGrams: 45,
        prepTimeMinutes: 12,
        toolsNeeded: ["Panci"],
        isBatchCook: false,
        batchTip: "Sayur bayam sebaiknya dihabiskan dalam hari yang sama.",
        description: "Menjaga stamina dan serat tubuh anak kos agar tetap bugar saat ujian akhir bulan.",
        instructions: ["Rebus air bersama irisan kunci dan bawang merah hingga harum.", "Masukkan jagung manis, rebus hingga empuk.", "Masukkan bayam, beri garam dan gula secukupnya. Angkat segera."],
        substitutions: [{ original: "Sayur brokoli", substitute: "Bayam segar", reason: "Harga pasar tradisional cuma Rp 2.500/ikat", savings: "Hemat Rp 5.000" }]
      }
    ];
    renderTangtuaMeals();
  }
}

// Generate Tanggal Tua menu via Backend / Gemini API
async function generateTangtuaMenu() {
  const btn = document.getElementById('btnTangtuaGenerate');
  const btnText = document.getElementById('btnTangtuaText');
  const money = parseFloat(document.getElementById('ttMoney').value) || 25000;
  const days = parseInt(document.getElementById('ttDays').value) || 3;

  btnText.innerText = "Meracik AI...";
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
    btnText.innerText = "Cari Menu";
    btn.style.opacity = "1";
    btn.disabled = false;
  }
}

function renderTangtuaMeals() {
  const container = document.getElementById('tangtuaMealsContainer');
  if (!container) return;

  container.innerHTML = state.tangtuaMeals.map((meal, idx) => {
    const pWidth = meal.proteinGrams || 20;
    const cWidth = meal.carbsGrams || 50;
    const vWidth = meal.veggiesGrams || 30;
    const total = pWidth + cWidth + vWidth;

    const pPct = Math.round((pWidth / total) * 100);
    const cPct = Math.round((cWidth / total) * 100);
    const vPct = 100 - pPct - cPct;

    return `
      <div class="meal-card" onclick="openRecipeDetail('tangtua', ${idx})">
        <div class="meal-top">
          <div class="meal-thumb">${idx === 0 ? '🍳' : (idx === 1 ? '🥢' : '🥬')}</div>
          <div class="meal-info">
            <div class="meal-name">${meal.name}</div>
            <div class="meal-meta">Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 350} kcal</div>
            <div class="meal-tags-row">
              <span class="badge-tag">${meal.prepTimeMinutes || 15} Menit</span>
              ${meal.isBatchCook ? '<span class="badge-tag green">🔁 Masak 1x Makan 3x</span>' : ''}
              ${meal.substitutions && meal.substitutions.length ? '<span class="badge-tag amber">💡 Ada Subtitusi Hemat</span>' : ''}
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
          <span>🍚 Karbo ${cPct}%</span>
          <span>🥦 Sayur ${vPct}%</span>
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
  const btn = document.querySelector('.btn-yellow');
  if (btn) {
    btn.innerHTML = '<span>⏳</span> Meracik AI...';
    btn.disabled = true;
  }

  await loadInitialWeeklyPlan();

  if (btn) {
    btn.innerHTML = '<span>⚡</span> Buat Menu AI';
    btn.disabled = false;
  }
}

function selectDay(dayIdx, chip) {
  state.selectedDayIndex = dayIdx;
  document.querySelectorAll('.day-chip').forEach(c => c.classList.remove('active'));
  chip.classList.add('active');

  const days = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu', 'Minggu'];
  document.getElementById('currentDayTitle').innerText = `Menu Hari ${days[dayIdx]}`;
  renderWeeklyMeals();
}

function toggleBatchCookFilter() {
  state.batchCookFilter = document.getElementById('batchCookFilter').checked;
  renderWeeklyMeals();
}

function renderWeeklyMeals() {
  const container = document.getElementById('weeklyMealsContainer');
  if (!container) return;

  let meals = state.weeklyMeals;
  if (state.batchCookFilter) {
    meals = meals.filter(m => m.isBatchCook);
  }

  if (meals.length === 0) {
    container.innerHTML = `<div style="text-align:center; padding:24px; color:#6B7280; font-size:12px;">Tidak ada menu batch cook untuk filter ini.</div>`;
    return;
  }

  container.innerHTML = meals.map((meal, idx) => {
    return `
      <div class="meal-card" onclick="openRecipeDetail('weekly', ${idx})">
        <div class="meal-top">
          <div class="meal-thumb">${idx % 2 === 0 ? '🍲' : '🍗'}</div>
          <div class="meal-info">
            <div class="meal-name">${meal.name}</div>
            <div class="meal-meta">Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.mealType || 'Siang'} • ${meal.approxCalories || 380} kcal</div>
            <div class="meal-tags-row">
              <span class="badge-tag">${meal.prepTimeMinutes || 15} Menit</span>
              ${meal.isBatchCook ? '<span class="badge-tag green">🔁 Tahan 3 Hari</span>' : ''}
              <span class="badge-tag">${meal.toolsNeeded ? meal.toolsNeeded.join(', ') : 'Wajan'}</span>
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

  let items = state.shoppingItems;
  if (state.shoppingFilter) {
    items = items.filter(it => it.store === state.shoppingFilter);
  }

  const checkedCount = state.shoppingItems.filter(i => i.checked).length;
  const totalChecked = state.shoppingItems.reduce((acc, it) => acc + (it.checked ? it.price : 0), 0);
  const totalAll = state.shoppingItems.reduce((acc, it) => acc + it.price, 0);

  document.getElementById('shoppingCheckedCount').innerText = `${checkedCount} dari ${state.shoppingItems.length} bahan terbeli`;
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
        <div class="shopping-name" style="font-size: 12px; font-weight: 700;">${item.name}</div>
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
  const name = prompt("Nama bahan belanja baru:");
  if (name && name.trim()) {
    const priceStr = prompt("Perkiraan harga (contoh: 5000):", "5000");
    const price = parseInt(priceStr) || 5000;
    state.shoppingItems.push({
      id: 's' + Date.now(),
      name: name.trim(),
      price: price,
      store: 'FARMERS_MARKET',
      storeName: 'Pasar Tradisional Malang',
      checked: false
    });
    renderShoppingList();
  }
}

// Stores List rendering
function renderStoresList() {
  const container = document.getElementById('storesListContainer');
  if (!container) return;

  container.innerHTML = state.stores.map(st => `
    <div class="store-card">
      <div>
        <div class="store-name">${st.name}</div>
        <div class="store-sub">${st.type === 'FARMERS_MARKET' ? '🥬 Pasar Tradisional' : (st.type === 'GROCERY_CONVENIENCE' ? '🏪 Warung Sayur' : '🛍️ Supermarket')} • Jam: ${st.hours}</div>
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

  const reader = new FileReader();
  reader.onload = async (event) => {
    const base64 = event.target.result;
    const resultCard = document.getElementById('fridgeResultCard');
    const ingList = document.getElementById('detectedIngredientsList');
    const recipeDiv = document.getElementById('fridgeRecipeResult');

    resultCard.style.display = 'block';
    ingList.innerHTML = '<span>⏳ Menganalisis dengan Gemini Vision...</span>';
    recipeDiv.innerHTML = '';

    try {
      const res = await fetch('/api/fridge-scan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageBase64: base64 })
      });
      const data = await res.json();
      
      const detected = data.detectedIngredients || ['Telur', 'Tempe', 'Bawang Merah', 'Kecap'];
      ingList.innerHTML = detected.map(item => `<span class="badge-tag green">✅ ${item}</span>`).join('');

      const recipes = data.recipes || [data.recipe || {
        name: 'Orak-Arik Tempe Telur Kecap',
        description: 'Menu cepat memanfaatkan sisa tempe dan telur di kulkas kosan.',
        estimatedCost: 7000,
        prepTimeMinutes: 12
      }];

      recipeDiv.innerHTML = recipes.map(r => `
        <div class="meal-card" style="margin-top:8px;">
          <div class="meal-name">${r.name}</div>
          <div class="meal-meta">Rp ${Number(r.estimatedCost).toLocaleString('id-ID')} • ${r.prepTimeMinutes || 12} Menit</div>
          <p style="font-size:11px; color:#4B5563; margin-top:4px;">${r.description}</p>
        </div>
      `).join('');
    } catch (err) {
      ingList.innerHTML = '<span style="color:#EF4444;">Gagal menghubungi AI. Menampilkan saran default.</span>';
    }
  };
  reader.readAsDataURL(file);
}

// Recipe detail modal
function openRecipeDetail(type, idx) {
  const list = type === 'tangtua' ? state.tangtuaMeals : state.weeklyMeals;
  const meal = list[idx];
  if (!meal) return;

  document.getElementById('modalRecipeName').innerText = meal.name;
  const body = document.getElementById('modalRecipeBody');

  const instructionsHtml = (meal.instructions || ["Siapkan bahan.", "Masak hingga matang dan bumbu meresap.", "Sajikan selagi hangat."])
    .map((step, i) => `<li style="margin-bottom:6px;"><strong>Langkah ${i+1}:</strong> ${step}</li>`).join('');

  const subsHtml = meal.substitutions && meal.substitutions.length ? `
    <div class="tips-card" style="margin-top:12px;">
      <div class="tips-header"><strong>💡 Trik Substitusi Hemat (Zero Waste):</strong></div>
      <p style="font-size:11px; color:#78350F;">
        Ganti <em>${meal.substitutions[0].original}</em> dengan <strong>${meal.substitutions[0].substitute}</strong>. 
        ${meal.substitutions[0].reason || ''} (${meal.substitutions[0].savings || 'Hemat hingga 30%'}).
      </p>
    </div>
  ` : '';

  body.innerHTML = `
    <div style="font-size:12px; color:#16A34A; font-weight:800; margin-bottom:8px;">
      Estimasi Biaya: Rp ${Number(meal.estimatedCost).toLocaleString('id-ID')} • ${meal.approxCalories || 350} Kalori
    </div>
    <p style="font-size:12px; color:#4B5563; line-height:1.4; margin-bottom:12px;">
      ${meal.description || 'Pilihan menu bergizi dan ramah kantong mahasiswa.'}
    </p>

    <div style="font-size:12px; font-weight:800; margin-bottom:6px;">Cara Memasak:</div>
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
