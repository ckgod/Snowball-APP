package ckgod.snowball.invest.domain.model

/**
 * 투자 종목 요약 정보
 * 메인 화면의 종목 카드에 표시될 데이터
 */
data class StockSummary(
    val ticker: String,                    // 티커명 (예: TQQQ)
    val fullName: String,                  // 종목 풀네임
    val currentPrice: Double,              // 현재가
    val dailyChangeRate: Double,           // 전일 대비 등락률 (%)

    // 전략 상태
    val tValue: Double,                       // 현재 T값
    val totalDivision: Int,                // 전체 분할 수 (예: 40)
    val starPercent: Double,               // 별 %
    val phase: TradePhase,                 // 현재 구간 (전반전, 후반전 등)

    // 내 계좌 상태
    val avgPrice: Double,                  // 평균 단가
    val quantity: Int,                     // 보유 수량
    val profitRate: Double,                // 수익률 (%)
    val profitAmount: Double,              // 평가 손익 금액
    val oneTimeAmount: Double,             // 1회 매수액
    val totalInvested: Double,             // 누적 투자 금액
    val capital: Double? = null,            // 원금
    val nextSellStarPrice: Double? = null,
    val nextSellTargetPrice: Double? = null,
    val nextBuyStarPrice: Double? = null
)

enum class TradePhase(val displayName: String) {
    FIRST_HALF("전반전"),
    BACK_HALF("후반전"),
    QUARTER_MODE("쿼터모드"),
    EXHAUSTED("자금소진"),
    UNKNOWN("알 수 없음");

    companion object {
        /**
         * 서버에서 받은 문자열을 TradePhase enum으로 변환
         * @param displayName 서버에서 받은 문자열 (예: "전반전", "후반전")
         * @return 매칭되는 TradePhase, 없으면 FIRST_HALF 반환
         */
        fun fromDisplayName(displayName: String): TradePhase {
            return entries.find { it.displayName == displayName } ?: UNKNOWN
        }
    }
}
