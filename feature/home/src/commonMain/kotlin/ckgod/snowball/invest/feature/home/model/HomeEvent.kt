package ckgod.snowball.invest.feature.home.model

/**
 * 홈 화면에서 발생하는 사용자 이벤트
 */
sealed interface HomeEvent {
    /**
     * 화면 진입 시 데이터 로드
     */
    data object LoadPortfolio : HomeEvent

    /**
     * 새로고침
     */
    data object Refresh : HomeEvent

    /**
     * 종목 카드 클릭
     */
    data class OnStockClick(val ticker: String) : HomeEvent

    /**
     * 에러 메시지 확인 (dismiss)
     */
    data object DismissError : HomeEvent
}
