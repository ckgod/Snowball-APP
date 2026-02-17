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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.feature.backtest.component.BacktestSummaryCard
import ckgod.snowball.invest.feature.backtest.component.TradeHistoryList
import ckgod.snowball.invest.ui.theme.ProgressBlueDark
import ckgod.snowball.invest.ui.theme.ProgressBlueLight
import com.ckgod.snowball.model.BacktestDailySnapshot
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_arrow_back

@Composable
fun BacktestResultContent(
    component: BacktestResultComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val result = state.result

    if (result == null) {
        BacktestResultEmpty(
            onBackClick = component::onBackClick,
            modifier = modifier
        )
        return
    }

    BacktestResultScreen(
        result = result,
        onBackClick = component::onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BacktestResultScreen(
    result: com.ckgod.snowball.model.BacktestResponse,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) ProgressBlueLight else ProgressBlueDark

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "백테스트 결과",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BacktestResultEmpty(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "백테스트 결과",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "결과를 불러올 수 없습니다",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("돌아가기")
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
