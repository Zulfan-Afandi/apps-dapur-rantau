package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = ChiliRedPrimary,
  onPrimary = Color.White,
  primaryContainer = TurmericLight,
  onPrimaryContainer = TurmericDark,
  secondary = PandanGreen,
  onSecondary = Color.White,
  secondaryContainer = PandanGreenLight,
  onSecondaryContainer = PandanGreenDark,
  tertiary = TurmericYellow,
  onTertiary = Color.White,
  background = CreamBackground,
  onBackground = TextDark,
  surface = CreamSurface,
  onSurface = TextDark,
  surfaceVariant = CreamSurfaceVariant,
  onSurfaceVariant = TextMuted,
  outline = BorderSoft,
)

private val DarkColorScheme = darkColorScheme(
  primary = ChiliRedPrimaryDark,
  onPrimary = DarkBackground,
  primaryContainer = DarkSurfaceVariant,
  onPrimaryContainer = ChiliRedPrimaryDark,
  secondary = PandanGreenPrimaryDark,
  onSecondary = DarkBackground,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = PandanGreenPrimaryDark,
  tertiary = TurmericYellowDark,
  onTertiary = DarkBackground,
  background = DarkBackground,
  onBackground = DarkText,
  surface = DarkSurface,
  onSurface = DarkText,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextMuted,
  outline = DarkSurfaceVariant,
)

@Composable
fun DapurRantauTheme(
  darkTheme: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
