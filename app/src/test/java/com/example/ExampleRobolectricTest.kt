package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.maps.OsmApiClient
import com.example.data.model.DemoLocations
import com.example.data.repository.DapurRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dapur Rantau", appName)
  }

  @Test
  fun `verify Malang curated demo dataset`() {
    val repository = DapurRepository()
    
    // Default: Malang
    assertEquals("Malang", repository.localeContext.value.city)
    assertEquals("IDR", repository.localeContext.value.currency)
    assertTrue("Malang dataset should have at least 10 stores", repository.nearbyStores.value.size >= 10)

    val malangPreset = DemoLocations.PRESETS.first { it.city == "Malang" }
    repository.applyDemoPreset(malangPreset)
    assertEquals("Malang", repository.localeContext.value.city)
    assertEquals("IDR", repository.localeContext.value.currency)
    assertTrue(repository.nearbyStores.value.isNotEmpty())
  }

  @Test
  fun `verify shopping items and actual price update`() {
    val repository = DapurRepository()
    val shoppingItems = repository.shoppingItems.value
    assertTrue(shoppingItems.isNotEmpty())

    val item = shoppingItems.first()
    repository.toggleShoppingItem(item.id)
    val toggled = repository.shoppingItems.value.first { it.id == item.id }
    assertTrue(toggled.isChecked)

    repository.updateActualPrice(item.id, 15000.0)
    val updated = repository.shoppingItems.value.first { it.id == item.id }
    assertEquals(15000.0, updated.actualPrice ?: 0.0, 0.001)
  }

  @Test
  fun `verify mathematical Haversine distance computation`() {
    // Distance between Malang (-7.9430, 112.6070) and nearby location
    val dist = OsmApiClient.calculateHaversineDistanceKm(-7.9430, 112.6070, -7.9460, 112.6090)
    assertTrue("Distance should be non-negative and realistic", dist > 0.0 && dist < 1.0)
  }
}
