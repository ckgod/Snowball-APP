package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.BacktestRequest
import com.ckgod.snowball.model.BacktestResponse
import com.ckgod.snowball.model.StockPriceHistoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class BacktestRepositoryImpl(
    private val httpClient: HttpClient
) : BacktestRepository {

    override suspend fun getStockPriceHistory(
        ticker: String,
        startDate: String,
        endDate: String
    ): StockPriceHistoryResponse {
        return httpClient.get("/sb/stock/history") {
            parameter("ticker", ticker)
            parameter("startDate", startDate)
            parameter("endDate", endDate)
        }.body<StockPriceHistoryResponse>()
    }

    override suspend fun runBacktest(request: BacktestRequest): BacktestResponse {
        return httpClient.post("/sb/backtest") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<BacktestResponse>()
    }
}
