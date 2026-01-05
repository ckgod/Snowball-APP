package ckgod.snowball.invest.feature.home.model

import ckgod.snowball.invest.domain.model.CurrencyType
import ckgod.snowball.invest.domain.model.Portfolio

/**
 * 홈 화면의 UI 상태
 */
data class HomeState(
    val isLoading: Boolean = false,
    val portfolio: Portfolio? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val currencyType: CurrencyType = CurrencyType.USD
)
