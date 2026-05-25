package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ChineseRed,
    onPrimary = Color.White,
    secondary = ChineseGold,
    onSecondary = Color.Black,
    tertiary = ChineseJade,
    background = ChineseDark,
    onBackground = TextLight,
    surface = CardBackground,
    onSurface = TextLight
)

private val LightColorScheme = lightColorScheme(
    primary = ChineseRed,
    onPrimary = Color.White,
    secondary = ChineseGoldDark,
    onSecondary = Color.Black,
    tertiary = ChineseJade,
    background = Color(0xFFFAF6EE), // Elegant ivory paper tone
    onBackground = Color(0xFF1E1E1E),
    surface = Color.White,
    onSurface = Color(0xFF1E1E1E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark immersive look for traditional lantern vibe
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
