package ckgod.snowball.invest.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.domain.model.TradeStatus
import ckgod.snowball.invest.domain.model.TradeType
import ckgod.snowball.invest.feature.detail.model.StockDetailEvent
import ckgod.snowball.invest.domain.model.StockDetailState
import ckgod.snowball.invest.ui.theme.getProfitColor
import ckgod.snowball.invest.ui.theme.getProgressColor
import ckgod.snowball.invest.util.formatDecimal
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_arrow_back

@Composable
fun StockDetailContent(
    component: StockDetailComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()

    StockDetailScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                StockDetailEvent.BackClick -> component.onBackClick()
                StockDetailEvent.Refresh -> component.onRefresh()
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    state: StockDetailState,
    onEvent: (StockDetailEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.ticker,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(StockDetailEvent.BackClick) }) {
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
            // 1. Summary Header
            item {
                SummaryHeader(state)
            }

            // 2. Strategy Dashboard
            item {
                StrategyDashboard(state)
            }

            // 3. History Timeline with sticky headers
            state.historyItems.forEach { (date, items) ->
                stickyHeader {
                    DateHeader(date)
                }

                items(items) { item ->
                    HistoryItemRow(item)
                }
            }
        }
    }
}

/**
 * 요약 헤더 카드
 */
@Composable
private fun SummaryHeader(state: StockDetailState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 현재가
            Text(
                text = "$${state.currentPrice.formatDecimal()}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 손익 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "손익",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${if (state.profitAmount >= 0) "+" else ""}$${state.profitAmount.formatDecimal()}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = getProfitColor(state.profitAmount),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (state.profitRate >= 0) "+" else ""}${state.profitRate.formatDecimal()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = getProfitColor(state.profitRate),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "보유량",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${state.quantity}주",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "평단 $${state.avgPrice.formatDecimal()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 전략 대시보드 카드
 */
@Composable
private fun StrategyDashboard(state: StockDetailState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // T-Value Progress
            Text(
                text = "T-Value 진행도",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.tValue} / ${state.division}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${((state.tValue.toDouble() / state.division) * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { state.tValue.toFloat() / state.division.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = getProgressColor(),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 정보 그리드
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "1회 매수액",
                    value = "$${state.oneTimeAmount.formatDecimal()}"
                )
                InfoItem(
                    label = "다음 매수가",
                    value = "$${state.nextBuyStarPrice.formatDecimal()}"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InfoItem(
                label = "다음 매도가",
                value = "$${state.nextSellStarPrice.formatDecimal()}"
            )
        }
    }
}

/**
 * 정보 아이템
 */
@Composable
private fun InfoItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 날짜 헤더 (Sticky Header)
 */
@Composable
private fun DateHeader(dateString: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = dateString,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * 히스토리 아이템 행
 */
@Composable
private fun HistoryItemRow(item: HistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.displayTime,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 메인 정보
        Column(modifier = Modifier.weight(1f)) {
            when (item) {
                is HistoryItem.Trade -> {
                    Text(
                        text = "${item.orderType.displayName} ${item.type.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$${item.price.formatDecimal()} × ${item.quantity}ea",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                is HistoryItem.Sync -> {
                    Text(
                        text = "일일 정산",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "실현 손익 ${if (item.profit >= 0) "+" else ""}$${item.profit.formatDecimal()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = getProfitColor(item.profit)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 상태 뱃지
        StatusBadge(item)
    }
}

/**
 * 상태 뱃지
 */
@Composable
private fun StatusBadge(item: HistoryItem) {
    val (text, color) = when (item) {
        is HistoryItem.Trade -> {
            when (item.status) {
                TradeStatus.PENDING -> "주문" to MaterialTheme.colorScheme.onSurfaceVariant
                TradeStatus.FILLED -> {
                    val tradeColor = when (item.type) {
                        TradeType.BUY -> getProgressColor()
                        TradeType.SELL -> getProfitColor(-1.0) // Red for sell
                    }
                    "체결" to tradeColor
                }
                TradeStatus.CANCELED -> "취소" to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        }
        is HistoryItem.Sync -> "정산" to getProgressColor()
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
