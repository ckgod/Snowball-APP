package ckgod.snowball.invest.domain.model

/**
 * 거래 히스토리 아이템
 * 상세 화면의 타임라인에 표시될 데이터
 */
sealed class HistoryItem {
    abstract val dateTime: String // yyyyMMddHHmmss 형식

    /**
     * 거래/주문 히스토리
     */
    data class Trade(
        override val dateTime: String,
        val type: TradeType,
        val orderType: OrderType,
        val price: Double,
        val quantity: Int,
        val status: TradeStatus
    ) : HistoryItem()

    /**
     * 일일 동기화 (수익 정산)
     */
    data class Sync(
        override val dateTime: String,
        val profit: Double,
        val tValueUpdate: String // "11 -> 12"
    ) : HistoryItem()
}

/**
 * 거래 유형
 */
enum class TradeType(val displayName: String) {
    BUY("매수"),
    SELL("매도")
}

/**
 * 주문 유형
 */
enum class OrderType(val displayName: String) {
    LOC("LOC"),
    LIMIT("지정가"),
    MOC("MOC")
}

/**
 * 거래 상태
 */
enum class TradeStatus(val displayName: String) {
    ORDERED("주문"),
    FILLED("체결"),
    CANCELLED("취소")
}
