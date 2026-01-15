package ckgod.snowball.invest.domain.usecase

import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepository
import ckgod.snowball.invest.data.repository.PortfolioRepository
import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.data.result.asResult
import ckgod.snowball.invest.domain.state.InvestmentStatusState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetInvestmentStatusUseCase(
    private val currencyPreferencesRepository: CurrencyPreferencesRepository,
    private val portfolioRepository: PortfolioRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(refreshFlow: Flow<Unit>): Flow<Result<InvestmentStatusState>> {
        val apiDataFlow = refreshFlow.flatMapLatest {
            flow {
                emit(portfolioRepository.getPortfolioStatus())
            }
            .flowOn(Dispatchers.IO)
            .asResult()
        }

        val currencyFlow = currencyPreferencesRepository.currencyType

        return combine(apiDataFlow, currencyFlow) { result, currencyType ->
            when (result) {
                is Result.Success -> {
                    currencyPreferencesRepository.setExchangeRate(result.data.statusList.firstOrNull()?.exchangeRate ?: 0.0)

                    Result.Success(InvestmentStatusState(
                        data = result.data,
                        isLoading = false,
                        error = null,
                        isRefreshing = false,
                        currencyType = currencyType,
                        exchangeRate = currencyPreferencesRepository.exchangeRate.value
                    ))
                }
                is Result.Loading -> { Result.Loading }
                is Result.Error -> {
                    Result.Error(result.exception)
                }
            }
        }
    }
}