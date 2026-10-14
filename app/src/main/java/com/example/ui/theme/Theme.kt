package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WhatsAppGreenLight,
    onPrimary = Color.Black,
    primaryContainer = WhatsAppGreenDark,
    onPrimaryContainer = Color.White,
    secondary = WhatsAppTeal,
    onSecondary = Color.White,
    background = WhatsAppDarkSurface,
    onBackground = WhatsAppTextPrimaryDark,
    surface = WhatsAppDarkSurface,
    onSurface = WhatsAppTextPrimaryDark,
    surfaceVariant = WhatsAppDarkCard,
    onSurfaceVariant = WhatsAppTextSecondaryDark,
    outline = WhatsAppDividerDark
)

private val LightColorScheme = lightColorScheme(
    primary = WhatsAppGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = WhatsAppGreenDark,
    onPrimaryContainer = Color.White,
    secondary = WhatsAppTeal,
    onSecondary = Color.White,
    background = WhatsAppLightBackground,
    onBackground = WhatsAppTextPrimaryLight,
    surface = WhatsAppLightSurface,
    onSurface = WhatsAppTextPrimaryLight,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = WhatsAppTextSecondaryLight,
    outline = WhatsAppDividerLight
)

@Composable
fun WhatsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
