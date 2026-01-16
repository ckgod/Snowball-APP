package ckgod.snowball.invest.feature.account

import ckgod.snowball.invest.data.result.Result
import ckgod.snowball.invest.domain.state.AccountState
import ckgod.snowball.invest.domain.usecase.GetAccountUseCase
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
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

interface AccountComponent {
    val state: StateFlow<AccountState>
    fun refresh()
}

class DefaultAccountComponent(
    componentContext: ComponentContext,
    private val getAccountUseCase: GetAccountUseCase
) : AccountComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(AccountState())
    override val state: StateFlow<AccountState> = _state.asStateFlow()
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        lifecycle.doOnDestroy {
            scope.cancel()
        }

        scope.launch {
            getAccountUseCase(_refreshTrigger).collect { result ->
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

    override fun refresh() {
        scope.launch {
            _refreshTrigger.emit(Unit)
        }
    }
}
