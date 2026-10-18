package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MasTempeAvatar(
  size: Dp = 48.dp,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "tempe_anim")
  val rotation by transition.animateFloat(
    initialValue = -5f,
    targetValue = 5f,
    animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "rot"
  )

  Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height

      // Tempe block (Golden soybean brown)
      drawRoundRect(
        color = Color(0xFFFDE68A),
        topLeft = Offset(w * 0.12f, h * 0.18f),
        size = Size(w * 0.76f, h * 0.64f),
        cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
      )
      drawRoundRect(
        color = Color(0xFFB45309),
        topLeft = Offset(w * 0.12f, h * 0.18f),
        size = Size(w * 0.76f, h * 0.64f),
        cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
        style = Stroke(width = w * 0.04f)
      )

      // Soybean dots inside tempe
      val beanColor = Color(0xFFD97706)
      drawCircle(beanColor, radius = w * 0.05f, center = Offset(w * 0.28f, h * 0.35f))
      drawCircle(beanColor, radius = w * 0.05f, center = Offset(w * 0.72f, h * 0.38f))
      drawCircle(beanColor, radius = w * 0.045f, center = Offset(w * 0.32f, h * 0.68f))
      drawCircle(beanColor, radius = w * 0.045f, center = Offset(w * 0.68f, h * 0.65f))

      // Cute Eyes
      drawCircle(Color(0xFF292524), radius = w * 0.045f, center = Offset(w * 0.42f, h * 0.45f))
      drawCircle(Color(0xFF292524), radius = w * 0.045f, center = Offset(w * 0.58f, h * 0.45f))

      // Smile
      val smile = Path().apply {
        moveTo(w * 0.46f, h * 0.55f)
        quadraticTo(w * 0.50f, h * 0.60f, w * 0.54f, h * 0.55f)
      }
      drawPath(smile, color = Color(0xFF292524), style = Stroke(width = w * 0.035f))
    }
  }
}

@Composable
fun DekTelurAvatar(
  size: Dp = 48.dp,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "telur_anim")
  val bounce by transition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(tween(750, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bounce"
  )

  Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height

      // Egg White
      drawOval(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(w * 0.15f, h * 0.12f + bounce),
        size = Size(w * 0.70f, h * 0.76f)
      )
      drawOval(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(w * 0.15f, h * 0.12f + bounce),
        size = Size(w * 0.70f, h * 0.76f),
        style = Stroke(width = w * 0.04f)
      )

      // Golden Yolk
      val yolkCenter = Offset(w * 0.50f, h * 0.52f + bounce)
      drawCircle(Color(0xFFFBBF24), radius = w * 0.22f, center = yolkCenter)

      // Yolk Face
      drawCircle(Color(0xFF78350F), radius = w * 0.035f, center = Offset(w * 0.44f, h * 0.49f + bounce))
      drawCircle(Color(0xFF78350F), radius = w * 0.035f, center = Offset(w * 0.56f, h * 0.49f + bounce))

      val smile = Path().apply {
        moveTo(w * 0.47f, h * 0.56f + bounce)
        quadraticTo(w * 0.50f, h * 0.60f + bounce, w * 0.53f, h * 0.56f + bounce)
      }
      drawPath(smile, color = Color(0xFF78350F), style = Stroke(width = w * 0.03f))

      // Cheeks
      drawCircle(Color(0xFFFCA5A5).copy(alpha = 0.6f), radius = w * 0.035f, center = Offset(w * 0.38f, h * 0.54f + bounce))
      drawCircle(Color(0xFFFCA5A5).copy(alpha = 0.6f), radius = w * 0.035f, center = Offset(w * 0.62f, h * 0.54f + bounce))
    }
  }
}

@Composable
fun MbakCabeAvatar(
  size: Dp = 48.dp,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "cabe_anim")
  val tilt by transition.animateFloat(
    initialValue = -8f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(tween(850, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "tilt"
  )

  Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height

      // Green stem
      val stemPath = Path().apply {
        moveTo(w * 0.45f, h * 0.22f)
        quadraticTo(w * 0.35f, h * 0.10f, w * 0.48f, h * 0.06f)
      }
      drawPath(stemPath, color = Color(0xFF15803D), style = Stroke(width = w * 0.06f))

      // Chili Body
      val chiliPath = Path().apply {
        moveTo(w * 0.40f, h * 0.24f)
        lineTo(w * 0.60f, h * 0.24f)
        quadraticTo(w * 0.72f, h * 0.50f, w * 0.54f, h * 0.88f)
        quadraticTo(w * 0.38f, h * 0.65f, w * 0.40f, h * 0.24f)
        close()
      }
      drawPath(chiliPath, color = Color(0xFFDC2626))
      drawPath(chiliPath, color = Color(0xFF991B1B), style = Stroke(width = w * 0.035f))

      // Cute Eyes on chili
      drawCircle(Color.White, radius = w * 0.04f, center = Offset(w * 0.48f, h * 0.42f))
      drawCircle(Color(0xFF1E293B), radius = w * 0.025f, center = Offset(w * 0.48f, h * 0.42f))

      drawCircle(Color.White, radius = w * 0.04f, center = Offset(w * 0.58f, h * 0.42f))
      drawCircle(Color(0xFF1E293B), radius = w * 0.025f, center = Offset(w * 0.58f, h * 0.42f))

      // Smile
      val mouth = Path().apply {
        moveTo(w * 0.51f, h * 0.50f)
        quadraticTo(w * 0.54f, h * 0.54f, w * 0.57f, h * 0.50f)
      }
      drawPath(mouth, color = Color.White, style = Stroke(width = w * 0.025f))
    }
  }
}
