package ckgod.snowball.invest.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

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

    /**
     * 일반 가격을 현재 통화 타입에 맞게 포맷팅
     * USD: $100.50
     * KRW: 135,000원
     */
    fun formatPrice(usdAmount: Double): String {
        return when (_currencyType.value) {
            CurrencyType.USD -> "$${usdAmount.formatDecimal()}"
            CurrencyType.KRW -> "${convertToKrw(usdAmount).formatDecimal(0)}원"
        }
    }

    fun formatProfit(usdAmount: Double): String {
        val prefix = when {
            usdAmount > 0 -> "+"
            usdAmount < 0 -> "-"
            else -> ""
        }
        return when (_currencyType.value) {
            CurrencyType.USD -> "$prefix$${abs(usdAmount).formatDecimal()}"
            CurrencyType.KRW -> "$prefix${convertToKrw(usdAmount).formatDecimal(0)}원"
        }
    }
}
