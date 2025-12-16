package com.sdu.threads.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sdu.threads.presentation.theme.UniversityNavy
import com.sdu.threads.presentation.theme.UniversityBlue
import com.sdu.threads.presentation.theme.UniversityLightBlue
import com.sdu.threads.presentation.theme.UniversityGold
import com.sdu.threads.presentation.theme.ThreadsDarkGray

private val LightColors = lightColorScheme(
    primary = UniversityNavy,
    secondary = UniversityBlue,
    tertiary = UniversityGold,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = UniversityLightBlue.copy(alpha = 0.3f),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = UniversityNavy,
    onSurface = UniversityNavy,
    onSurfaceVariant = UniversityNavy.copy(alpha = 0.7f),
    outline = UniversityLightBlue
)

private val DarkColors = darkColorScheme(
    primary = UniversityLightBlue,
    secondary = UniversityBlue,
    tertiary = UniversityGold,
    background = UniversityNavy.copy(alpha = 0.95f),
    surface = ThreadsDarkGray,
    surfaceVariant = UniversityNavy.copy(alpha = 0.8f),
    onPrimary = UniversityNavy,
    onSecondary = UniversityNavy,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = UniversityLightBlue,
    outline = UniversityBlue.copy(alpha = 0.5f)
)

@Composable
fun SduThreadsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme? = null,
    content: @Composable () -> Unit
) {
    val scheme = colorScheme ?: if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        typography = ThreadsTypography,
        content = content
    )
}
