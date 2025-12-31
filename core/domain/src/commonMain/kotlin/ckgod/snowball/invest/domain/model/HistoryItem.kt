package ckgod.snowball.invest.domain.model

import com.ckgod.snowball.model.OrderSide
import com.ckgod.snowball.model.OrderType
import com.ckgod.snowball.model.TradeHistoryResponse
import com.ckgod.snowball.model.TradeStatus

data class HistoryItem(
    val dateTime: String,
    val displayTime: String,
    val orderNo: String,
    val orderSide: OrderSide,
    val orderType: OrderType,
    val price: Double,
    val quantity: Int,
    val status: TradeStatus,
    val crashRate: String? = null
) {
    companion object {
        fun from(response: TradeHistoryResponse): HistoryItem {
            // "2025-12-22T18:09:07" or "2025-12-22T18:09"
            val parts = response.orderTime.split("T")
            val datePart = parts[0].replace("-", "") // "20251222"
            val timePart = parts[1].replace(":", "") // "180907" or "1809"

            val formattedTime = if (timePart.length == 4) {
                timePart + "00" // "180900"
            } else {
                timePart // "180907"
            }

            val dateTime = datePart + formattedTime // "20251222180907"

            val displayTime = "${formattedTime.substring(0, 2)}:${formattedTime.substring(2, 4)}"

            return HistoryItem(
                dateTime = dateTime,
                displayTime = displayTime,
                orderNo = response.orderNo,
                orderSide = response.orderSide,
                orderType = response.orderType,
                price = response.orderPrice,
                quantity = response.orderQuantity,
                status = response.tradeStatus,
                crashRate = response.crashRate?.toString()
            )
        }
    }
}
