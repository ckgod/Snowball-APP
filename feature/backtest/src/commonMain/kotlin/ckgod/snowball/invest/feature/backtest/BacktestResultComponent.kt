package ckgod.snowball.invest.feature.backtest

import com.arkivanov.decompose.ComponentContext
import com.ckgod.snowball.model.BacktestResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BacktestResultState(
    val result: BacktestResponse? = null
)

interface BacktestResultComponent {
    val state: StateFlow<BacktestResultState>
    fun onBackClick()
}

class DefaultBacktestResultComponent(
    componentContext: ComponentContext,
    response: BacktestResponse?,
    private val onBack: () -> Unit
) : BacktestResultComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(BacktestResultState(result = response))
    override val state: StateFlow<BacktestResultState> = _state.asStateFlow()

    override fun onBackClick() {
        onBack()
    }
}
