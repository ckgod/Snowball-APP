package ckgod.snowball.invest.feature.detail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.domain.state.StockDetailState
import ckgod.snowball.invest.feature.detail.component.CrashProtectionAccordion
import ckgod.snowball.invest.feature.detail.component.DateHeader
import ckgod.snowball.invest.feature.detail.component.HistoryItemRow
import ckgod.snowball.invest.feature.detail.component.HistoryListItem
import ckgod.snowball.invest.feature.detail.component.OrderPlanCard
import ckgod.snowball.invest.feature.detail.component.StockDetailSkeleton
import ckgod.snowball.invest.feature.detail.component.StrategyDashboard
import ckgod.snowball.invest.feature.detail.component.SummaryHeader
import ckgod.snowball.invest.feature.detail.component.toHistoryListItems
import ckgod.snowball.invest.feature.detail.model.StockDetailEvent
import com.ckgod.snowball.model.CurrencyType
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_arrow_back

/**
 * @param showBackButton 2-pane 에서는 false. 왼쪽에 목록이 그대로 남아 있어 돌아갈 곳이 없다.
 */
@Composable
fun StockDetailContent(
    component: StockDetailComponent,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true
) {
    val state by component.state.collectAsState()

    StockDetailScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                StockDetailEvent.BackClick -> component.onBackClick()
            }
        },
        modifier = modifier,
        currencyType = state.currencyType,
        exchangeRate = state.exchangeRate,
        showBackButton = showBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    state: StockDetailState,
    onEvent: (StockDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
    currencyType: CurrencyType,
    exchangeRate: Double,
    showBackButton: Boolean = true
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.stockDetail.ticker,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { onEvent(StockDetailEvent.BackClick) }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        // 스켈레톤과 실제 콘텐츠는 레이아웃 높이가 달라서, 즉시 맞바꾸면 화면이 덜그럭거린다.
        // 화면 전환 슬라이드가 끝나기 전에 데이터가 도착하면 두 움직임이 겹쳐 특히 거슬린다.
        // 둘 다 같은 영역을 채우므로 교차 페이드로 녹이면 교체 지점이 드러나지 않는다.
        Crossfade(
            targetState = state.isLoading,
            animationSpec = tween(durationMillis = ContentFadeDurationMillis),
            label = "StockDetailContent",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { isLoading ->
            if (isLoading) {
                StockDetailSkeleton(modifier = Modifier.fillMaxSize())
            } else {
                StockDetailList(
                    state = state,
                    currencyType = currencyType,
                    exchangeRate = exchangeRate
                )
            }
        }
    }
}

/** 스켈레톤에서 넘어올 때 쓰는 교차 페이드 길이. 전환 슬라이드보다 짧게 둬서 끌리지 않게 한다. */
private const val ContentFadeDurationMillis = 220

@Composable
private fun StockDetailList(
    state: StockDetailState,
    currencyType: CurrencyType,
    exchangeRate: Double,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "summary_header") {
            SummaryHeader(
                data = state.stockDetail,
                currencyType = currencyType,
                exchangeRate = exchangeRate
            )
        }

        item(key = "strategy_dashboard") {
            StrategyDashboard(data = state.stockDetail)
        }

        item(key = "order_plan_card") {
            OrderPlanCard(
                data = state.stockDetail,
                currencyType = currencyType,
                exchangeRate = exchangeRate,
            )
        }

        state.historyItems.entries.forEach { (date, historyList) ->
            stickyHeader(key = "header_$date") {
                DateHeader(date)
            }

            val listItems = historyList.toHistoryListItems()

            items(
                items = listItems,
                key = { listItem ->
                    when (listItem) {
                        is HistoryListItem.Single -> listItem.item.orderNo
                        is HistoryListItem.CrashProtectionGroup ->
                            "crash_group_${listItem.items.first().orderNo}"
                    }
                }
            ) { listItem ->
                when (listItem) {
                    is HistoryListItem.Single -> {
                        HistoryItemRow(
                            data = listItem.item,
                            currencyType = currencyType,
                            exchangeRate = exchangeRate,
                        )
                    }
                    is HistoryListItem.CrashProtectionGroup -> {
                        CrashProtectionAccordion(
                            list = listItem.items,
                            currencyType = currencyType,
                            exchangeRate = exchangeRate
                        )
                    }
                }
            }
        }
    }
}
