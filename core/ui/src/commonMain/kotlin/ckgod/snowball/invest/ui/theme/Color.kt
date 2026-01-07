package ckgod.snowball.invest.ui.theme

import androidx.compose.ui.graphics.Color

// Neutral Colors (검은색/회색 계열)
// Dark Mode용
val Grey90 = Color(0xFFE0E0E0)
val Grey80 = Color(0xFFBDBDBD)
val Grey70 = Color(0xFF9E9E9E)

// Light Mode용
val Grey60 = Color(0xFF757575)
val Grey50 = Color(0xFF616161)
val Grey40 = Color(0xFF424242)

// Stock-specific Colors (미국 주식 기준)
// 상승/수익: 초록색
val ProfitGreen = Color(0xFF4CAF50)
val ProfitGreenDark = Color(0xFF388E3C)
val ProfitGreenLight = Color(0xFF81C784)

// 하락/손실: 빨간색
val LossRed = Color(0xFFF44336)
val LossRedDark = Color(0xFFD32F2F)
val LossRedLight = Color(0xFFE57373)

// 전략 진행 상태: 파란색
val ProgressBlue = Color(0xFF2196F3)
val ProgressBlueDark = Color(0xFF1976D2)
val ProgressBlueLight = Color(0xFF64B5F6)

// Phase Colors (전반전/후반전/쿼터모드)
val PhaseCrystalLightStart = Color(0xFF40C4FF) // Light Blue A200
val PhaseCrystalLightEnd   = Color(0xFF0288D1) // Light Blue 700

val PhaseCrystalDarkStart  = Color(0xFF80D8FF) // Light Blue A100 (하이라이트)
val PhaseCrystalDarkEnd    = Color(0xFF00B0FF) // Light Blue A400 (본체)

val PhaseSolarLightStart = Color(0xFFFFD740) // Amber A200 (밝은 금색)
val PhaseSolarLightEnd   = Color(0xFFFF6D00) // Orange A700 (강렬한 오렌지)

val PhaseSolarDarkStart  = Color(0xFFFFE57F) // Amber A100 (하이라이트)
val PhaseSolarDarkEnd    = Color(0xFFFF9100) // Orange A400 (네온 오렌지)

val PhaseMagentaLightStart = Color(0xFFEA80FC) // Purple A100 (밝은 보라)
val PhaseMagentaLightEnd   = Color(0xFFD50000) // Red A700 (피색에 가까운 빨강)

val PhaseMagentaDarkStart  = Color(0xFFFF80AB) // Pink A100 (흰끼 도는 핑크)
val PhaseMagentaDarkEnd    = Color(0xFFF50057) // Pink A400 (강렬한 핫핑크)

val PhaseAmber = Color(0xFFFFAB00)
val PhaseAmberDark = Color(0xFFFF9100)
val PhaseAmberLight = Color(0xFFFFD740)

val PhasePurple = Color(0xFFD500F9)
val PhasePurpleDark = Color(0xFFAA00FF)
val PhasePurpleLight = Color(0xFFE040FB)

// Trading Side Colors (한국 주식시장 컨벤션)
// 매수: 빨간색
val BuyRed = LossRed
val BuyRedDark = LossRedDark
val BuyRedLight = LossRedLight

// 매도: 파란색
val SellBlue = ProgressBlue
val SellBlueDark = ProgressBlueDark
val SellBlueLight = ProgressBlueLight

// Background Colors
val BackgroundDark = Color(0xFF000000)        // 순수 검은색
val BackgroundLight = Color(0xFFF5F5F5)       // 연한 회색

val SurfaceDark = Color(0xFF121212)           // 약간 밝은 검은색
val SurfaceLight = Color(0xFFFFFFFF)          // 흰색

val SurfaceVariantDark = Color(0xFF1E1E1E)    // 카드용
val SurfaceVariantLight = Color(0xFFFAFAFA)   // 카드용

// Surface Container Colors (NavigationBar 등)
val SurfaceContainerDark = Color(0xFF1C1C1C)
val SurfaceContainerLight = Color(0xFFEAEAEA)

val SurfaceContainerHighDark = Color(0xFF2A2A2A)
val SurfaceContainerHighLight = Color(0xFFE0E0E0)

val SurfaceContainerHighestDark = Color(0xFF353535)
val SurfaceContainerHighestLight = Color(0xFFD5D5D5)

// Container Colors
val SecondaryContainerDark = Color(0xFF2A2A2A)
val SecondaryContainerLight = Color(0xFFE0E0E0)

val TertiaryContainerDark = Color(0xFF2A2A2A)
val TertiaryContainerLight = Color(0xFFE0E0E0)

// Text Colors
val OnBackgroundDark = Color(0xFFE0E0E0)      // 다크모드 텍스트
val OnBackgroundLight = Color(0xFF212121)     // 라이트모드 텍스트

val OnSurfaceVariantDark = Color(0xFFBDBDBD)  // 보조 텍스트 (다크)
val OnSurfaceVariantLight = Color(0xFF757575) // 보조 텍스트 (라이트)
