package ckgod.snowball.invest.feature.detail

import ckgod.snowball.invest.data.repository.StockDetailRepository
import com.arkivanov.decompose.ComponentContext
import ckgod.snowball.invest.feature.detail.model.StockDetailState
import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepository
import ckgod.snowball.invest.domain.usecase.GroupTradeHistoriesByDateUseCase
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.ckgod.snowball.model.InvestmentStatusResponse
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
import kotlin.onSuccess

interface StockDetailComponent {
    val state: StateFlow<StockDetailState>

    fun onBackClick()
}

class DefaultStockDetailComponent(
    componentContext: ComponentContext,
    private val ticker: String,
    private val onBack: () -> Unit,
    private val stockDetailRepository: StockDetailRepository,
    private val currencyRepository: CurrencyPreferencesRepository,
    private val groupTradeHistoriesByDateUseCase: GroupTradeHistoriesByDateUseCase
) : StockDetailComponent, ComponentContext by componentContext, KoinComponent {

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
            currencyRepository.currencyType.collect { newCurrencyType ->
                _state.update { currentState ->
                    currentState.copy(
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

            val currentCurrencyType = currencyRepository.currencyType.value

            val result = withContext(Dispatchers.IO) {
                stockDetailRepository.getStockDetail(ticker)
            }
            result.onSuccess { response ->
                _state.update {
                    it.copy(
                        stockDetail = response.status ?: InvestmentStatusResponse(),
                        historyItems = groupTradeHistoriesByDateUseCase(response.histories),
                        currencyType = currentCurrencyType,
                        exchangeRate = response.status?.exchangeRate ?: 0.0,
                        isLoading = false,
                        error = null,
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
