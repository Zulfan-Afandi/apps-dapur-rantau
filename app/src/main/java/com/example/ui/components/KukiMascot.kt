package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.example.data.model.MascotState
import kotlin.math.sin

@Composable
fun KukiMascot(
  state: MascotState = MascotState.IDLE,
  size: Dp = 90.dp,
  modifier: Modifier = Modifier,
  isPaused: Boolean = false
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val isResumed = lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED
  val shouldPause = isPaused || !isResumed
  
  val infiniteTransition = rememberInfiniteTransition(label = "kuki_anim")

  // Gentle vertical bounce
  val bounceY by if (shouldPause) mutableStateOf(0f) else infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bounce"
  )

  // Blink animation
  val blinkScale by if (isPaused) mutableStateOf(1f) else infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 0.1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blink"
  )

  // Steam float for cooking state
  val steamOffset by if (isPaused) mutableStateOf(0f) else infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 12f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "steam"
  )

  // Spoon or hand rotation
  val waveAngle by if (isPaused) mutableStateOf(0f) else infiniteTransition.animateFloat(
    initialValue = -15f,
    targetValue = 15f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height

      val effectiveBounce = if (state == MascotState.HAPPY) bounceY * 2f else bounceY
      val centerY = h * 0.52f + effectiveBounce

      // Draw Steam if cooking
      if (state == MascotState.COOKING) {
        drawSteam(w * 0.72f, centerY - h * 0.35f - steamOffset, steamOffset)
      }

      // Draw Mascot Body (White with warm shadow)
      val bodyWidth = w * 0.72f
      val bodyHeight = h * 0.56f
      val bodyLeft = (w - bodyWidth) / 2f
      val bodyTop = centerY - bodyHeight / 2f + h * 0.05f

      // Cute feet
      drawCircle(Color(0xFF8B2500), radius = w * 0.055f, center = Offset(w * 0.35f, bodyTop + bodyHeight - h * 0.02f))
      drawCircle(Color(0xFF8B2500), radius = w * 0.055f, center = Offset(w * 0.65f, bodyTop + bodyHeight - h * 0.02f))

      // Main Rice Cooker Body
      drawRoundRect(
        color = Color(0xFFFAF7F2),
        topLeft = Offset(bodyLeft, bodyTop),
        size = Size(bodyWidth, bodyHeight),
        cornerRadius = CornerRadius(w * 0.18f, w * 0.18f)
      )
      // Body stroke
      drawRoundRect(
        color = Color(0xFF4A3E3D),
        topLeft = Offset(bodyLeft, bodyTop),
        size = Size(bodyWidth, bodyHeight),
        cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
        style = Stroke(width = w * 0.025f)
      )

      // Lower bottom trim (Red Chili)
      val bottomTrimPath = Path().apply {
        moveTo(bodyLeft + w * 0.04f, bodyTop + bodyHeight - h * 0.09f)
        lineTo(bodyLeft + bodyWidth - w * 0.04f, bodyTop + bodyHeight - h * 0.09f)
        lineTo(bodyLeft + bodyWidth - w * 0.09f, bodyTop + bodyHeight)
        lineTo(bodyLeft + w * 0.09f, bodyTop + bodyHeight)
        close()
      }
      drawPath(bottomTrimPath, color = Color(0xFFD32F2F))

      // Lid / Cap (Red Chili Lid with Golden Ring)
      val lidWidth = w * 0.76f
      val lidHeight = h * 0.22f
      val lidLeft = (w - lidWidth) / 2f
      val lidTop = bodyTop - h * 0.12f

      // Gold ring
      drawRoundRect(
        color = Color(0xFFF59E0B),
        topLeft = Offset(lidLeft + w * 0.02f, bodyTop - h * 0.02f),
        size = Size(lidWidth - w * 0.04f, h * 0.06f),
        cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
      )

      // Dome lid
      val lidPath = Path().apply {
        moveTo(lidLeft, bodyTop)
        quadraticTo(w / 2f, lidTop - h * 0.06f, lidLeft + lidWidth, bodyTop)
        close()
      }
      drawPath(lidPath, color = Color(0xFFE53935))
      drawPath(lidPath, color = Color(0xFF4A3E3D), style = Stroke(width = w * 0.025f))

      // Lid Knob
      drawRoundRect(
        color = Color(0xFF8B2500),
        topLeft = Offset(w * 0.44f, lidTop - h * 0.08f),
        size = Size(w * 0.12f, h * 0.05f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
      )

      // Steam vent valve
      drawCircle(Color(0xFF3E2723), radius = w * 0.025f, center = Offset(w * 0.62f, lidTop - h * 0.01f))

      // Side Handles
      drawRoundRect(
        color = Color(0xFF8B2500),
        topLeft = Offset(bodyLeft - w * 0.05f, bodyTop + h * 0.18f),
        size = Size(w * 0.06f, h * 0.10f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
      )
      drawRoundRect(
        color = Color(0xFF8B2500),
        topLeft = Offset(bodyLeft + bodyWidth - w * 0.01f, bodyTop + h * 0.18f),
        size = Size(w * 0.06f, h * 0.10f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
      )

      // Front Button / Display Panel
      val panelWidth = w * 0.22f
      val panelHeight = h * 0.14f
      val panelLeft = (w - panelWidth) / 2f
      val panelTop = bodyTop + bodyHeight - h * 0.22f

      drawRoundRect(
        color = Color(0xFFFEF3C7),
        topLeft = Offset(panelLeft, panelTop),
        size = Size(panelWidth, panelHeight),
        cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
      )
      // Cook switch button
      drawRoundRect(
        color = if (state == MascotState.COOKING) Color(0xFFD32F2F) else Color(0xFFF59E0B),
        topLeft = Offset(panelLeft + w * 0.04f, panelTop + h * 0.04f),
        size = Size(panelWidth - w * 0.08f, h * 0.06f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
      )

      // Mascot Face Features
      val eyeY = bodyTop + h * 0.18f
      val leftEyeX = w * 0.40f
      val rightEyeX = w * 0.60f

      // Blushing Cheeks
      drawCircle(Color(0xFFFF8A80).copy(alpha = 0.5f), radius = w * 0.045f, center = Offset(w * 0.32f, eyeY + h * 0.06f))
      drawCircle(Color(0xFFFF8A80).copy(alpha = 0.5f), radius = w * 0.045f, center = Offset(w * 0.68f, eyeY + h * 0.06f))

      when (state) {
        MascotState.IDLE, MascotState.WAVING -> {
          // Normal cute round eyes with blink
          val actualBlink = if (blinkScale < 0.25f) 0.15f else 1f
          drawOval(
            color = Color(0xFF2C1810),
            topLeft = Offset(leftEyeX - w * 0.035f, eyeY - (h * 0.04f * actualBlink)),
            size = Size(w * 0.07f, h * 0.08f * actualBlink)
          )
          drawOval(
            color = Color(0xFF2C1810),
            topLeft = Offset(rightEyeX - w * 0.035f, eyeY - (h * 0.04f * actualBlink)),
            size = Size(w * 0.07f, h * 0.08f * actualBlink)
          )
          // Eye shine
          if (actualBlink > 0.5f) {
            drawCircle(Color.White, radius = w * 0.015f, center = Offset(leftEyeX - w * 0.01f, eyeY - h * 0.015f))
            drawCircle(Color.White, radius = w * 0.015f, center = Offset(rightEyeX - w * 0.01f, eyeY - h * 0.015f))
          }
          // Smile
          val smilePath = Path().apply {
            moveTo(w * 0.45f, eyeY + h * 0.05f)
            quadraticTo(w * 0.50f, eyeY + h * 0.09f, w * 0.55f, eyeY + h * 0.05f)
          }
          drawPath(smilePath, color = Color(0xFF2C1810), style = Stroke(width = w * 0.025f))
        }
        MascotState.HAPPY -> {
          // Happy arc eyes ^_^
          val eyeArcLeft = Path().apply {
            moveTo(leftEyeX - w * 0.04f, eyeY + h * 0.01f)
            quadraticTo(leftEyeX, eyeY - h * 0.04f, leftEyeX + w * 0.04f, eyeY + h * 0.01f)
          }
          val eyeArcRight = Path().apply {
            moveTo(rightEyeX - w * 0.04f, eyeY + h * 0.01f)
            quadraticTo(rightEyeX, eyeY - h * 0.04f, rightEyeX + w * 0.04f, eyeY + h * 0.01f)
          }
          drawPath(eyeArcLeft, color = Color(0xFF2C1810), style = Stroke(width = w * 0.025f))
          drawPath(eyeArcRight, color = Color(0xFF2C1810), style = Stroke(width = w * 0.025f))

          // Big happy mouth with pink tongue
          val happyMouth = Path().apply {
            moveTo(w * 0.44f, eyeY + h * 0.04f)
            quadraticTo(w * 0.50f, eyeY + h * 0.12f, w * 0.56f, eyeY + h * 0.04f)
            close()
          }
          drawPath(happyMouth, color = Color(0xFFD32F2F))
          drawPath(happyMouth, color = Color(0xFF2C1810), style = Stroke(width = w * 0.02f))

          // Sparkles
          drawSparkle(w * 0.18f, eyeY - h * 0.1f)
          drawSparkle(w * 0.82f, eyeY - h * 0.08f)
        }
        MascotState.COOKING -> {
          // Determined smiling eyes
          drawCircle(Color(0xFF2C1810), radius = w * 0.035f, center = Offset(leftEyeX, eyeY))
          drawCircle(Color(0xFF2C1810), radius = w * 0.035f, center = Offset(rightEyeX, eyeY))

          // Small focused mouth
          drawCircle(Color(0xFF2C1810), radius = w * 0.025f, center = Offset(w * 0.5f, eyeY + h * 0.06f))

          // Little wooden spoon in hand
          val spoonAngle = waveAngle
          drawSpoon(w * 0.82f, bodyTop + h * 0.22f, spoonAngle)
        }
        MascotState.WORRIED -> {
          // Worried round eyes
          drawCircle(Color(0xFF2C1810), radius = w * 0.035f, center = Offset(leftEyeX, eyeY))
          drawCircle(Color(0xFF2C1810), radius = w * 0.035f, center = Offset(rightEyeX, eyeY))

          // Wavy mouth ~
          val worriedMouth = Path().apply {
            moveTo(w * 0.44f, eyeY + h * 0.07f)
            quadraticTo(w * 0.47f, eyeY + h * 0.05f, w * 0.50f, eyeY + h * 0.07f)
            quadraticTo(w * 0.53f, eyeY + h * 0.09f, w * 0.56f, eyeY + h * 0.07f)
          }
          drawPath(worriedMouth, color = Color(0xFF2C1810), style = Stroke(width = w * 0.022f))

          // Blue sweat drop
          val sweatPath = Path().apply {
            moveTo(w * 0.74f, eyeY - h * 0.05f)
            quadraticTo(w * 0.77f, eyeY + h * 0.02f, w * 0.74f, eyeY + h * 0.04f)
            quadraticTo(w * 0.71f, eyeY + h * 0.02f, w * 0.74f, eyeY - h * 0.05f)
            close()
          }
          drawPath(sweatPath, color = Color(0xFF38BDF8))
        }
      }

      // Hand Waving if WAVING
      if (state == MascotState.WAVING) {
        drawHandWave(bodyLeft + bodyWidth - w * 0.02f, bodyTop + h * 0.25f, waveAngle)
      }
    }
  }
}

private fun DrawScope.drawSteam(x: Float, y: Float, offset: Float) {
  val steamColor = Color.White.copy(alpha = (0.9f - (offset / 15f)).coerceIn(0.1f, 0.9f))
  drawCircle(steamColor, radius = size.width * 0.04f, center = Offset(x, y))
  drawCircle(steamColor, radius = size.width * 0.055f, center = Offset(x + size.width * 0.04f, y - size.height * 0.04f))
  drawCircle(steamColor, radius = size.width * 0.03f, center = Offset(x - size.width * 0.03f, y - size.height * 0.07f))
}

private fun DrawScope.drawSparkle(x: Float, y: Float) {
  val s = size.width * 0.04f
  val sparklePath = Path().apply {
    moveTo(x, y - s)
    lineTo(x + s * 0.3f, y - s * 0.3f)
    lineTo(x + s, y)
    lineTo(x + s * 0.3f, y + s * 0.3f)
    lineTo(x, y + s)
    lineTo(x - s * 0.3f, y + s * 0.3f)
    lineTo(x - s, y)
    lineTo(x - s * 0.3f, y - s * 0.3f)
    close()
  }
  drawPath(sparklePath, color = Color(0xFFF59E0B))
}

private fun DrawScope.drawSpoon(x: Float, y: Float, angle: Float) {
  val rad = Math.toRadians(angle.toDouble()).toFloat()
  val cos = kotlin.math.cos(rad)
  val sin = kotlin.math.sin(rad)

  val w = size.width
  val handleLength = w * 0.28f
  val endX = x + sin * handleLength
  val endY = y - cos * handleLength

  // Spoon handle (wood color)
  drawLine(
    color = Color(0xFFB45309),
    start = Offset(x, y),
    end = Offset(endX, endY),
    strokeWidth = w * 0.03f
  )
  // Spoon head
  drawOval(
    color = Color(0xFFD97706),
    topLeft = Offset(endX - w * 0.04f, endY - w * 0.05f),
    size = Size(w * 0.08f, w * 0.10f)
  )
}

private fun DrawScope.drawHandWave(x: Float, y: Float, angle: Float) {
  val w = size.width
  drawCircle(Color.White, radius = w * 0.05f, center = Offset(x + w * 0.04f, y - w * 0.04f + angle * 0.3f))
  drawCircle(Color(0xFF4A3E3D), radius = w * 0.05f, center = Offset(x + w * 0.04f, y - w * 0.04f + angle * 0.3f), style = Stroke(width = w * 0.02f))
}
