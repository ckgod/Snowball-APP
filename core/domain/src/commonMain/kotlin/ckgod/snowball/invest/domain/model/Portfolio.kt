package ckgod.snowball.invest.domain.model

import ckgod.snowball.invest.util.formatDecimal
import ckgod.snowball.invest.util.formatWithComma
import com.ckgod.snowball.model.HomeTabResponse
import kotlin.math.abs

data class Portfolio(
    val stocks: List<StockSummary>,
    val totalRealizedProfitUsd: Double = 0.0,
    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 0.0
) {
    val totalRealizedProfit: String
        get() {
            val prefix = when {
                totalRealizedProfitUsd > 0 -> "+"
                totalRealizedProfitUsd < 0 -> "-"
                else -> ""
            }
            return when (currencyType) {
                CurrencyType.USD -> "$prefix$${abs(totalRealizedProfitUsd).formatDecimal()}"
                CurrencyType.KRW -> "$prefix${abs(totalRealizedProfitUsd * exchangeRate).formatWithComma()}원"
            }
        }

    companion object {
        fun from(
            response: HomeTabResponse,
            currencyType: CurrencyType = CurrencyType.USD
        ): Portfolio {
            val totalProfit = response.statusList.sumOf { it.realizedProfit }
            val exchangeRate = response.statusList.firstOrNull()?.exchangeRate ?: 0.0

            return Portfolio(
                stocks = response.statusList.map {
                    StockSummary.from(it, currencyType, exchangeRate)
                },
                totalRealizedProfitUsd = totalProfit,
                currencyType = currencyType,
                exchangeRate = exchangeRate
            )
        }
    }
}
