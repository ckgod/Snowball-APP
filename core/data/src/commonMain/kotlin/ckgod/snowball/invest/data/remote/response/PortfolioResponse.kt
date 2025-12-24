package ckgod.snowball.invest.data.remote.response

import ckgod.snowball.invest.data.remote.dto.StockDto
import ckgod.snowball.invest.domain.model.Portfolio
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    @SerialName("total")
    val totalCount: Int,
    @SerialName("statusList")
    val stocks: List<StockDto>
) {
    fun toDomain(): Portfolio {
        return Portfolio(
            totalCount = totalCount,
            stocks = stocks.map { it.toDomain() }
        )
    }
}