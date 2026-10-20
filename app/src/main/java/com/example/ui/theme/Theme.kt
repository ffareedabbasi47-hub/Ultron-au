package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val UltronDarkColorScheme = darkColorScheme(
    primary = UltronCyan,
    onPrimary = UltronObsidian,
    primaryContainer = UltronSurfaceElevated,
    onPrimaryContainer = UltronCyan,
    secondary = UltronViolet,
    onSecondary = UltronTextPrimary,
    secondaryContainer = UltronSurfaceElevated,
    onSecondaryContainer = UltronTextPrimary,
    tertiary = UltronCrimson,
    onTertiary = UltronTextPrimary,
    background = UltronObsidian,
    onBackground = UltronTextPrimary,
    surface = UltronSurface,
    onSurface = UltronTextPrimary,
    surfaceVariant = UltronSurfaceElevated,
    onSurfaceVariant = UltronTextSecondary,
    outline = UltronGlassBorder,
    error = UltronCrimson,
    onError = UltronTextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UltronDarkColorScheme,
        typography = Typography,
        content = content
    )
}
