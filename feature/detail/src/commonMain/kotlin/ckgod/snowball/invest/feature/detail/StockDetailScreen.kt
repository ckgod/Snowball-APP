package ckgod.snowball.invest.feature.detail

import ckgod.snowball.invest.feature.detail.component.OpenOrdersCard
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
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
                is StockDetailEvent.CancelOrder -> component.onCancelOrder(event.orderNo)
                is StockDetailEvent.ModifyOrder -> component.onModifyOrder(event.orderNo, event.price, event.quantity)
                StockDetailEvent.OrderMessageShown -> component.onOrderMessageShown()
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
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.orderMessage) {
        val message = state.orderMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(StockDetailEvent.OrderMessageShown)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    exchangeRate = exchangeRate,
                    onEvent = onEvent
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
    onEvent: (StockDetailEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // 그리는 도중(LazyColumn 빌더)에 묶으면 매번 새 리스트가 생겨 폭락대비 묶음이 스킵되지 않는다.
    // 내역이 바뀔 때만 다시 묶는다.
    val groupedHistory = remember(state.historyItems) {
        state.historyItems.mapValues { (_, historyList) -> historyList.toHistoryListItems() }
    }

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

        if (state.openOrders.isNotEmpty()) {
            item(key = "open_orders") {
                OpenOrdersCard(
                    orders = state.openOrders,
                    isActionRunning = state.isOrderActionRunning,
                    onCancel = { orderNo -> onEvent(StockDetailEvent.CancelOrder(orderNo)) },
                    onModify = { orderNo, price, quantity ->
                        onEvent(StockDetailEvent.ModifyOrder(orderNo, price, quantity))
                    }
                )
            }
        }

        item(key = "order_plan_card") {
            OrderPlanCard(
                data = state.stockDetail,
                currencyType = currencyType,
                exchangeRate = exchangeRate,
            )
        }

        groupedHistory.forEach { (date, listItems) ->
            stickyHeader(key = "header_$date") {
                DateHeader(date)
            }

            items(
                items = listItems,
                // 주문번호가 아니라 DB id 로 키를 잡는다. 거부(REJECTED) 주문은 주문번호가 빈 문자열이라
                // 여러 건이면 키가 겹쳐 LazyColumn 이 죽는다.
                key = { listItem ->
                    when (listItem) {
                        is HistoryListItem.Single -> "history_${listItem.item.id}"
                        is HistoryListItem.CrashProtectionGroup ->
                            "crash_group_${listItem.items.first().id}"
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
