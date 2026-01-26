package ckgod.snowball.invest.feature.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.feature.account.component.PortfolioCard
import ckgod.snowball.invest.feature.account.component.StockCapitalCard
import ckgod.snowball.invest.feature.account.component.SummaryHeader
import ckgod.snowball.invest.ui.component.CustomPullToRefresh
import com.ckgod.snowball.model.TotalAssetResponse
import com.ckgod.snowball.model.CurrencyType

@Composable
fun AccountScreen(
    component: AccountComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()

    CustomPullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { component.refresh() },
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
                            text = state.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { component.refresh() }) {
                            Text("다시 시도")
                        }
                    }
                }

                state.data != null -> {
                    AccountContent(
                        accountStatus = state.data!!,
                        modifier = Modifier.fillMaxSize(),
                        currencyType = state.currencyType,
                        exchangeRate = state.exchangeRate
                    )
                }
            }
        }
    }
}

@Composable
internal fun AccountContent(
    modifier: Modifier = Modifier,
    accountStatus: TotalAssetResponse,
    currencyType: CurrencyType,
    exchangeRate: Double
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryHeader(
                data = accountStatus,
                currencyType = currencyType,
                exchangeRate = exchangeRate
            )
        }

        item {
            PortfolioCard(
                data = accountStatus,
                currencyType = currencyType,
                exchangeRate = exchangeRate
            )
        }

        items(accountStatus.holdingStocks) { stock ->
            StockCapitalCard(data = stock)
        }
    }
}



