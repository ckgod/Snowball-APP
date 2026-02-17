package ckgod.snowball.invest.feature.backtest

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.feature.backtest.component.BacktestParamsCard
import ckgod.snowball.invest.feature.backtest.component.TickerSelector
import ckgod.snowball.invest.feature.backtest.component.chart.InteractivePriceChart
import ckgod.snowball.invest.ui.theme.ProgressBlueLight
import ckgod.snowball.invest.ui.theme.ProgressBlueDark

@Composable
fun BacktestScreen(
    component: BacktestComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.error != null && state.tqqqPriceHistory == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "오류가 발생했습니다",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                BacktestSetupContent(
                    component = component,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun BacktestSetupContent(
    component: BacktestComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) ProgressBlueLight else ProgressBlueDark

    val selectedPriceHistory = when (state.selectedTicker) {
        "TQQQ" -> state.tqqqPriceHistory
        "SOXL" -> state.soxlPriceHistory
        else -> null
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ticker selector
        item {
            TickerSelector(
                selectedTicker = state.selectedTicker,
                onTickerSelected = component::onTickerSelected
            )
        }

        // Interactive candlestick chart
        item {
            InteractivePriceChart(
                priceHistory = selectedPriceHistory,
                startDate = state.startDate,
                endDate = state.endDate,
                onStartDateChanged = component::onStartDateChanged,
                onEndDateChanged = component::onEndDateChanged
            )
        }

        // Section title
        item {
            Text(
                text = "백테스트 설정",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Params (2x2 grid + StarMode toggle)
        item {
            BacktestParamsCard(
                initialCapital = state.initialCapital,
                oneTimeAmount = state.oneTimeAmount,
                division = state.division,
                targetRate = state.targetRate,
                starMode = state.starMode,
                onInitialCapitalChanged = component::onInitialCapitalChanged,
                onDivisionChanged = component::onDivisionChanged,
                onTargetRateChanged = component::onTargetRateChanged,
                onStarModeChanged = component::onStarModeChanged
            )
        }

        // Error + Run button
        item {
            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = component::runBacktest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !state.isRunningBacktest,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor
                )
            ) {
                if (state.isRunningBacktest) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Text(
                        text = "백테스트 실행하기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

