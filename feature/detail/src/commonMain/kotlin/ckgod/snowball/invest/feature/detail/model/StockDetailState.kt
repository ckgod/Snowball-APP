package ckgod.snowball.invest.feature.detail.model

import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.InvestmentStatusResponse
import com.ckgod.snowball.model.TradeHistoryResponse

data class StockDetailState(
    val stockDetail: InvestmentStatusResponse = InvestmentStatusResponse(),
    val historyItems: Map<String, List<TradeHistoryResponse>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null,
    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 0.0
)