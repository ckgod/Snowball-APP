package ckgod.snowball.invest.domain.model

import com.ckgod.snowball.model.StockDetailResponse

data class StockDetailState(
    val stock: StockSummary = StockSummary(),
    val historyItems: Map<String, List<HistoryItem>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        fun from(response: StockDetailResponse): StockDetailState {
            val historyItems = response.histories
                .map { history ->
                    HistoryItem.from(history)
                }
                .groupBy { item ->
                    val date = item.dateTime.take(8)
                    "${date.substring(0, 4)}.${date.substring(4, 6)}.${date.substring(6, 8)}"
                }

            return StockDetailState(
                stock = response.status?.let { StockSummary.from(it) } ?: StockSummary(),
                historyItems = historyItems,
                isLoading = false,
                error = null
            )
        }
    }
}