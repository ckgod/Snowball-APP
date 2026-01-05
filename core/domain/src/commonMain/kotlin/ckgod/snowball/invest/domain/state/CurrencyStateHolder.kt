package ckgod.snowball.invest.domain.state

import ckgod.snowball.invest.domain.model.CurrencyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyStateHolder {
    private val _currencyType = MutableStateFlow(CurrencyType.USD)
    val currencyType: StateFlow<CurrencyType> = _currencyType.asStateFlow()

    fun setCurrencyType(type: CurrencyType) {
        _currencyType.value = type
    }
}
