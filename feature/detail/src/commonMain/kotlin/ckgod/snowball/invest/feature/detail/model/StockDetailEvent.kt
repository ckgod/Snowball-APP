package ckgod.snowball.invest.feature.detail.model

/**
 * 종목 상세 화면 이벤트
 */
sealed class StockDetailEvent {
    data object BackClick : StockDetailEvent()
    data class CancelOrder(val orderNo: String) : StockDetailEvent()
    data class ModifyOrder(val orderNo: String, val price: Double, val quantity: Int) : StockDetailEvent()
    data object OrderMessageShown : StockDetailEvent()
}
