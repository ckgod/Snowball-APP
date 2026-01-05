package ckgod.snowball.invest.domain.model

import com.ckgod.snowball.model.StockDetailResponse

data class StockDetailState(
    val stock: StockSummary = StockSummary(),
    val historyItems: Map<String, List<HistoryItem>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null,
    val currencyType: CurrencyType = CurrencyType.USD
) {
    companion object {
        fun from(
            response: StockDetailResponse,
            currencyType: CurrencyType = CurrencyType.USD
        ): StockDetailState {
            val historyItems = response.histories
                .map { history -> HistoryItem.from(history) }
                .groupBy { item ->
                    val date = item.dateTime.take(8)
                    "${date.substring(0, 4)}.${date.substring(4, 6)}.${date.substring(6, 8)}"
                }

            val exchangeRate = response.status?.exchangeRate ?: 0.0

            return StockDetailState(
                stock = response.status?.let {
                    StockSummary.from(it, currencyType, exchangeRate)
                } ?: StockSummary(),
                historyItems = historyItems,
                isLoading = false,
                error = null,
                currencyType = currencyType
            )
        }
    }
}