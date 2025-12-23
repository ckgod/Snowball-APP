package ckgod.snowball.invest.data.repository

import ckgod.snowball.invest.data.mapper.toDomain
import ckgod.snowball.invest.data.remote.dto.PortfolioResponse
import ckgod.snowball.invest.domain.model.Portfolio
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PortfolioRepositoryImpl(
    private val httpClient: HttpClient
) : PortfolioRepository {

    override suspend fun getPortfolioStatus(): Result<Portfolio> {
        return try {
            val response = httpClient.get("/ckapi/v1/main/status").body<PortfolioResponse>()

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
