package ckgod.snowball.invest.feature.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ckgod.snowball.model.CurrencyType
import ckgod.snowball.invest.feature.home.component.StockSummaryCard
import ckgod.snowball.invest.feature.home.model.HomeEvent
import ckgod.snowball.invest.feature.home.model.HomeState
import ckgod.snowball.invest.ui.component.CurrencyToggleSwitch
import ckgod.snowball.invest.ui.component.CustomPullToRefresh
import ckgod.snowball.invest.ui.extensions.toDisplayProfit
import ckgod.snowball.invest.ui.extensions.withFixedHeight
import ckgod.snowball.invest.ui.theme.getProfitColor

@Composable
fun HomeScreen(
    component: HomeComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()

    HomeScreenContent(
        state = state,
        onEvent = component::onEvent,
        onStockClick = component::onStockClick,
        onCurrencySwitch = component::onCurrencySwitch,
        modifier = modifier,
        currencyType = state.currencyType,
        exchangeRate = state.exchangeRate
    )
}

@Composable
internal fun HomeScreenContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    onStockClick: (String) -> Unit,
    onCurrencySwitch: (CurrencyType) -> Unit,
    modifier: Modifier = Modifier,
    currencyType: CurrencyType,
    exchangeRate: Double
) {
    CustomPullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(HomeEvent.Refresh) },
        modifier = modifier.fillMaxSize(),
        indicatorColor = MaterialTheme.colorScheme.primary
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
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
                            text = state.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                state.data != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            TotalProfitHeader(
                                formattedProfit = state.data.totalProfit.toDisplayProfit(currencyType, exchangeRate),
                                currencyType = state.currencyType,
                                onCurrencySwitch = onCurrencySwitch
                            )
                        }

                        items(
                            items = state.data.statusList,
                            key = { it.ticker }
                        ) { stock ->
                            StockSummaryCard(
                                data = stock,
                                currencyType = currencyType,
                                exchangeRate = exchangeRate
                            ) {
                                onStockClick(stock.ticker)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalProfitHeader(
    formattedProfit: String,
    currencyType: CurrencyType,
    onCurrencySwitch: (CurrencyType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "총 실현 손익",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedProfit,
                    style = MaterialTheme.typography.headlineMedium.withFixedHeight(),
                    color = getProfitColor(formattedProfit),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                CurrencyToggleSwitch(
                    isKrw = currencyType == CurrencyType.KRW,
                    onToggleChange = { isKrw ->
                        onCurrencySwitch(if (isKrw) CurrencyType.KRW else CurrencyType.USD)
                    }
                )
            }
        }
    }
}
