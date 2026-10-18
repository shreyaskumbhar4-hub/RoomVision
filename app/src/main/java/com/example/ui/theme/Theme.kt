package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = DarkPrimary,
        onPrimary = Slate900,
        primaryContainer = Slate800,
        onPrimaryContainer = DarkPrimary,
        secondary = DarkSecondary,
        onSecondary = Slate900,
        tertiary = DarkTertiary,
        background = DarkBackground,
        onBackground = Slate100,
        surface = DarkSurface,
        onSurface = Slate100,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = Slate300
    )

private val LightColorScheme =
    lightColorScheme(
        primary = LightPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0F2FE),
        onPrimaryContainer = Color(0xFF0369A1),
        secondary = LightSecondary,
        onSecondary = Color.White,
        tertiary = LightTertiary,
        background = LightBackground,
        onBackground = Slate900,
        surface = LightSurface,
        onSurface = Slate900,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = Slate700
    )

@Composable
fun RoomVisionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

// Retain alias for backward compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) = RoomVisionTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
