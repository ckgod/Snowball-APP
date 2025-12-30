package ckgod.snowball.invest.domain.model

import com.ckgod.snowball.model.HomeTabResponse

data class Portfolio(
    val stocks: List<StockSummary>
) {
    companion object {
        fun from(response: HomeTabResponse): Portfolio {
            return Portfolio(
                response.statusList.map { status ->
                    StockSummary.from(status)
                }
            )
        }
    }
}
