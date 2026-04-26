package com.etnobulgaria.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BulgarianGreen,
    onPrimary = IvoryWhite,
    primaryContainer = BulgarianGreenLight,
    onPrimaryContainer = ForestNight,
    secondary = EmbroideryRed,
    onSecondary = IvoryWhite,
    secondaryContainer = EmbroideryRedLight,
    onSecondaryContainer = Rosewood,
    background = IvoryWhite,
    onBackground = Charcoal,
    surface = LinenWhite,
    onSurface = Charcoal,
    onSurfaceVariant = WarmGray,
)

private val DarkColorScheme = darkColorScheme(
    primary = BulgarianGreenLight,
    onPrimary = ForestNight,
    primaryContainer = BulgarianGreen,
    onPrimaryContainer = IvoryWhite,
    secondary = EmbroideryRedLight,
    onSecondary = Rosewood,
    secondaryContainer = EmbroideryRed,
    onSecondaryContainer = IvoryWhite,
    background = ForestNight,
    onBackground = IvoryWhite,
    surface = ParchmentDark,
    onSurface = IvoryWhite,
    onSurfaceVariant = ParchmentDarkVariant,
)

@Composable
fun EthnoBulgariaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
