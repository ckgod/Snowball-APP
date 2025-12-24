package ckgod.snowball.invest.data.mapper

import ckgod.snowball.invest.data.remote.dto.StockDetailResponse
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.domain.model.StockDetailState

fun StockDetailResponse.toDomain(): StockDetailState {
    val historyItems = histories
        .map { history ->
            // "2025-12-22T18:09:07" or "2025-12-22T18:09"
            val parts = history.orderTime.split("T")
            val datePart = parts[0].replace("-", "") // "20251222"
            val timePart = parts[1].replace(":", "") // "180907" or "1809"

            val formattedTime = if (timePart.length == 4) {
                timePart + "00" // "180900"
            } else {
                timePart // "180907"
            }

            val dateTime = datePart + formattedTime // "20251222180907"

            val displayTime = "${formattedTime.substring(0, 2)}:${formattedTime.substring(2, 4)}"

            HistoryItem.Trade(
                dateTime = dateTime,
                displayTime = displayTime,
                type = history.orderSide,
                orderType = history.orderType,
                price = history.orderPrice,
                quantity = history.orderQuantity,
                status = history.status
            )
        }
        .groupBy { item ->
            val date = item.dateTime.take(8)
            "${date.substring(0, 4)}.${date.substring(4, 6)}.${date.substring(6, 8)}"
        }

    return StockDetailState(
        stock = status.toDomain(),
        historyItems = historyItems,
        isLoading = false,
        error = null
    )
}