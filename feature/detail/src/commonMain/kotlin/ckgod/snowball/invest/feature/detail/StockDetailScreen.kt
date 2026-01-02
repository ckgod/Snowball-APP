package ckgod.snowball.invest.feature.detail

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
import ckgod.snowball.invest.domain.model.StockDetailState
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
                        text = state.stock.ticker,
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
        if (state.isLoading) {
            StockDetailSkeleton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "summary_header") {
                    SummaryHeader(state.stock)
                }

                item(key = "strategy_dashboard") {
                    StrategyDashboard(state.stock)
                }

                item(key = "order_plan_card") {
                    OrderPlanCard(state.stock)
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
                                HistoryItemRow(listItem.item)
                            }
                            is HistoryListItem.CrashProtectionGroup -> {
                                CrashProtectionAccordion(items = listItem.items)
                            }
                        }
                    }
                }
            }
        }
    }
}
