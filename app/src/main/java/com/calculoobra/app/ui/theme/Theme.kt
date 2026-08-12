package com.calculoobra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BorderGray = Color(0xFF334155)

private val DarkColors = darkColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    primaryContainer = BlueDark,
    onPrimaryContainer = TextPrimary,
    secondary = Green,
    onSecondary = Color(0xFF062C12),
    secondaryContainer = GreenDark,
    onSecondaryContainer = Color.White,
    tertiary = Amber,
    onTertiary = Color(0xFF3b2600),
    background = Bg,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextMuted,
    outline = BorderGray,
    error = Red,
    onError = Color.White
)

@Composable
fun CalculoObraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content
    )
}