package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.StockDetailResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class StockDetailRepositoryImpl(
    private val httpClient: HttpClient
) : StockDetailRepository {

    override suspend fun getStockDetail(ticker: String): Result<StockDetailResponse> {
        return try {
            val response = httpClient.get("sb/stock/detail") {
                url {
                    parameters.append("ticker", ticker)
                }
            }.body<StockDetailResponse>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
