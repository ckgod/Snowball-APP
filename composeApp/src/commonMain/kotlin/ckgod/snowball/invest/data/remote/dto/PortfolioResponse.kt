package ckgod.snowball.invest.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    @SerialName("total")
    val totalCount: Int,
    @SerialName("statusList")
    val stocks: List<StockDto>
)

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
    val exchangeRate: Double
)
