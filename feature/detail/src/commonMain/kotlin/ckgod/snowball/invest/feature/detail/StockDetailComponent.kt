package ckgod.snowball.invest.feature.detail

import com.arkivanov.decompose.ComponentContext
import ckgod.snowball.invest.domain.model.StockDetailState
import ckgod.snowball.invest.domain.repository.StockDetailRepository
import ckgod.snowball.invest.domain.state.CurrencyStateHolder
import com.arkivanov.essenty.lifecycle.doOnDestroy
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

interface StockDetailComponent {
    val state: StateFlow<StockDetailState>

    fun onBackClick()
}

class DefaultStockDetailComponent(
    componentContext: ComponentContext,
    private val ticker: String,
    stockDetailRepository: StockDetailRepository? = null,
    private val onBack: () -> Unit
) : StockDetailComponent, ComponentContext by componentContext, KoinComponent {

    private val repository: StockDetailRepository = stockDetailRepository ?: get()
    private val currencyStateHolder: CurrencyStateHolder = get()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(
        StockDetailState(
            isLoading = true
        )
    )

    override val state: StateFlow<StockDetailState> = _state.asStateFlow()

    init {
        lifecycle.doOnDestroy {
            scope.cancel()
        }

        loadStockDetail()

        scope.launch {
            currencyStateHolder.currencyType.collect { newCurrencyType ->
                _state.update { currentState ->
                    currentState.copy(
                        stock = currentState.stock.copy(currencyType = newCurrencyType),
                        currencyType = newCurrencyType
                    )
                }
            }
        }
    }

    override fun onBackClick() {
        onBack()
    }

    private fun loadStockDetail() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val currentCurrencyType = currencyStateHolder.currencyType.value

            val result = withContext(Dispatchers.IO) {
                repository.getStockDetail(ticker)
            }

            result.onSuccess { stockDetail ->
                _state.update {
                    stockDetail.copy(
                        stock = stockDetail.stock.copy(currencyType = currentCurrencyType),
                        currencyType = currentCurrencyType
                    )
                }
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
