package ckgod.snowball.invest.data.repository

import ckgod.snowball.invest.domain.model.StockDetailState
import ckgod.snowball.invest.domain.repository.StockDetailRepository
import ckgod.snowball.invest.util.CurrencyManager
import com.ckgod.snowball.model.StockDetailResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class StockDetailRepositoryImpl(
    private val httpClient: HttpClient,
    private val currencyManager: CurrencyManager
) : StockDetailRepository {

    override suspend fun getStockDetail(ticker: String): Result<StockDetailState> {
        return try {
            val response = httpClient.get("sb/stock/detail") {
                url {
                    parameters.append("ticker", ticker)
                }
            }.body<StockDetailResponse>()

            Result.success(StockDetailState.from(response, currencyManager))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
