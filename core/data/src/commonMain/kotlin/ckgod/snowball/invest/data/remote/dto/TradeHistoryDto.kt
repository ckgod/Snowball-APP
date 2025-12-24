package ckgod.snowball.invest.data.remote.dto

import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.domain.model.OrderType
import ckgod.snowball.invest.domain.model.TradeStatus
import ckgod.snowball.invest.domain.model.TradeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TradeHistoryDto(
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
) {
    fun toDomain(): HistoryItem.Trade {
        // "2025-12-22T18:09:07" or "2025-12-22T18:09"
        val parts = orderTime.split("T")
        val datePart = parts[0].replace("-", "") // "20251222"
        val timePart = parts[1].replace(":", "") // "180907" or "1809"

        val formattedTime = if (timePart.length == 4) {
            timePart + "00" // "180900"
        } else {
            timePart // "180907"
        }

        val dateTime = datePart + formattedTime // "20251222180907"

        val displayTime = "${formattedTime.substring(0, 2)}:${formattedTime.substring(2, 4)}"

        return HistoryItem.Trade(
            dateTime = dateTime,
            displayTime = displayTime,
            orderNo = orderNo,
            type = orderSide,
            orderType = orderType,
            price = orderPrice,
            quantity = orderQuantity,
            status = status
        )
    }
}