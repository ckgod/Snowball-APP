package ckgod.snowball.invest.domain.model

/**
 * 전체 포트폴리오 정보
 */
data class Portfolio(
    val totalRealizedProfit: Double,      // 모든 종목의 총 실현 손익
    val stocks: List<StockSummary>        // 투자 종목 리스트
)
