package ckgod.snowball.invest.feature.backtest.component.chart

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.theme.BuyRedDark
import ckgod.snowball.invest.ui.theme.BuyRedLight
import ckgod.snowball.invest.ui.theme.ProgressBlueDark
import ckgod.snowball.invest.ui.theme.ProgressBlueLight
import ckgod.snowball.invest.ui.theme.SellBlueDark
import ckgod.snowball.invest.ui.theme.SellBlueLight
import com.ckgod.snowball.model.StockPriceHistoryResponse
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun InteractivePriceChart(
    priceHistory: StockPriceHistoryResponse?,
    startDate: String,
    endDate: String,
    onStartDateChanged: (String) -> Unit,
    onEndDateChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (priceHistory == null || priceHistory.prices.isEmpty()) return

    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) ProgressBlueLight else ProgressBlueDark
    val bullColor = if (isDark) BuyRedLight else BuyRedDark
    val bearColor = if (isDark) SellBlueLight else SellBlueDark

    var viewMode by remember { mutableStateOf(ChartViewMode.DAILY) }

    val candles = remember(priceHistory.prices, viewMode) {
        aggregateCandles(priceHistory.prices, viewMode)
    }

    val initialVisible = if (candles.size > 30) 30 else candles.size

    val chartState = remember(candles.size) {
        InteractiveChartState(totalDataPoints = candles.size, initialVisibleCount = initialVisible)
    }

    // Initialize fractions from external dates (only on candles change / mode switch)
    // Then watch for selection index changes and report dates back
    LaunchedEffect(candles) {
        chartState.initSelectionFromDates(startDate, endDate, candles)

        snapshotFlow {
            Pair(chartState.selectionStartIndex, chartState.selectionEndIndex)
        }
            .distinctUntilChanged()
            .collect { (startIdx, endIdx) ->
                candles.getOrNull(startIdx)?.date?.let(onStartDateChanged)
                candles.getOrNull(endIdx)?.date?.let(onEndDateChanged)
            }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ViewModeSelector(
            selectedMode = viewMode,
            onModeSelected = { viewMode = it },
            accentColor = accentColor,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(8.dp))

        MainChartCanvas(
            candles = candles,
            chartState = chartState,
            viewMode = viewMode,
            accentColor = accentColor,
            bullColor = bullColor,
            bearColor = bearColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
