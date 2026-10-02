package ckgod.snowball.invest.feature.detail

import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.domain.state.StockDetailState
import ckgod.snowball.invest.domain.usecase.GetStockDetailUseCase
import ckgod.snowball.invest.domain.usecase.ManageOpenOrdersUseCase
import com.ckgod.snowball.model.OrderActionResponse
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

    fun onCancelOrder(orderNo: String)

    fun onModifyOrder(orderNo: String, price: Double, quantity: Int)

    fun onOrderMessageShown()
}

class DefaultStockDetailComponent(
    componentContext: ComponentContext,
    private val ticker: String,
    private val onBack: () -> Unit,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val manageOpenOrdersUseCase: ManageOpenOrdersUseCase
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
                        // 상세 데이터가 다시 와도 미체결 목록·안내 문구는 유지한다
                        is Result.Success -> result.data.copy(
                            openOrders = currentState.openOrders,
                            isOrderActionRunning = currentState.isOrderActionRunning,
                            orderMessage = currentState.orderMessage
                        )
                    }
                }
            }
        }

        loadOpenOrders()
    }

    override fun onBackClick() {
        onBack()
    }

    override fun onCancelOrder(orderNo: String) = runOrderAction {
        manageOpenOrdersUseCase.cancel(orderNo)
    }

    override fun onModifyOrder(orderNo: String, price: Double, quantity: Int) = runOrderAction {
        manageOpenOrdersUseCase.modify(orderNo, price, quantity)
    }

    override fun onOrderMessageShown() {
        _state.update { it.copy(orderMessage = null) }
    }

    private fun loadOpenOrders() {
        scope.launch {
            // 미체결 조회 실패가 상세 화면 전체를 가리지 않도록 따로 받는다
            val orders = runCatching { manageOpenOrdersUseCase.getOpenOrders(ticker) }.getOrNull() ?: return@launch
            _state.update { it.copy(openOrders = orders) }
        }
    }

    private fun runOrderAction(action: suspend () -> OrderActionResponse) {
        if (_state.value.isOrderActionRunning) return  // 연타로 같은 요청이 두 번 가지 않게
        _state.update { it.copy(isOrderActionRunning = true) }
        scope.launch {
            val result = action()
            _state.update { it.copy(isOrderActionRunning = false, orderMessage = result.message) }
            loadOpenOrders()
        }
    }
}
