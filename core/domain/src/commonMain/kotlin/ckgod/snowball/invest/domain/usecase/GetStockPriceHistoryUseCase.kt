package ckgod.snowball.invest.domain.usecase

import ckgod.snowball.invest.data.repository.BacktestRepository
import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.data.result.asResult
import com.ckgod.snowball.model.StockPriceHistoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetStockPriceHistoryUseCase(
    private val backtestRepository: BacktestRepository
) {
    operator fun invoke(
        ticker: String,
        startDate: String = "2010-01-01",
        endDate: String = "2030-12-31"
    ): Flow<Result<StockPriceHistoryResponse>> {
        return flow {
            emit(backtestRepository.getStockPriceHistory(ticker, startDate, endDate))
        }
            .flowOn(Dispatchers.IO)
            .asResult()
    }
}
