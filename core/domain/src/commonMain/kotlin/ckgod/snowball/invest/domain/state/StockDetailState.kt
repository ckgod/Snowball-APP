package ckgod.snowball.invest.domain.state

import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.InvestmentStatusResponse
import com.ckgod.snowball.model.OpenOrderResponse
import com.ckgod.snowball.model.TradeHistoryResponse

data class StockDetailState(
    val stockDetail: InvestmentStatusResponse = InvestmentStatusResponse(),
    val historyItems: Map<String, List<TradeHistoryResponse>> = emptyMap(), // yyyyMMdd 형식
    val isLoading: Boolean = false,
    val error: String? = null,
    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 0.0,
    // 미체결 주문 (정정·취소 대상)
    val openOrders: List<OpenOrderResponse> = emptyList(),
    val isOrderActionRunning: Boolean = false,
    /** 정정·취소 결과 안내. 화면이 한 번 보여 주고 지운다. */
    val orderMessage: String? = null
)