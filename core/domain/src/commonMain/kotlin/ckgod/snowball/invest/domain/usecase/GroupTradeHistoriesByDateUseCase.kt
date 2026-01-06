package ckgod.snowball.invest.domain.usecase

import com.ckgod.snowball.model.TradeHistoryResponse

class GroupTradeHistoriesByDateUseCase {
    operator fun invoke(histories: List<TradeHistoryResponse>): Map<String, List<TradeHistoryResponse>> {
        return histories.groupBy { history ->
            // "2025-12-22T18:09:07" -> "2025.12.22"
            history.orderTime.split("T")[0].replace("-", ".")
        }
    }
}
