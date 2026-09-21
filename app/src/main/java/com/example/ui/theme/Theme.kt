package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = StudioCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF8CF4FF),

    secondary = StudioAmber,
    onSecondary = Color(0xFF422C00),
    secondaryContainer = Color(0xFF5E4000),
    onSecondaryContainer = Color(0xFFFFDF9E),

    tertiary = StudioPurple,
    onTertiary = Color(0xFF270086),
    tertiaryContainer = Color(0xFF4300B4),
    onTertiaryContainer = Color(0xFFE4DFFF),

    background = StudioBackground,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioTextSecondary,

    outline = StudioBorder,
    outlineVariant = Color(0xFF1E2330),
    error = StudioRed,
    onError = Color.White
)

@Composable
fun AhmedEditsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
