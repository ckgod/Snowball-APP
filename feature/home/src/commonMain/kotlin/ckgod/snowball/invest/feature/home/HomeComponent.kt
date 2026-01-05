package ckgod.snowball.invest.feature.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import ckgod.snowball.invest.domain.model.CurrencyType
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import ckgod.snowball.invest.domain.state.CurrencyStateHolder
import ckgod.snowball.invest.feature.home.model.HomeEvent
import ckgod.snowball.invest.feature.home.model.HomeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface HomeComponent {
    val state: StateFlow<HomeState>

    fun onEvent(event: HomeEvent)
    fun onStockClick(ticker: String)
    fun onCurrencySwitch(type: CurrencyType)
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    portfolioRepository: PortfolioRepository? = null,
    private val onStockSelected: (String) -> Unit
) : HomeComponent, ComponentContext by componentContext, KoinComponent {

    private val portfolioRepository: PortfolioRepository = portfolioRepository ?: get()
    private val currencyStateHolder: CurrencyStateHolder = get()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(HomeState())
    override val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        lifecycle.doOnDestroy {
            scope.cancel()
        }

        loadPortfolio(isRefresh = false)

        scope.launch {
            currencyStateHolder.currencyType.collect { newCurrencyType ->
                _state.update { currentState ->
                    val portfolio = currentState.portfolio ?: return@update currentState.copy(currencyType = newCurrencyType)

                    val updatedStocks = portfolio.stocks.map { stock ->
                        stock.copy(currencyType = newCurrencyType)
                    }

                    currentState.copy(
                        portfolio = portfolio.copy(
                            stocks = updatedStocks,
                            currencyType = newCurrencyType
                        ),
                        currencyType = newCurrencyType
                    )
                }
            }
        }
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> loadPortfolio(isRefresh = true)
            HomeEvent.LoadPortfolio -> loadPortfolio(isRefresh = false)
            HomeEvent.DismissError -> _state.update { it.copy(error = null) }
            is HomeEvent.OnStockClick -> onStockClick(event.ticker)
        }
    }

    override fun onCurrencySwitch(type: CurrencyType) {
        currencyStateHolder.setCurrencyType(type)
    }

    override fun onStockClick(ticker: String) {
        onStockSelected(ticker)
    }

    private fun loadPortfolio(isRefresh: Boolean) {
        scope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _state.update { it.copy(isLoading = true, error = null) }
            }

            val currentCurrencyType = currencyStateHolder.currencyType.value

            val result = withContext(Dispatchers.IO) {
                portfolioRepository.getPortfolioStatus()
            }

            result
                .onSuccess { portfolio ->
                    val updatedStocks = portfolio.stocks.map { stock ->
                        stock.copy(currencyType = currentCurrencyType)
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            portfolio = portfolio.copy(
                                stocks = updatedStocks,
                                currencyType = currentCurrencyType
                            ),
                            currencyType = currentCurrencyType,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Unknown error"
                        )
                    }
                }
        }
    }
}
