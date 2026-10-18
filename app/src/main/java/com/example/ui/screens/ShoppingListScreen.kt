package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.LocaleContext
import com.example.data.model.PriceConfidence
import com.example.data.model.ShoppingItem
import com.example.data.model.StoreType
import com.example.ui.components.DekTelurAvatar
import com.example.ui.components.MasTempeAvatar

@Composable
fun ShoppingListScreen(
  shoppingItems: List<ShoppingItem>,
  weeklyBudget: Double,
  localeContext: LocaleContext,
  language: AppLanguage,
  selectedStoreFilter: StoreType?,
  onToggleItem: (String) -> Unit,
  onUpdateActualPrice: (String, Double?) -> Unit,
  onAddItem: (String, String, Double, StoreType) -> Unit,
  onDeleteItem: (String) -> Unit,
  onSetStoreFilter: (StoreType?) -> Unit
) {
  var editingItem by remember { mutableStateOf<ShoppingItem?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  val filteredItems = if (selectedStoreFilter == null) {
    shoppingItems
  } else {
    shoppingItems.filter { it.category == selectedStoreFilter }
  }

  val totalEstimated = shoppingItems.sumOf { it.estimatedPrice }
  val totalActual = shoppingItems.sumOf { it.effectivePrice }
  val checkedCount = shoppingItems.count { it.isChecked }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFFFBF2))
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header Hero Card: Spending Comparison in Local Currency
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
            Column {
              Text(
                text = if (language == AppLanguage.ID) "Daftar Belanja Pasar" else "Market Shopping List",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFD32F2F)
                )
              )
              Text(
                text = if (language == AppLanguage.ID)
                  "$checkedCount dari ${shoppingItems.size} bahan terbeli • ${localeContext.city}"
                else
                  "$checkedCount of ${shoppingItems.size} items checked • ${localeContext.city}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C))
              )
            }
            MasTempeAvatar(size = 40.dp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Estimasi vs Real Paid Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFFFFFBEB))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Estimasi Pasar" else "Est. Cost",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF78716C))
              )
              Text(
                text = localeContext.formatPrice(totalEstimated),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309)
                )
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(Color(0xFFFDE68A))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (language == AppLanguage.ID) "Realisasi Dibayar" else "Actual Paid",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF78716C))
              )
              Text(
                text = localeContext.formatPrice(totalActual),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = if (totalActual <= totalEstimated) Color(0xFF16A34A) else Color(0xFFD32F2F)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Disclaimer Note about Estimated Prices & Confidence
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF3F4F6))
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = Color(0xFF6B7280),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.ID)
                "Harga adalah ESTIMASI lokal dengan tingkat akurasi (Tinggi/Sedang). Ketuk harga untuk catat realisasi bayar."
              else
                "Prices are local ESTIMATES with confidence flags. Tap any price to log what you actually paid.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFF4B5563))
            )
          }
        }
      }
    }

    // Filter Chips for Store Types
    item {
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          val isAllSelected = selectedStoreFilter == null
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isAllSelected) Color(0xFFD32F2F) else Color.White)
              .border(1.dp, if (isAllSelected) Color(0xFFD32F2F) else Color(0xFFE7E0D3), RoundedCornerShape(12.dp))
              .clickable { onSetStoreFilter(null) }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = if (language == AppLanguage.ID) "Semua Tempat" else "All Types",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isAllSelected) Color.White else Color(0xFF292524)
              )
            )
          }
        }

        items(StoreType.entries.toTypedArray()) { storeType ->
          val isSelected = selectedStoreFilter == storeType
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) Color(0xFFD32F2F) else Color.White)
              .border(1.dp, if (isSelected) Color(0xFFD32F2F) else Color(0xFFE7E0D3), RoundedCornerShape(12.dp))
              .clickable { onSetStoreFilter(storeType) }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = "${storeType.emoji} ${if (language == AppLanguage.ID) storeType.labelId else storeType.labelEn}",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF292524)
              )
            )
          }
        }
      }
    }

    // Add item button
    item {
      OutlinedButton(
        onClick = { showAddDialog = true },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_shopping_item_button")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (language == AppLanguage.ID) "Tambah Bahan Belanja Sendiri" else "Add Custom Ingredient",
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Shopping Items List
    if (filteredItems.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            DekTelurAvatar(size = 48.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (language == AppLanguage.ID) "Tidak ada item untuk kategori ini." else "No items for this store type.",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    } else {
      items(filteredItems) { item ->
        ShoppingItemRowItem(
          item = item,
          localeContext = localeContext,
          language = language,
          onToggle = { onToggleItem(item.id) },
          onEditPrice = { editingItem = item },
          onDelete = { onDeleteItem(item.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Dialog to Edit Actual Price Paid
  if (editingItem != null) {
    val current = editingItem!!
    var inputPrice by remember { mutableStateOf(current.effectivePrice.toString()) }

    AlertDialog(
      onDismissRequest = { editingItem = null },
      title = {
        Text(text = if (language == AppLanguage.ID) "Catat Harga Dibayar" else "Log Actual Price Paid")
      },
      text = {
        Column {
          Text(
            text = if (language == AppLanguage.ID) current.name else current.nameEn,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          Text(
            text = if (language == AppLanguage.ID)
              "Estimasi awal: ${localeContext.formatPrice(current.estimatedPrice)}"
            else
              "Original estimate: ${localeContext.formatPrice(current.estimatedPrice)}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78716C)),
            modifier = Modifier.padding(bottom = 12.dp)
          )
          OutlinedTextField(
            value = inputPrice,
            onValueChange = { inputPrice = it },
            label = { Text(if (language == AppLanguage.ID) "Harga Riil (${localeContext.currency})" else "Actual Price (${localeContext.currency})") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().testTag("edit_actual_price_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val p = inputPrice.toDoubleOrNull()
            onUpdateActualPrice(current.id, p)
            editingItem = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
          modifier = Modifier.testTag("save_actual_price_button")
        ) {
          Text(if (language == AppLanguage.ID) "Simpan" else "Save")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { editingItem = null }) {
          Text(if (language == AppLanguage.ID) "Batal" else "Cancel")
        }
      }
    )
  }

  // Dialog to Add Custom Shopping Item
  if (showAddDialog) {
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("1") }
    var itemEstPrice by remember { mutableStateOf("5") }
    var itemStore by remember { mutableStateOf(StoreType.FARMERS_MARKET) }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = {
        Text(text = if (language == AppLanguage.ID) "Tambah Bahan Belanja" else "Add Shopping Item")
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text(if (language == AppLanguage.ID) "Nama Bahan" else "Item Name") },
            modifier = Modifier.fillMaxWidth().testTag("add_item_name_input")
          )
          OutlinedTextField(
            value = itemEstPrice,
            onValueChange = { itemEstPrice = it },
            label = { Text(if (language == AppLanguage.ID) "Perkiraan Harga (${localeContext.currency})" else "Est. Price (${localeContext.currency})") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().testTag("add_item_price_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (itemName.isNotBlank()) {
              val p = itemEstPrice.toDoubleOrNull() ?: 5.0
              onAddItem(itemName, itemQty, p, itemStore)
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
          Text(if (language == AppLanguage.ID) "Tambahkan" else "Add")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showAddDialog = false }) {
          Text(if (language == AppLanguage.ID) "Batal" else "Cancel")
        }
      }
    )
  }
}

@Composable
fun ShoppingItemRowItem(
  item: ShoppingItem,
  localeContext: LocaleContext,
  language: AppLanguage,
  onToggle: () -> Unit,
  onEditPrice: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (item.isChecked) Color(0xFFF9FAFB) else Color.White
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(
        if (item.isChecked) Color(0xFFE5E7EB) else Color(0xFFF3E8DF)
      )
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("shopping_item_${item.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = item.isChecked,
        onCheckedChange = { onToggle() },
        colors = CheckboxDefaults.colors(
          checkedColor = Color(0xFF16A34A),
          uncheckedColor = Color(0xFF9CA3AF)
        ),
        modifier = Modifier.size(28.dp).testTag("checkbox_${item.id}")
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (language == AppLanguage.ID) item.name else item.nameEn,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
            color = if (item.isChecked) Color(0xFF9CA3AF) else Color(0xFF1F2937)
          )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${item.category.emoji} ${if (language == AppLanguage.ID) item.quantity else item.quantityEn} • ",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF78716C))
          )
          // Confidence badge
          Text(
            text = when (item.confidence) {
              PriceConfidence.HIGH -> if (language == AppLanguage.ID) "Akurat" else "High Conf."
              PriceConfidence.MEDIUM -> if (language == AppLanguage.ID) "Sedang" else "Med Conf."
              PriceConfidence.LOW -> if (language == AppLanguage.ID) "Kasar" else "Rough Est."
            },
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = when (item.confidence) {
                PriceConfidence.HIGH -> Color(0xFF15803D)
                PriceConfidence.MEDIUM -> Color(0xFFB45309)
                PriceConfidence.LOW -> Color(0xFF6B7280)
              }
            )
          )
        }
      }

      // Price Button (Clickable to Edit)
      Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onEditPrice() }
          .padding(4.dp)
      ) {
        Text(
          text = localeContext.formatPrice(item.effectivePrice),
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Black,
            color = if (item.actualPrice != null) Color(0xFF16A34A) else Color(0xFFD32F2F)
          )
        )
        Text(
          text = if (item.actualPrice != null) {
            if (language == AppLanguage.ID) "✓ Riil" else "✓ Paid"
          } else {
            if (language == AppLanguage.ID) "Estimasi" else "Est. (Tap)"
          },
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 9.sp,
            color = if (item.actualPrice != null) Color(0xFF16A34A) else Color(0xFF9CA3AF)
          )
        )
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.size(28.dp).padding(start = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Delete",
          tint = Color(0xFFD1D5DB),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
