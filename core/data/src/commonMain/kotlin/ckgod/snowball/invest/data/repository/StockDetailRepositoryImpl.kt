package ckgod.snowball.invest.data.repository

import ckgod.snowball.invest.data.mapper.toDomain
import ckgod.snowball.invest.data.remote.dto.StockDetailResponse
import ckgod.snowball.invest.domain.model.StockDetailState
import ckgod.snowball.invest.domain.repository.StockDetailRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class StockDetailRepositoryImpl(
    private val httpClient: HttpClient
) : StockDetailRepository {

    override suspend fun getStockDetail(ticker: String): Result<StockDetailState> {
        return try {
            val response = httpClient.get("sb/stock/detail") {
                url {
                    parameters.append("ticker", ticker)
                }
            }.body<StockDetailResponse>()

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
