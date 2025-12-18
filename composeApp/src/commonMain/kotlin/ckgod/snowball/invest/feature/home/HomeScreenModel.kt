package ckgod.snowball.invest.feature.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ckgod.snowball.invest.domain.model.Portfolio
import ckgod.snowball.invest.domain.model.StockPhase
import ckgod.snowball.invest.domain.model.StockSummary
import ckgod.snowball.invest.feature.home.model.HomeEvent
import ckgod.snowball.invest.feature.home.model.HomeSideEffect
import ckgod.snowball.invest.feature.home.model.HomeState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 홈 화면의 ScreenModel (MVI 패턴)
 */
class HomeScreenModel : StateScreenModel<HomeState>(HomeState()) {

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
     * TODO: 실제 API 호출로 대체 필요
     */
    private fun loadPortfolio() {
        screenModelScope.launch {
            mutableState.value = state.value.copy(isLoading = true, error = null)

            try {
                // TODO: Repository에서 실제 데이터 가져오기
                val portfolio = getMockPortfolio()

                mutableState.value = state.value.copy(
                    isLoading = false,
                    portfolio = portfolio,
                    error = null
                )
            } catch (e: Exception) {
                mutableState.value = state.value.copy(
                    isLoading = false,
                    error = e.message ?: "데이터를 불러오는데 실패했습니다."
                )
                _sideEffect.send(
                    HomeSideEffect.ShowErrorToast(e.message ?: "오류가 발생했습니다.")
                )
            }
        }
    }

    /**
     * 새로고침
     */
    private fun refresh() {
        screenModelScope.launch {
            mutableState.value = state.value.copy(isRefreshing = true, error = null)

            try {
                // TODO: Repository에서 실제 데이터 가져오기
                val portfolio = getMockPortfolio()

                mutableState.value = state.value.copy(
                    isRefreshing = false,
                    portfolio = portfolio,
                    error = null
                )
            } catch (e: Exception) {
                mutableState.value = state.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "데이터를 불러오는데 실패했습니다."
                )
                _sideEffect.send(
                    HomeSideEffect.ShowErrorToast(e.message ?: "오류가 발생했습니다.")
                )
            }
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

    /**
     * Mock 데이터 생성 (개발용)
     * TODO: 실제 Repository로 대체
     */
    private fun getMockPortfolio(): Portfolio {
        return Portfolio(
            totalRealizedProfit = 1250.50,
            stocks = listOf(
                StockSummary(
                    ticker = "TQQQ",
                    fullName = "ProShares UltraPro QQQ",
                    currentPrice = 55.20,
                    dailyChangeRate = 2.5,
                    tValue = 12,
                    totalDivision = 40,
                    phase = StockPhase.FIRST_HALF,
                    avgPrice = 52.10,
                    quantity = 50,
                    profitRate = 5.95,
                    profitAmount = 155.20,
                    todayRealizedProfit = 12.5
                ),
                StockSummary(
                    ticker = "SOXL",
                    fullName = "Direxion Daily Semiconductor Bull 3X",
                    currentPrice = 28.75,
                    dailyChangeRate = -1.2,
                    tValue = 25,
                    totalDivision = 40,
                    phase = StockPhase.SECOND_HALF,
                    avgPrice = 30.20,
                    quantity = 100,
                    profitRate = -4.80,
                    profitAmount = -145.00,
                    todayRealizedProfit = null
                ),
                StockSummary(
                    ticker = "UPRO",
                    fullName = "ProShares UltraPro S&P500",
                    currentPrice = 62.30,
                    dailyChangeRate = 1.8,
                    tValue = 8,
                    totalDivision = 40,
                    phase = StockPhase.FIRST_HALF,
                    avgPrice = 58.50,
                    quantity = 30,
                    profitRate = 6.50,
                    profitAmount = 114.00,
                    todayRealizedProfit = null
                )
            )
        )
    }
}
