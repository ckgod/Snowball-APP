package ckgod.snowball.invest.domain.model

import ckgod.snowball.invest.util.CurrencyManager
import com.ckgod.snowball.model.HomeTabResponse

data class Portfolio(
    val stocks: List<StockSummary>,
    val totalRealizedProfit: String = "$0"
) {
    companion object {
        fun from(
            response: HomeTabResponse,
            currencyManager: CurrencyManager
        ): Portfolio {
            val totalProfit = response.statusList.sumOf { it.realizedProfit }

            return Portfolio(
                stocks = response.statusList.map {
                    StockSummary.from(it, currencyManager)
                },
                totalRealizedProfit = currencyManager.formatProfit(totalProfit)
            )
        }
    }
}
