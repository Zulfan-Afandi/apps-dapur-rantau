package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.NearbyStore
import com.example.data.model.StoreType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MarketRadarView(
  centerLatitude: Double,
  centerLongitude: Double,
  stores: List<NearbyStore>,
  selectedStore: NearbyStore?,
  isScanning: Boolean = false,
  language: AppLanguage = AppLanguage.ID,
  onStoreSelected: (NearbyStore) -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")

  val sweepAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "RadarSweepAngle"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "RadarPulseScale"
  )

  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(Color(0xFF111827))
      .testTag("market_radar_container")
  ) {
    val boxWidthPx = constraints.maxWidth.toFloat()
    val boxHeightPx = constraints.maxHeight.toFloat()
    val radarCenter = Offset(boxWidthPx / 2f, boxHeightPx / 2f)
    val maxRadiusPx = (min(boxWidthPx, boxHeightPx) / 2f) - 36f

    // Calculate blip positions relative to user's coordinate center
    val maxDistanceKm = remember(stores) {
      val maxD = stores.maxOfOrNull { it.distanceKm } ?: 3.0
      maxOf(maxD, 2.0)
    }

    val storeOffsets = remember(stores, centerLatitude, centerLongitude, maxRadiusPx, maxDistanceKm) {
      stores.map { store ->
        val latDiff = store.latitude - centerLatitude
        val lngDiff = store.longitude - centerLongitude
        val bearingRad = atan2(lngDiff, latDiff) // Angle from North (clockwise)
        
        // Normalize distance: 0 at center, maxRadiusPx at outer edge
        val normalizedDist = (store.distanceKm / maxDistanceKm).coerceIn(0.12, 0.92)
        val r = normalizedDist * maxRadiusPx

        // In screen coordinates: Y is down, X is right
        val x = radarCenter.x + (r * sin(bearingRad)).toFloat()
        val y = radarCenter.y - (r * cos(bearingRad)).toFloat()
        Pair(store, Offset(x, y))
      }
    }

    // Canvas drawing Radar circles, sweep, and blips
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(storeOffsets) {
          detectTapGestures { tapOffset ->
            // Find closest store blip within 36dp touch target threshold
            val hitThresholdPx = 36.dp.toPx()
            var closestStore: NearbyStore? = null
            var minDistance = Float.MAX_VALUE

            for ((store, blipOffset) in storeOffsets) {
              val dx = tapOffset.x - blipOffset.x
              val dy = tapOffset.y - blipOffset.y
              val dist = sqrt(dx * dx + dy * dy)
              if (dist <= hitThresholdPx && dist < minDistance) {
                minDistance = dist
                closestStore = store
              }
            }

            closestStore?.let { onStoreSelected(it) }
          }
        }
    ) {
      drawRadarBackground(
        center = radarCenter,
        maxRadius = maxRadiusPx,
        sweepAngle = sweepAngle,
        isScanning = isScanning
      )

      // Draw Store Blips
      storeOffsets.forEach { (store, blipOffset) ->
        val isSelected = store.id == selectedStore?.id
        val blipColor = getStoreColor(store.type)
        
        // Pulse ring around each blip
        drawCircle(
          color = blipColor.copy(alpha = if (isSelected) 0.5f else 0.25f),
          radius = if (isSelected) 14.dp.toPx() * pulseScale else 9.dp.toPx(),
          center = blipOffset
        )
        // Solid core marker
        drawCircle(
          color = Color.White,
          radius = if (isSelected) 7.dp.toPx() else 5.dp.toPx(),
          center = blipOffset
        )
        drawCircle(
          color = blipColor,
          radius = if (isSelected) 5.5.dp.toPx() else 3.8.dp.toPx(),
          center = blipOffset
        )
      }

      // Draw Center User Location Dot with halo
      drawCircle(
        color = Color(0xFF38BDF8).copy(alpha = 0.25f),
        radius = 16.dp.toPx() * pulseScale,
        center = radarCenter
      )
      drawCircle(
        color = Color.White,
        radius = 6.dp.toPx(),
        center = radarCenter
      )
      drawCircle(
        color = Color(0xFF0284C7),
        radius = 4.5.dp.toPx(),
        center = radarCenter
      )
    }

    // Top overlay: Distance Range and Status Indicator
    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(Color(0xFF10B981))
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (language == AppLanguage.ID) "RADAR AKTIF (${stores.size} Toko)" else "RADAR ACTIVE (${stores.size} Stores)",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF34D399),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          letterSpacing = 1.sp
        )
      )
    }

    // Compass Orientation indicator (N / U)
    Text(
      text = if (language == AppLanguage.ID) "U" else "N",
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 10.dp),
      style = MaterialTheme.typography.bodySmall.copy(
        color = Color(0xFF94A3B8),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    )

    // Bottom overlay: Interactive Category Legend
    FlowRow(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.Center,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      RadarLegendBadge(color = Color(0xFFEF4444), label = if (language == AppLanguage.ID) "Pasar" else "Market")
      Spacer(modifier = Modifier.width(8.dp))
      RadarLegendBadge(color = Color(0xFF10B981), label = if (language == AppLanguage.ID) "Warung Sayur" else "Grocery")
      Spacer(modifier = Modifier.width(8.dp))
      RadarLegendBadge(color = Color(0xFFF59E0B), label = if (language == AppLanguage.ID) "Minimarket" else "Minimarket")
      Spacer(modifier = Modifier.width(8.dp))
      RadarLegendBadge(color = Color(0xFF3B82F6), label = if (language == AppLanguage.ID) "Supermarket" else "Supermarket")
    }
  }
}

