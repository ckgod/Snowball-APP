package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.AccountStatusResponse

interface AccountRepository {
    suspend fun getAccountStatus(): AccountStatusResponse
}
