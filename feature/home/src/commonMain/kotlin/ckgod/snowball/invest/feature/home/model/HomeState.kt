package ckgod.snowball.invest.feature.home.model

import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.HomeTabResponse

/**
 * 홈 화면의 UI 상태
 */
data class HomeState(
    val data: HomeTabResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val currencyType: CurrencyType = CurrencyType.USD,
    val exchangeRate: Double = 0.0
)
