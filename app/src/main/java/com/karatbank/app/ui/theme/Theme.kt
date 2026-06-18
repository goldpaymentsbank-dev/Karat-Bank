package com.karatbank.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = KaratGold,
    secondary = LightGold,
    tertiary = White80,
    background = DeepBlack,
    surface = DarkGrey,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onTertiary = DeepBlack,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun KaratBankTheme(
    darkTheme: Boolean = true, // Force dark theme for premium feel
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
