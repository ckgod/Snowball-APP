package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.CurrencyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyPreferencesRepositoryImpl : CurrencyPreferencesRepository {
    private val _currencyType = MutableStateFlow(CurrencyType.USD)
    override val currencyType: StateFlow<CurrencyType> = _currencyType.asStateFlow()

    override fun setCurrencyType(type: CurrencyType) {
        _currencyType.value = type
    }
}
