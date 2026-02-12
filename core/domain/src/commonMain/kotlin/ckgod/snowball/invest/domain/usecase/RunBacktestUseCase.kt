package ckgod.snowball.invest.domain.usecase

import ckgod.snowball.invest.data.repository.BacktestRepository
import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.data.result.asResult
import com.ckgod.snowball.model.BacktestRequest
import com.ckgod.snowball.model.BacktestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RunBacktestUseCase(
    private val backtestRepository: BacktestRepository
) {
    operator fun invoke(request: BacktestRequest): Flow<Result<BacktestResponse>> {
        return flow {
            emit(backtestRepository.runBacktest(request))
        }
            .flowOn(Dispatchers.IO)
            .asResult()
    }
}
