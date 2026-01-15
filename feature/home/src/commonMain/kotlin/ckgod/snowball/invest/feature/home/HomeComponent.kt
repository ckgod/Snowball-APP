package ckgod.snowball.invest.feature.home

import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepository
import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.domain.state.InvestmentStatusState
import ckgod.snowball.invest.domain.usecase.GetInvestmentStatusUseCase
import ckgod.snowball.invest.feature.home.model.HomeEvent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.ckgod.snowball.model.CurrencyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface HomeComponent {
    val state: StateFlow<InvestmentStatusState>

    fun onEvent(event: HomeEvent)
    fun onStockClick(ticker: String)
    fun onCurrencySwitch(type: CurrencyType)
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val onStockSelected: (String) -> Unit,
    private val currencyRepository: CurrencyPreferencesRepository,
    private val getInvestmentStatusUseCase: GetInvestmentStatusUseCase
) : HomeComponent, ComponentContext by componentContext, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(InvestmentStatusState())
    override val state: StateFlow<InvestmentStatusState> = _state.asStateFlow()

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        lifecycle.doOnDestroy {
            scope.cancel()
        }

        scope.launch {
            getInvestmentStatusUseCase(_refreshTrigger).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Loading -> {
                            if (currentState.data == null) {
                                currentState.copy(isRefreshing = false, isLoading = true)
                            } else {
                                currentState.copy(isRefreshing = true, isLoading = false)
                            }
                        }
                        is Result.Success -> result.data
                        is Result.Error -> {
                            currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = result.exception.message
                            )
                        }
                    }
                }
            }
        }

        refresh()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> refresh()
            is HomeEvent.OnStockClick -> onStockClick(event.ticker)
        }
    }

    private fun refresh() {
        scope.launch {
            _refreshTrigger.emit(Unit)
        }
    }

    override fun onCurrencySwitch(type: CurrencyType) {
        currencyRepository.setCurrencyType(type)
    }

    override fun onStockClick(ticker: String) {
        onStockSelected(ticker)
    }
}
