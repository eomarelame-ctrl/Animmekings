package com.animekings.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val AnimeKingsDarkScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonPink,
    tertiary = NeonPurpleDim,
    background = BgBlack,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = TextSecondary
)

/**
 * Anime KINGS is Arabic-first and RTL by design, so layout direction is
 * forced to RTL regardless of the device's system locale.
 */
@Composable
fun AnimeKingsTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = AnimeKingsDarkScheme,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}
