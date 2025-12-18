package ckgod.snowball.invest.domain.model

/**
 * 거래 히스토리 아이템
 * 상세 화면의 타임라인에 표시될 데이터
 */
sealed class HistoryItem {
    abstract val dateTime: String  // ISO 8601 형식 문자열 (예: "2023-10-27T07:00:00")

    /**
     * 주문 내역
     */
    data class OrderHistory(
        override val dateTime: String,
        val type: OrderType,              // 주문 유형
        val price: Double,                // 주문 가격
        val quantity: Int,                // 주문 수량
        val status: OrderStatus           // 주문 상태
    ) : HistoryItem()

    /**
     * 체결 내역
     */
    data class ExecutionHistory(
        override val dateTime: String,
        val side: ExecutionSide,          // 매수/매도
        val price: Double,                // 체결 가격
        val quantity: Int,                // 체결 수량
        val totalAmount: Double           // 총 체결 금액
    ) : HistoryItem()

    /**
     * 정산 내역
     */
    data class SyncHistory(
        override val dateTime: String,
        val realizedProfit: Double,       // 실현 수익
        val tValueChange: String          // T값 변화 (예: "11 → 12")
    ) : HistoryItem()
}

/**
 * 주문 유형
 */
enum class OrderType(val displayName: String) {
    BUY_LOC("매수(LOC)"),
    BUY_LIMIT("매수(지정가)"),
    SELL_LOC("매도(LOC)"),
    SELL_LIMIT("매도(지정가)")
}

/**
 * 주문 상태
 */
enum class OrderStatus(val displayName: String) {
    ACCEPTED("접수"),
    FILLED("체결"),
    UNFILLED("미체결"),
    CANCELLED("취소")
}

/**
 * 체결 방향
 */
enum class ExecutionSide(val displayName: String) {
    BUY("매수"),
    SELL("매도")
}
