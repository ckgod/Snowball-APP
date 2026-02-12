package ckgod.snowball.invest.domain.state

import com.ckgod.snowball.model.BacktestResponse
import com.ckgod.snowball.model.StarMode
import com.ckgod.snowball.model.StockPriceHistoryResponse

data class BacktestState(
    // Setup
    val selectedTicker: String = "TQQQ",
    val tqqqPriceHistory: StockPriceHistoryResponse? = null,
    val soxlPriceHistory: StockPriceHistoryResponse? = null,
    val startDate: String = "2021-01-04",
    val endDate: String = "2023-12-28",
    val initialCapital: Double = 10000.0,
    val oneTimeAmount: Double = 250.0,
    val division: Int = 40,
    val targetRate: Double = 15.0,
    val starMode: StarMode = StarMode.P1_2,
    // Results
    val backtestResult: BacktestResponse? = null,
    val isRunningBacktest: Boolean = false,
    // Common
    val isLoading: Boolean = true,
    val error: String? = null
)
