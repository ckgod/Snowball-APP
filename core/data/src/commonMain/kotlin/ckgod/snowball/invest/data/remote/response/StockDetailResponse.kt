package ckgod.snowball.invest.data.remote.response

import ckgod.snowball.invest.data.remote.dto.StockDto
import ckgod.snowball.invest.data.remote.dto.TradeHistoryDto
import ckgod.snowball.invest.domain.model.StockDetailState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockDetailResponse(
    @SerialName("status")
    val status: StockDto,
    @SerialName("histories")
    val histories: List<TradeHistoryDto>
) {
    fun toDomain(): StockDetailState {
        val historyItems = histories
            .map { history ->
                history.toDomain()
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
}