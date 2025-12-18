package ckgod.snowball.invest.feature.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ckgod.snowball.invest.domain.model.Portfolio
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import ckgod.snowball.invest.feature.home.model.HomeEvent
import ckgod.snowball.invest.feature.home.model.HomeSideEffect
import ckgod.snowball.invest.feature.home.model.HomeState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 홈 화면의 ScreenModel (MVI 패턴)
 */
class HomeScreenModel(
    private val portfolioRepository: PortfolioRepository
) : StateScreenModel<HomeState>(HomeState()) {

    private val _sideEffect = Channel<HomeSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadPortfolio()
    }

    /**
     * 이벤트 처리
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadPortfolio -> loadPortfolio()
            is HomeEvent.Refresh -> refresh()
            is HomeEvent.OnStockClick -> onStockClick(event.ticker)
            is HomeEvent.DismissError -> dismissError()
        }
    }

    /**
     * 포트폴리오 데이터 로드
     */
    private fun loadPortfolio() {
        screenModelScope.launch {
            mutableState.value = state.value.copy(isLoading = true, error = null)

            fetchPortfolio(
                onSuccess = { portfolio ->
                    mutableState.value = state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        portfolio = portfolio,
                        error = null
                    )
                },
                onFailure = { exception ->
                    mutableState.value = state.value.copy(
                        isLoading = false,
                        portfolio = null,
                        error = "error: ${exception.message}"
                    )
                    _sideEffect.send(
                        HomeSideEffect.ShowErrorToast(exception.message ?: "오류가 발생했습니다.")
                    )
                }
            )
        }
    }

    /**
     * 새로고침
     */
    private fun refresh() {
        screenModelScope.launch {
            mutableState.value = state.value.copy(isRefreshing = true, error = null)

            fetchPortfolio(
                onSuccess = { portfolio ->
                    mutableState.value = state.value.copy(
                        isRefreshing = false,
                        portfolio = portfolio,
                        error = null
                    )
                },
                onFailure = { exception ->
                    mutableState.value = state.value.copy(
                        isLoading = false,
                        portfolio = null,
                        error = "error: ${exception.message}"
                    )
                    _sideEffect.send(
                        HomeSideEffect.ShowErrorToast(exception.message ?: "오류가 발생했습니다.")
                    )
                }
            )
        }
    }

    private suspend inline fun fetchPortfolio(
        onSuccess: (Portfolio) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            val result = portfolioRepository.getPortfolioStatus()

            result.fold(
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        } catch (e: Exception) {
            onFailure.invoke(e)
        }
    }

    /**
     * 종목 카드 클릭
     */
    private fun onStockClick(ticker: String) {
        screenModelScope.launch {
            _sideEffect.send(HomeSideEffect.NavigateToStockDetail(ticker))
        }
    }

    /**
     * 에러 메시지 닫기
     */
    private fun dismissError() {
        mutableState.value = state.value.copy(error = null)
    }
}
