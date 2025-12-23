package ckgod.snowball.invest.feature.detail.model

/**
 * 종목 상세 화면 이벤트
 */
sealed class StockDetailEvent {
    data object BackClick : StockDetailEvent()
    data object Refresh : StockDetailEvent()
}
