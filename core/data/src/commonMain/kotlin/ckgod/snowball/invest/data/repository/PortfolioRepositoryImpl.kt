package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.HomeTabResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PortfolioRepositoryImpl(
    private val httpClient: HttpClient
) : PortfolioRepository {

    override suspend fun getPortfolioStatus(): Result<HomeTabResponse> {
        return try {
            val response = httpClient.get("/sb/home/status").body<HomeTabResponse>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
