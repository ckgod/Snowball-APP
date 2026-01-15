package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.CurrencyType
import kotlinx.coroutines.flow.StateFlow

interface CurrencyPreferencesRepository {
    val currencyType: StateFlow<CurrencyType>
    val exchangeRate: StateFlow<Double>
    fun setCurrencyType(type: CurrencyType)
    fun setExchangeRate(exchangeRate: Double)
}
