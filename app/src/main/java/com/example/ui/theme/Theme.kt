package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = SoftBlack,
    primaryContainer = SoftMutedGray,
    onPrimaryContainer = LuxuryWhite,
    secondary = PremiumGray,
    onSecondary = LuxuryWhite,
    background = SoftBlack,
    onBackground = LuxuryWhite,
    surface = DarkSurface,
    onSurface = LuxuryWhite,
    surfaceVariant = SoftMutedGray,
    onSurfaceVariant = WarmGray,
    outline = CardBorderColor
)

private val LightColorScheme = lightColorScheme(
    primary = SleekSatinGold,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = SleekText,
    secondary = SleekBrightGold,
    onSecondary = SleekText,
    background = SleekBackground,
    onBackground = SleekText,
    surface = Color.White,
    onSurface = SleekText,
    surfaceVariant = SleekLightGray,
    onSurfaceVariant = WarmGray,
    outline = SleekBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
