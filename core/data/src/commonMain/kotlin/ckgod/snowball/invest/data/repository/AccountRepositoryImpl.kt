package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.TotalAssetResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class AccountRepositoryImpl(
    private val httpClient: HttpClient
) : AccountRepository {

    override suspend fun getAccountStatus(): TotalAssetResponse {
        return httpClient.get("/sb/account/status").body<TotalAssetResponse>()
    }
}
