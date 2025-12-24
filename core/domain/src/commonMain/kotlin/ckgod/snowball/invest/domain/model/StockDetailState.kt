package ckgod.snowball.invest.domain.model

data class StockDetailState(
    val stock: StockSummary = StockSummary(),
    val historyItems: Map<String, List<HistoryItem>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null
)