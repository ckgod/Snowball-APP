package ckgod.snowball.invest.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.domain.model.OrderType
import ckgod.snowball.invest.domain.model.TradeStatus
import ckgod.snowball.invest.domain.model.TradeType
import ckgod.snowball.invest.feature.detail.model.StockDetailEvent
import ckgod.snowball.invest.feature.detail.model.StockDetailState
import ckgod.snowball.invest.feature.main.LocalMainNavigator

/**
 * 종목 상세 화면 Holder (Voyager Screen)
 */
data class StockDetailScreenHolder(
    val ticker: String
) : Screen {

    @Composable
    override fun Content() {
        val mainNavigator = LocalMainNavigator.current

        // TODO: 실제로는 ScreenModel에서 데이터를 가져와야 함
        // 현재는 Mock 데이터 사용
        val mockState = remember {
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
        }

        StockDetailScreen(
            state = mockState,
            onEvent = { event ->
                when (event) {
                    is StockDetailEvent.BackClick -> mainNavigator.pop()
                    is StockDetailEvent.Refresh -> {
                        // TODO: Refresh 로직
                    }
                }
            }
        )
    }

    private fun getMockHistoryItems(): Map<String, List<HistoryItem>> {
        return mapOf(
            "20241219" to listOf(
                HistoryItem.Trade(
                    dateTime = "20241219153000", // yyyyMMddHHmmss
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
