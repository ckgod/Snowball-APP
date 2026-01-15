package ckgod.snowball.invest.domain.usecase

import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepository
import com.ckgod.snowball.model.CurrencyType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetCurrencyInfoUseCase(
    private val currencyPreferencesRepository: CurrencyPreferencesRepository
) {
    operator fun invoke(): Flow<Pair<CurrencyType, Double>> =
        combine(
            currencyPreferencesRepository.currencyType,
            currencyPreferencesRepository.exchangeRate
        ) { currencyType, exchangeRate ->
            currencyType to exchangeRate
        }
}