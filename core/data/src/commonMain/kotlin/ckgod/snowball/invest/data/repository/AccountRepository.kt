package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.TotalAssetResponse

interface AccountRepository {
    suspend fun getAccountStatus(): TotalAssetResponse
}
