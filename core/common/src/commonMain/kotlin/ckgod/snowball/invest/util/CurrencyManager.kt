package ckgod.snowball.invest.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyManager {
    private val _exchangeRate = MutableStateFlow(0.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate.asStateFlow()

    private val _currencyType = MutableStateFlow(CurrencyType.USD)
    val currencyType: StateFlow<CurrencyType> = _currencyType.asStateFlow()

    fun updateExchangeRate(rate: Double) {
        _exchangeRate.value = rate
    }

    fun toggleCurrency() {
        if (_currencyType.value == CurrencyType.USD) {
            _currencyType.value = CurrencyType.KRW
        } else {
            _currencyType.value = CurrencyType.USD
        }
    }

    fun setKrwMode(type: CurrencyType) {
        _currencyType.value = type
    }

    fun convertToKrw(usdAmount: Double): Double {
        return usdAmount * _exchangeRate.value
    }

    fun convertToUsd(krwAmount: Double): Double {
        return krwAmount / _exchangeRate.value
    }
}

enum class CurrencyType {
    USD, KRW
}
