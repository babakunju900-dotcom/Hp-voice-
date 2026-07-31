package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = HelloPrimary,
    onPrimary = HelloOnPrimary,
    primaryContainer = HelloPrimaryContainer,
    onPrimaryContainer = HelloOnPrimaryContainer,
    secondary = HelloSecondary,
    onSecondary = HelloOnSecondary,
    secondaryContainer = HelloSecondaryContainer,
    onSecondaryContainer = HelloOnSecondaryContainer,
    tertiary = HelloTertiary,
    onTertiary = HelloOnTertiary,
    tertiaryContainer = HelloTertiaryContainer,
    onTertiaryContainer = HelloOnTertiaryContainer,
    background = HelloBackground,
    onBackground = HelloOnBackground,
    surface = HelloSurface,
    onSurface = HelloOnSurface,
    surfaceVariant = HelloSurfaceVariant,
    onSurfaceVariant = HelloOnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = HelloPrimaryDark,
    onPrimary = HelloOnPrimaryDark,
    secondary = HelloSecondary,
    tertiary = HelloTertiary,
    background = HelloBackgroundDark,
    onBackground = HelloOnSurface,
    surface = HelloSurfaceDark,
    surfaceVariant = HelloSurfaceVariantDark
)

@Composable
fun HelloTalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent HelloTalk branded colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
