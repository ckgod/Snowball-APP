package ckgod.snowball.invest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Grey80,
    secondary = Grey70,
    tertiary = Grey90,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = OnBackgroundDark,
    outline = Grey70,
    outlineVariant = Grey70.copy(alpha = 0.3f),
)

private val LightColorScheme = lightColorScheme(
    primary = Grey50,
    secondary = Grey60,
    tertiary = Grey40,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = OnBackgroundLight,
    outline = Grey60,
    outlineVariant = Grey60.copy(alpha = 0.3f),
)

@Composable
fun SnowballTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * 확장 함수: 수익/손실 여부에 따른 색상 반환
 */
@Composable
fun getProfitColor(isProfit: Boolean, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (isProfit) {
        if (isDarkTheme) ProfitGreenLight else ProfitGreenDark
    } else {
        if (isDarkTheme) LossRedLight else LossRedDark
    }
}

/**
 * 확장 함수: 진행 상태 색상 반환
 */
@Composable
fun getProgressColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (isDarkTheme) ProgressBlueLight else ProgressBlueDark
}
