package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.CurrencyType
import kotlinx.coroutines.flow.StateFlow

interface CurrencyPreferencesRepository {
    val currencyType: StateFlow<CurrencyType>
    fun setCurrencyType(type: CurrencyType)
}
