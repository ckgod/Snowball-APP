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
    val phase: StockPhase,                 // 현재 구간 (전반전, 후반전 등)

    // 내 계좌 상태
    val avgPrice: Double,                  // 평균 단가
    val quantity: Int,                     // 보유 수량
    val profitRate: Double,                // 수익률 (%)
    val profitAmount: Double,              // 평가 손익 금액
    val oneTimeAmount: Double,             // 1회 매수액
    val totalInvested: Double,             // 누적 투자 금액
)

/**
 * 전략 진행 구간
 */
enum class StockPhase(val displayName: String) {
    FIRST_HALF("전반전"),
    BACK_HALF("후반전"),
    QUARTER_MODE("쿼터모드"),
    EXHAUSTED("자금소진")
}
