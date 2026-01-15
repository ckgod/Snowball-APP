package ckgod.snowball.invest.feature.detail

import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.domain.state.StockDetailState
import ckgod.snowball.invest.domain.usecase.GetStockDetailUseCase
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface StockDetailComponent {
    val state: StateFlow<StockDetailState>

    fun onBackClick()
}

class DefaultStockDetailComponent(
    componentContext: ComponentContext,
    private val ticker: String,
    private val onBack: () -> Unit,
    private val getStockDetailUseCase: GetStockDetailUseCase
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

        scope.launch {
            getStockDetailUseCase(ticker).collect { result ->
                _state.update { currentState ->
                    when(result) {
                        is Result.Loading -> currentState.copy(isLoading = true)
                        is Result.Error -> currentState.copy(error = result.exception.message)
                        is Result.Success -> {
                            result.data
                        }
                    }
                }
            }
        }
    }

    override fun onBackClick() {
        onBack()
    }
}
