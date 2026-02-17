package ckgod.snowball.invest.feature.backtest

import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.domain.state.BacktestState
import ckgod.snowball.invest.domain.usecase.GetStockPriceHistoryUseCase
import ckgod.snowball.invest.domain.usecase.RunBacktestUseCase
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.ckgod.snowball.model.BacktestRequest
import com.ckgod.snowball.model.BacktestResponse
import com.ckgod.snowball.model.StarMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

interface BacktestComponent {
    val state: StateFlow<BacktestState>
    fun onTickerSelected(ticker: String)
    fun onStartDateChanged(date: String)
    fun onEndDateChanged(date: String)
    fun onInitialCapitalChanged(value: Double)
    fun onDivisionChanged(value: Int)
    fun onTargetRateChanged(value: Double)
    fun onStarModeChanged(mode: StarMode)
    fun runBacktest()
    fun clearResult()
}

class DefaultBacktestComponent(
    componentContext: ComponentContext,
    private val getStockPriceHistoryUseCase: GetStockPriceHistoryUseCase,
    private val runBacktestUseCase: RunBacktestUseCase,
    private val onBacktestCompleted: (BacktestResponse) -> Unit
) : BacktestComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(BacktestState())
    override val state: StateFlow<BacktestState> = _state.asStateFlow()

    init {
        lifecycle.doOnDestroy {
            scope.cancel()
        }

        loadPriceHistories()
    }

    private fun loadPriceHistories() {
        scope.launch {
            getStockPriceHistoryUseCase("TQQQ").collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Loading -> currentState.copy(isLoading = true)
                        is Result.Success -> currentState.copy(
                            tqqqPriceHistory = result.data,
                            isLoading = currentState.soxlPriceHistory == null,
                            error = null
                        )
                        is Result.Error -> currentState.copy(
                            isLoading = false,
                            error = result.exception.message
                        )
                    }
                }
            }
        }

        scope.launch {
            getStockPriceHistoryUseCase("SOXL").collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Loading -> currentState.copy(isLoading = true)
                        is Result.Success -> currentState.copy(
                            soxlPriceHistory = result.data,
                            isLoading = currentState.tqqqPriceHistory == null,
                            error = null
                        )
                        is Result.Error -> currentState.copy(
                            isLoading = false,
                            error = result.exception.message
                        )
                    }
                }
            }
        }
    }

    override fun onTickerSelected(ticker: String) {
        _state.update { it.copy(selectedTicker = ticker) }
    }

    override fun onStartDateChanged(date: String) {
        _state.update { it.copy(startDate = date) }
    }

    override fun onEndDateChanged(date: String) {
        _state.update { it.copy(endDate = date) }
    }

    override fun onInitialCapitalChanged(value: Double) {
        _state.update {
            it.copy(
                initialCapital = value,
                oneTimeAmount = if (it.division > 0) value / it.division else it.oneTimeAmount
            )
        }
    }

    override fun onDivisionChanged(value: Int) {
        _state.update {
            it.copy(
                division = value,
                oneTimeAmount = if (value > 0) it.initialCapital / value else it.oneTimeAmount
            )
        }
    }

    override fun onTargetRateChanged(value: Double) {
        _state.update { it.copy(targetRate = value) }
    }

    override fun onStarModeChanged(mode: StarMode) {
        _state.update { it.copy(starMode = mode) }
    }

    override fun runBacktest() {
        val currentState = _state.value
        val request = BacktestRequest(
            ticker = currentState.selectedTicker,
            startDate = LocalDate.parse(currentState.startDate),
            endDate = LocalDate.parse(currentState.endDate),
            initialCapital = currentState.initialCapital,
            oneTimeAmount = currentState.oneTimeAmount,
            division = currentState.division,
            targetRate = currentState.targetRate,
            starMode = currentState.starMode
        )

        scope.launch {
            runBacktestUseCase(request).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Loading -> currentState.copy(
                            isRunningBacktest = true,
                            error = null
                        )
                        is Result.Success -> {
                            onBacktestCompleted(result.data)
                            currentState.copy(
                                isRunningBacktest = false,
                                error = null
                            )
                        }
                        is Result.Error -> currentState.copy(
                            isRunningBacktest = false,
                            error = result.exception.message
                        )
                    }
                }
            }
        }
    }

    override fun clearResult() {
        _state.update { it.copy(backtestResult = null) }
    }
}
