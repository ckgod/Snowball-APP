package ckgod.snowball.invest.domain.model

data class StockDetailState(
    val ticker: String = "",
    val currentPrice: Double = 0.0,
    val profitRate: Double = 0.0,
    val profitAmount: Double = 0.0,
    val quantity: Int = 0,
    val avgPrice: Double = 0.0,
    val tValue: Double = 0.0,
    val division: Int = 40,
    val oneTimeAmount: Double = 0.0,
    val nextBuyStarPrice: Double = 0.0,
    val nextSellStarPrice: Double = 0.0,
    val nextSellTargetPrice: Double = 0.0,
    val historyItems: Map<String, List<HistoryItem>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null
)