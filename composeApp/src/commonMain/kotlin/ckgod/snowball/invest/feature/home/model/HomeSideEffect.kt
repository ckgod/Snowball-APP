package ckgod.snowball.invest.feature.home.model

/**
 * 홈 화면의 일회성 부수효과 (Navigation, Toast 등)
 */
sealed interface HomeSideEffect {
    /**
     * 종목 상세 화면으로 이동
     */
    data class NavigateToStockDetail(val ticker: String) : HomeSideEffect

    /**
     * 에러 토스트 표시
     */
    data class ShowErrorToast(val message: String) : HomeSideEffect
}
