package ckgod.snowball.invest.domain.model

import com.ckgod.snowball.model.InvestmentStatusResponse
import com.ckgod.snowball.model.TradePhase
import kotlin.math.abs

/**
 * 투자 종목 요약 정보
 * 메인 화면의 종목 카드에 표시될 데이터
 */
data class StockSummary(
    val ticker: String = "",                          // 티커명 (예: TQQQ)
    val fullName: String = "",                        // 종목 풀네임
    val currentPrice: Double = 0.0,                   // 현재가
    val dailyChangeRate: Double = 0.0,                // 전일 대비 등락률 (%)

    // 전략 상태
    val tValue: Double = 0.0,                         // 현재 T값
    val totalDivision: Int = 40,                      // 전체 분할 수 (예: 40)
    val starPercent: Double = 0.0,                    // 별 %
    val phase: TradePhase = TradePhase.FIRST_HALF,    // 현재 구간 (전반전, 후반전 등)

    // 내 계좌 상태
    val avgPrice: Double = 0.0,                       // 평균 단가
    val quantity: Int = 0,                            // 보유 수량
    val profitRate: Double = 0.0,                     // 수익률 (%)
    val profitAmount: String = "$0",                   // 평가 손익 금액
    val oneTimeAmount: Double = 0.0,                  // 1회 매수액
    val totalInvested: Double = 0.0,                  // 누적 투자 금액
    val capital: Double? = null,                      // 원금
    val nextSellStarPrice: Double? = null,            // 다음 LOC 매도가
    val nextSellTargetPrice: Double? = null,          // 다음 지정가 매도가
    val nextBuyStarPrice: Double? = null,             // 다음 LOC 매수가
    val realizedProfit: Double = 0.0
) {
    companion object {
        fun from(response: InvestmentStatusResponse): StockSummary {
            val profitAmount = when {
                response.profitRate > 0.0 -> "+$${response.profitAmount}"
                response.profitRate < 0.0 -> "-$${abs( response.profitAmount)}"
                else -> "$${response.profitAmount}"
            }
            return StockSummary(
                ticker = response.ticker,
                fullName = response.fullName ?: "",
                currentPrice = response.currentPrice,
                dailyChangeRate = response.dailyChangeRate,
                tValue = response.tValue,
                totalDivision = response.totalDivision,
                starPercent = response.starPercent,
                phase = response.phase,
                avgPrice = response.avgPrice,
                quantity = response.quantity,
                profitRate = response.profitRate,
                profitAmount = profitAmount,
                oneTimeAmount = response.oneTimeAmount,
                totalInvested = response.totalInvested,
                capital = response.capital,
                nextSellStarPrice = response.nextSellStarPrice,
                nextSellTargetPrice = response.nextSellTargetPrice,
                nextBuyStarPrice = response.nextBuyStarPrice,
                realizedProfit = response.realizedProfit
            )
        }
    }
}
