package ckgod.snowball.invest.data.remote.dto

import ckgod.snowball.invest.domain.model.OrderType
import ckgod.snowball.invest.domain.model.TradeStatus
import ckgod.snowball.invest.domain.model.TradeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockDetailResponse(
    @SerialName("status")
    val status: StockDto,
    @SerialName("histories")
    val histories: List<TradeHistory>
)

@Serializable
data class TradeHistory(
    @SerialName("ticker")
    val ticker: String,
    @SerialName("orderNo")
    val orderNo: String,
    @SerialName("orderSide")
    val orderSide: TradeType,
    @SerialName("orderType")
    val orderType: OrderType,
    @SerialName("orderPrice")
    val orderPrice: Double,
    @SerialName("orderQuantity")
    val orderQuantity: Int,
    @SerialName("orderTime")
    val orderTime: String,
    @SerialName("status")
    val status: TradeStatus,
    @SerialName("filledQuantity")
    val filledQuantity: Int?,
    @SerialName("filledPrice")
    val filledPrice: String?,
    @SerialName("filledTime")
    val filledTime: String?,
    @SerialName("tValue")
    val tValue: Double,
)
