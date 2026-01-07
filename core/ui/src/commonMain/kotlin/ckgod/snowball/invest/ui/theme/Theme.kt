package ckgod.snowball.invest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.ckgod.snowball.model.TradePhase

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
        typography = getTypography(),
        content = content
    )
}

/**
 * 확장 함수: 수익/손실 여부에 따른 색상 반환 (한국 주식시장 스타일)
 * - 수익(상승): 빨간색
 * - 손실(하락): 파란색
 */
@Composable
fun getProfitColor(profit: Double, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return when {
        profit < 0 -> {
            if (isDarkTheme) SellBlueLight else SellBlueDark
        }
        profit > 0 -> {
            if (isDarkTheme) BuyRedLight else BuyRedDark
        }
        else -> {
            if (isDarkTheme) OnSurfaceVariantDark else OnSurfaceVariantLight
        }
    }
}

@Composable
fun getProfitColor(profit: String, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return when {
        profit.startsWith("-") -> {
            if (isDarkTheme) SellBlueLight else SellBlueDark
        }
        profit.startsWith("+") -> {
            if (isDarkTheme) BuyRedLight else BuyRedDark
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

@Composable
fun getPhaseColor(phase: TradePhase, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    return when (phase) {
        TradePhase.FIRST_HALF -> {
            if (isDarkTheme) PhaseCrystalDarkStart else PhaseCrystalLightStart
        }
        TradePhase.BACK_HALF -> {
            if (isDarkTheme) PhaseSolarDarkStart else PhaseSolarLightStart
        }
        TradePhase.QUARTER_MODE,
        TradePhase.EXHAUSTED -> {
            if (isDarkTheme) PhaseMagentaLightEnd else PhaseMagentaDarkEnd
        }
        TradePhase.UNKNOWN -> {
            if (isDarkTheme) OnSurfaceVariantDark else OnSurfaceVariantLight
        }
    }
}

@Composable
fun getPhaseBrush(phase: TradePhase, isDarkTheme: Boolean = isSystemInDarkTheme()): Brush {
    val colors = when (phase) {
        TradePhase.FIRST_HALF -> {
             if (isDarkTheme) {
                 listOf(PhaseCrystalDarkStart, PhaseCrystalDarkEnd)
             } else {
                 listOf(PhaseCrystalLightStart, PhaseCrystalLightEnd)
             }
        }
        TradePhase.BACK_HALF -> {
            if (isDarkTheme) {
                listOf(PhaseSolarDarkStart, PhaseSolarDarkEnd)
            } else {
                listOf(PhaseSolarLightStart, PhaseSolarLightEnd)
            }
        }
        TradePhase.QUARTER_MODE,
        TradePhase.EXHAUSTED -> {
            if (isDarkTheme) {
                listOf(PhaseMagentaDarkStart, PhaseMagentaDarkEnd)
            } else {
                listOf(PhaseMagentaLightStart, PhaseMagentaLightEnd)
            }
        }
        TradePhase.UNKNOWN -> {
            listOf(Color.Gray, Color.DarkGray)
        }
    }

    return Brush.horizontalGradient(colors = colors)
}

@Composable
fun getPhaseTrackColor(phase: TradePhase, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    val baseColor = when (phase) {
        TradePhase.FIRST_HALF -> {
             if (isDarkTheme) PhaseCrystalDarkEnd else PhaseCrystalLightEnd
        }
        TradePhase.BACK_HALF -> {
            if (isDarkTheme) PhaseSolarDarkEnd else PhaseSolarLightEnd
        }
        TradePhase.QUARTER_MODE,
        TradePhase.EXHAUSTED -> {
            if (isDarkTheme) PhaseMagentaDarkEnd else PhaseMagentaLightEnd
        }
        TradePhase.UNKNOWN -> {
            Color.Gray
        }
    }

    return baseColor.copy(alpha = 0.15f)
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
