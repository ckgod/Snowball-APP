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
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    primaryContainer = SurfaceVariantDark,
    secondaryContainer = SecondaryContainerDark,
    tertiaryContainer = TertiaryContainerDark,
    onPrimaryContainer = OnBackgroundDark,
    onSecondaryContainer = Grey80,
    onTertiaryContainer = Grey80,
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
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    primaryContainer = SurfaceVariantLight,
    secondaryContainer = SecondaryContainerLight,
    tertiaryContainer = TertiaryContainerLight,
    onPrimaryContainer = OnBackgroundLight,
    onSecondaryContainer = Grey50,
    onTertiaryContainer = Grey50,
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
fun getProfitColor(profit: Double, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return when {
        profit < 0 -> {
            if (isDarkTheme) LossRedLight else LossRedDark
        }
        profit > 0 -> {
            if (isDarkTheme) ProfitGreenLight else ProfitGreenDark
        }
        else -> {
            if (isDarkTheme) OnSurfaceVariantDark else OnSurfaceVariantLight
        }
    }
}

/**
 * 확장 함수: 진행 상태 색상 반환
 */
@Composable
fun getProgressColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (isDarkTheme) ProgressBlueLight else ProgressBlueDark
}

/**
 * 확장 함수: 매수 색상 반환 (한국 주식시장 컨벤션 - Red)
 */
@Composable
fun getBuySideColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (isDarkTheme) BuyRedLight else BuyRedDark
}

/**
 * 확장 함수: 매도 색상 반환 (한국 주식시장 컨벤션 - Blue)
 */
@Composable
fun getSellSideColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (isDarkTheme) SellBlueLight else SellBlueDark
}