private fun DrawScope.drawRadarBackground(
  center: Offset,
  maxRadius: Float,
  sweepAngle: Float,
  isScanning: Boolean
) {
  // Dark cyber-radar grid rings
  val ringColor = Color(0xFF1E293B)
  val ringAccent = Color(0xFF334155)

  // Distance concentric rings
  val ringFractions = listOf(0.33f, 0.66f, 1.0f)
  ringFractions.forEachIndexed { index, frac ->
    val r = maxRadius * frac
    drawCircle(
      color = if (index == 2) ringAccent else ringColor,
      radius = r,
      center = center,
      style = Stroke(width = if (index == 2) 1.5.dp.toPx() else 1.dp.toPx())
    )
  }

  // Crosshair axes (North-South, East-West)
  drawLine(
    color = ringColor,
    start = Offset(center.x, center.y - maxRadius),
    end = Offset(center.x, center.y + maxRadius),
    strokeWidth = 1.dp.toPx()
  )
  drawLine(
    color = ringColor,
    start = Offset(center.x - maxRadius, center.y),
    end = Offset(center.x + maxRadius, center.y),
    strokeWidth = 1.dp.toPx()
  )

  // Rotating Radar Beam Sweep (Sweep Gradient Arc)
  val sweepBrush = Brush.sweepGradient(
    0.0f to Color.Transparent,
    0.75f to Color.Transparent,
    0.95f to Color(0x3310B981),
    1.0f to Color(0x8810B981),
    center = center
  )

  // Draw sweep sector
  drawCircle(
    brush = sweepBrush,
    radius = maxRadius,
    center = center
  )
}

@Composable
private fun RadarLegendBadge(color: Color, label: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF1E293B).copy(alpha = 0.85f))
      .padding(horizontal = 7.dp, vertical = 3.dp)
  ) {
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      color = Color(0xFFE2E8F0),
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

fun getStoreColor(type: StoreType): Color {
  return when (type) {
    StoreType.FARMERS_MARKET -> Color(0xFFEF4444) // Traditional Market: Red
    StoreType.GROCERY_CONVENIENCE -> Color(0xFF10B981) // Warung / Grocer: Green
    StoreType.MINIMARKET -> Color(0xFFF59E0B) // Minimarket: Yellow / Amber
    StoreType.SUPERMARKET -> Color(0xFF3B82F6) // Supermarket: Blue
  }
}
