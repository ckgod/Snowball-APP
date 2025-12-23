package ckgod.snowball.invest.feature.detail

import com.arkivanov.decompose.ComponentContext
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.domain.model.OrderType
import ckgod.snowball.invest.domain.model.TradeStatus
import ckgod.snowball.invest.domain.model.TradeType
import ckgod.snowball.invest.feature.detail.model.StockDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface StockDetailComponent {
    val state: StateFlow<StockDetailState>

    fun onBackClick()
    fun onRefresh()
}

class DefaultStockDetailComponent(
    componentContext: ComponentContext,
    private val ticker: String,
    private val onBack: () -> Unit
) : StockDetailComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(
        // TODO: 실제로는 ViewModel/UseCase에서 데이터를 가져와야 함
        StockDetailState(
            ticker = ticker,
            currentPrice = 68.25,
            profitRate = 12.5,
            profitAmount = 850.0,
            quantity = 120,
            avgPrice = 60.50,
            tValue = 15,
            division = 40,
            oneTimeAmount = 1005.2,
            nextBuyPrice = 65.0,
            nextSellPrice = 72.0,
            historyItems = getMockHistoryItems()
        )
    )

    override val state: StateFlow<StockDetailState> = _state.asStateFlow()

    override fun onBackClick() {
        onBack()
    }

    override fun onRefresh() {
        // TODO: Implement refresh logic
    }

    private fun getMockHistoryItems(): Map<String, List<HistoryItem>> {
        return mapOf(
            "20241219" to listOf(
                HistoryItem.Trade(
                    dateTime = "20241219153000",
                    type = TradeType.BUY,
                    orderType = OrderType.LOC,
                    price = 68.0,
                    quantity = 5,
                    status = TradeStatus.FILLED
                ),
                HistoryItem.Trade(
                    dateTime = "20241219101500",
                    type = TradeType.BUY,
                    orderType = OrderType.LIMIT,
                    price = 67.5,
                    quantity = 3,
                    status = TradeStatus.ORDERED
                )
            ),
            "20241218" to listOf(
                HistoryItem.Sync(
                    dateTime = "20241218180000",
                    profit = 150.5,
                    tValueUpdate = "14 -> 15"
                ),
                HistoryItem.Trade(
                    dateTime = "20241218142000",
                    type = TradeType.SELL,
                    orderType = OrderType.LOC,
                    price = 70.0,
                    quantity = 2,
                    status = TradeStatus.FILLED
                )
            )
        )
    }
}
