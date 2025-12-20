package ckgod.snowball.invest.feature.detail.model

import ckgod.snowball.invest.domain.model.HistoryItem

/**
 * 종목 상세 화면 상태
 */
data class StockDetailState(
    val ticker: String = "",
    val currentPrice: Double = 0.0,
    val profitRate: Double = 0.0,
    val profitAmount: Double = 0.0,
    val quantity: Int = 0,
    val avgPrice: Double = 0.0,
    val tValue: Int = 0,
    val division: Int = 40,
    val oneTimeAmount: Double = 0.0,
    val nextBuyPrice: Double = 0.0,
    val nextSellPrice: Double = 0.0,
    val historyItems: Map<String, List<HistoryItem>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null
)
