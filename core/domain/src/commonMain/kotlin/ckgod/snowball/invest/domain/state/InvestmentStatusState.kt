package ckgod.snowball.invest.domain.state

import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.HomeTabResponse

data class InvestmentStatusState(
    val data: HomeTabResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 0.0
)