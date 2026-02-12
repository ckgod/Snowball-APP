package ckgod.snowball.invest.feature.backtest

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.feature.backtest.component.BacktestParamsCard
import ckgod.snowball.invest.feature.backtest.component.BacktestSummaryCard
import ckgod.snowball.invest.feature.backtest.component.TickerSelector
import ckgod.snowball.invest.feature.backtest.component.TradeHistoryList
import ckgod.snowball.invest.feature.backtest.component.chart.InteractivePriceChart
import ckgod.snowball.invest.ui.theme.ProgressBlueLight
import ckgod.snowball.invest.ui.theme.ProgressBlueDark
import com.ckgod.snowball.model.BacktestDailySnapshot

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

            state.backtestResult != null -> {
                BacktestResultContent(
                    component = component,
                    modifier = Modifier.fillMaxSize()
                )
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

@Composable
private fun BacktestResultContent(
    component: BacktestComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val result = state.backtestResult ?: return
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) ProgressBlueLight else ProgressBlueDark

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary
        item {
            DarkCard {
                BacktestSummaryCard(result = result)
            }
        }

        // Portfolio Value Chart
        item {
            DarkCard {
                PortfolioValueChart(snapshots = result.dailySnapshots)
            }
        }

        // Trade History
        item {
            DarkCard {
                TradeHistoryList(trades = result.trades)
            }
        }

        // Back button
        item {
            Button(
                onClick = component::clearResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Text(
                    text = "설정으로 돌아가기",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DarkCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun PortfolioValueChart(
    snapshots: List<BacktestDailySnapshot>,
    modifier: Modifier = Modifier
) {
    if (snapshots.isEmpty()) return

    val isDark = isSystemInDarkTheme()
    val lineColor = if (isDark) ProgressBlueLight else ProgressBlueDark

    val minValue = snapshots.minOf { it.portfolioValue }
    val maxValue = snapshots.maxOf { it.portfolioValue }
    val valueRange = maxValue - minValue

    Column(modifier = modifier) {
        Text(
            text = "자산 추이",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val topPadding = 8f
            val bottomPadding = 8f
            val chartHeight = canvasHeight - topPadding - bottomPadding

            if (snapshots.size < 2 || valueRange <= 0) return@Canvas

            val fillPath = Path().apply {
                snapshots.forEachIndexed { index, snapshot ->
                    val x = (index.toFloat() / (snapshots.size - 1)) * canvasWidth
                    val y = topPadding + chartHeight - ((snapshot.portfolioValue - minValue) / valueRange * chartHeight).toFloat()
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
                lineTo(canvasWidth, canvasHeight - bottomPadding)
                lineTo(0f, canvasHeight - bottomPadding)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.3f),
                        lineColor.copy(alpha = 0.02f)
                    )
                )
            )

            val linePath = Path().apply {
                snapshots.forEachIndexed { index, snapshot ->
                    val x = (index.toFloat() / (snapshots.size - 1)) * canvasWidth
                    val y = topPadding + chartHeight - ((snapshot.portfolioValue - minValue) / valueRange * chartHeight).toFloat()
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2f)
            )
        }
    }
}
