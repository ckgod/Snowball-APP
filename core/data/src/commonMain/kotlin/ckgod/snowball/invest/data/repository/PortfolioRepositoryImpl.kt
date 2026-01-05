package ckgod.snowball.invest.data.repository

import ckgod.snowball.invest.domain.model.Portfolio
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import com.ckgod.snowball.model.HomeTabResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PortfolioRepositoryImpl(
    private val httpClient: HttpClient
) : PortfolioRepository {

    override suspend fun getPortfolioStatus(): Result<Portfolio> {
        return try {
            val response = httpClient.get("/sb/home/status").body<HomeTabResponse>()

            Result.success(Portfolio.from(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
