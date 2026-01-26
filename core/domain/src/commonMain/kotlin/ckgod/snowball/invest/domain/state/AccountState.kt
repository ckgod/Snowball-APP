package ckgod.snowball.invest.domain.state

import com.ckgod.snowball.model.TotalAssetResponse
import com.ckgod.snowball.model.CurrencyType

data class AccountState(
    val data: TotalAssetResponse? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 1.0
)