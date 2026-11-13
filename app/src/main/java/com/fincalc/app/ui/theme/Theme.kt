package com.fincalc.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * FinCalc theme — premium royal blue + purple on white (light) and deep
 * navy (dark), matching the supplied design screenshots.
 */
val FinCalcBlue = Color(0xFF1D4ED8)
val FinCalcBlueDark = Color(0xFF1E3A8A)
val FinCalcBlueDeep = Color(0xFF17255A)
val FinCalcPurple = Color(0xFF7C3AED)
val FinCalcPurpleLight = Color(0xFFA78BFA)
val FinCalcGold = Color(0xFFF59E0B)
val FinCalcGreen = Color(0xFF16A34A)
val FinCalcBackground = Color(0xFFF4F6FB)

private val LightColors = lightColorScheme(
    primary = FinCalcBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    secondary = FinCalcPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    background = FinCalcBackground,
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE8ECF5),
    onSurfaceVariant = Color(0xFF6B7280),
    error = Color(0xFFDC2626)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0B1B3A),
    primaryContainer = Color(0xFF1E3A8A),
    secondary = FinCalcPurpleLight,
    onSecondary = Color(0xFF1E1033),
    secondaryContainer = Color(0xFF2E1065),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Color(0xFFF87171)
)

@Composable
fun FinCalcTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
