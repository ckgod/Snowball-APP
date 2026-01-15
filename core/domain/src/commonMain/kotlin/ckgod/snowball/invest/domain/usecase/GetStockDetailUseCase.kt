package ckgod.snowball.invest.domain.usecase

import ckgod.snowball.invest.data.repository.StockDetailRepository
import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.data.result.asResult
import ckgod.snowball.invest.domain.state.StockDetailState
import com.ckgod.snowball.model.InvestmentStatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class GetStockDetailUseCase(
    private val currencyInfoUseCase: GetCurrencyInfoUseCase,
    private val stockDetailRepository: StockDetailRepository
) {
    operator fun invoke(ticker: String): Flow<Result<StockDetailState>> {
        val apiFlow = flow {
            emit(stockDetailRepository.getStockDetail(ticker))
        }
        .flowOn(Dispatchers.IO)
        .asResult()

        val currencyFlow = currencyInfoUseCase()

        return combine(apiFlow, currencyFlow) { result, currency ->
            when (result) {
                is Result.Loading -> { Result.Loading }
                is Result.Error -> { Result.Error(result.exception) }
                is Result.Success -> {
                    Result.Success(StockDetailState(
                        stockDetail = result.data.status ?: InvestmentStatusResponse(),
                        historyItems = result.data.histories.groupBy { history ->
                            // "2025-12-22T18:09:07" -> "2025.12.22"
                            history.orderTime.split("T")[0].replace("-", ".")
                        },
                        isLoading = false,
                        error = null,
                        currencyType = currency.first,
                        exchangeRate = currency.second
                    ))
                }
            }
        }
    }
}