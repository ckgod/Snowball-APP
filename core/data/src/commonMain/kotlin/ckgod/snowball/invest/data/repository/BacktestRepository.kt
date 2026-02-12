package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.BacktestRequest
import com.ckgod.snowball.model.BacktestResponse
import com.ckgod.snowball.model.StockPriceHistoryResponse

interface BacktestRepository {
    suspend fun getStockPriceHistory(ticker: String, startDate: String, endDate: String): StockPriceHistoryResponse
    suspend fun runBacktest(request: BacktestRequest): BacktestResponse
}
