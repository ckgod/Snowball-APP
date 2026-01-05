package ckgod.snowball.invest.domain.model

import ckgod.snowball.invest.util.formatDecimal
import ckgod.snowball.invest.util.formatWithComma
import com.ckgod.snowball.model.InvestmentStatusResponse
import com.ckgod.snowball.model.TradePhase
import kotlin.math.abs

data class StockSummary(
    val ticker: String = "",                          // 티커명 (예: TQQQ)
    val fullName: String = "",                        // 종목 풀네임
    val currentPriceUsd: Double = 0.0,                // 현재가 (USD)
    val dailyChangeRate: Double = 0.0,                // 전일 대비 등락률 (%)

    val tValue: Double = 0.0,                         // 현재 T값
    val totalDivision: Int = 40,                      // 전체 분할 수 (예: 40)
    val starPercent: Double = 0.0,                    // 별 %
    val phase: TradePhase = TradePhase.FIRST_HALF,    // 현재 구간 (전반전, 후반전 등)

    val avgPriceUsd: Double = 0.0,                    // 평균 단가 (USD)
    val quantity: Int = 0,                            // 보유 수량
    val profitRate: Double = 0.0,                     // 수익률 (%)
    val profitAmountUsd: Double = 0.0,                // 평가 손익 금액 (USD)
    val oneTimeAmountUsd: Double = 0.0,               // 1회 매수액 (USD)
    val totalInvestedUsd: Double = 0.0,               // 누적 투자 금액 (USD)
    val capitalUsd: Double = 0.0,                     // 원금 (USD)
    val nextSellStarPriceUsd: Double? = null,         // 다음 LOC 매도가 (USD)
    val nextSellTargetPriceUsd: Double? = null,       // 다음 지정가 매도가 (USD)
    val nextBuyStarPriceUsd: Double? = null,          // 다음 LOC 매수가 (USD)
    val realizedProfitUsd: Double = 0.0,              // 실현 손익 (USD)

    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 0.0
) {
    val currentPrice: String
        get() = formatPrice(currentPriceUsd)

    val avgPrice: String
        get() = formatPrice(avgPriceUsd)

    val profitAmount: String
        get() = formatProfit(profitAmountUsd)

    val oneTimeAmount: String
        get() = formatPrice(oneTimeAmountUsd)

    val totalInvested: String
        get() = formatPrice(totalInvestedUsd)

    val capital: String
        get() = formatPrice(capitalUsd)

    val nextSellStarPrice: String?
        get() = nextSellStarPriceUsd?.let { formatPrice(it) }

    val nextSellTargetPrice: String?
        get() = nextSellTargetPriceUsd?.let { formatPrice(it) }

    val nextBuyStarPrice: String?
        get() = nextBuyStarPriceUsd?.let { formatPrice(it) }

    val realizedProfit: String
        get() = formatProfit(realizedProfitUsd)

    private fun formatPrice(usdAmount: Double): String {
        return when (currencyType) {
            CurrencyType.USD -> "$${usdAmount.formatDecimal()}"
            CurrencyType.KRW -> "${(usdAmount * exchangeRate).formatWithComma()}원"
        }
    }

    private fun formatProfit(usdAmount: Double): String {
        val prefix = when {
            usdAmount > 0 -> "+"
            usdAmount < 0 -> "-"
            else -> ""
        }
        return when (currencyType) {
            CurrencyType.USD -> "$prefix$${abs(usdAmount).formatDecimal()}"
            CurrencyType.KRW -> "$prefix${abs(usdAmount * exchangeRate).formatWithComma()}원"
        }
    }

    companion object {
        fun from(
            response: InvestmentStatusResponse,
            currencyType: CurrencyType = CurrencyType.USD,
            exchangeRate: Double = 0.0
        ): StockSummary {
            return StockSummary(
                ticker = response.ticker,
                fullName = response.fullName ?: "",
                currentPriceUsd = response.currentPrice,
                dailyChangeRate = response.dailyChangeRate,
                tValue = response.tValue,
                totalDivision = response.totalDivision,
                starPercent = response.starPercent,
                phase = response.phase,
                avgPriceUsd = response.avgPrice,
                quantity = response.quantity,
                profitRate = response.profitRate,
                profitAmountUsd = response.profitAmount,
                oneTimeAmountUsd = response.oneTimeAmount,
                totalInvestedUsd = response.totalInvested,
                capitalUsd = response.capital,
                nextSellStarPriceUsd = response.nextSellStarPrice,
                nextSellTargetPriceUsd = response.nextSellTargetPrice,
                nextBuyStarPriceUsd = response.nextBuyStarPrice,
                realizedProfitUsd = response.realizedProfit,
                currencyType = currencyType,
                exchangeRate = exchangeRate
            )
        }
    }
}
