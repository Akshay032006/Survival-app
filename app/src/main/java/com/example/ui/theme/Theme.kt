package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = ClayTerracotta,
  secondary = ClaySage,
  tertiary = ClayBlue,
  background = Color(0xFF191816),
  surface = Color(0xFF242220),
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFFEBE7DF),
  onSurface = Color(0xFFEBE7DF),
  outlineVariant = Color(0xFF3B3834)
)

private val LightColorScheme = lightColorScheme(
  primary = ClayTerracotta,
  secondary = ClaySage,
  tertiary = ClayBlue,
  background = ClayBackground,
  surface = ClaySurface,
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = ClayTextPrimary,
  onSurface = ClayTextPrimary,
  surfaceVariant = ClaySurfaceElevated,
  outline = ClayBorderLight,
  outlineVariant = ClayBorderHighlight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Clay theme needs cohesive warm palette
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

