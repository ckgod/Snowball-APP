package ckgod.snowball.invest.domain.model

/**
 * 전체 포트폴리오 정보
 */
data class Portfolio(
    val totalCount: Int,
    val stocks: List<StockSummary>        // 투자 종목 리스트
)
