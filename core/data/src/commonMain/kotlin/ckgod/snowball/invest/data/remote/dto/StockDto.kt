package ckgod.snowball.invest.data.remote.dto

import ckgod.snowball.invest.domain.model.StockSummary
import ckgod.snowball.invest.domain.model.TradePhase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockDto(
    @SerialName("ticker")
    val ticker: String,
    @SerialName("fullName")
    val fullName: String?,
    @SerialName("currentPrice")
    val currentPrice: Double,
    @SerialName("dailyChangeRate")
    val dailyChangeRate: Double,
    @SerialName("tValue")
    val tValue: Double,
    @SerialName("totalDivision")
    val totalDivision: Int,
    @SerialName("starPercent")
    val starPercent: Double,
    @SerialName("phase")
    val phase: String,  // "전반전", "후반전", "쿼터모드", "자금소진"
    @SerialName("avgPrice")
    val avgPrice: Double,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("profitRate")
    val profitRate: Double,
    @SerialName("profitAmount")
    val profitAmount: Double,
    @SerialName("oneTimeAmount")
    val oneTimeAmount: Double,
    @SerialName("totalInvested")
    val totalInvested: Double,
    @SerialName("exchangeRate")
    val exchangeRate: Double,
    @SerialName("capital")
    val capital: Double? = null,
    @SerialName("nextSellStarPrice")
    val nextSellStarPrice: Double? = null,
    @SerialName("nextSellTargetPrice")
    val nextSellTargetPrice: Double? = null,
    @SerialName("nextBuyStarPrice")
    val nextBuyStarPrice: Double? = null,
) {
    fun toDomain(): StockSummary {
        return StockSummary(
            ticker = ticker,
            fullName = fullName ?: ticker,
            currentPrice = currentPrice,
            dailyChangeRate = dailyChangeRate,
            tValue = tValue,
            totalDivision = totalDivision,
            starPercent = starPercent,
            phase = TradePhase.fromDisplayName(phase),
            avgPrice = avgPrice,
            quantity = quantity,
            profitRate = profitRate,
            profitAmount = profitAmount,
            oneTimeAmount = oneTimeAmount,
            totalInvested = totalInvested,
            capital = capital,
            nextSellStarPrice = nextSellStarPrice,
            nextSellTargetPrice = nextSellTargetPrice,
            nextBuyStarPrice = nextBuyStarPrice,
        )
    }
}
