package ckgod.snowball.invest.feature.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.domain.model.StockSummary
import ckgod.snowball.invest.ui.component.AnimatedProgressIndicator
import ckgod.snowball.invest.ui.theme.getPhaseColor
import ckgod.snowball.invest.ui.theme.getProfitColor
import ckgod.snowball.invest.util.formatDecimal

@Composable
fun StockSummaryCard(
    stock: StockSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StockBasicInfo(stock)

            Spacer(modifier = Modifier.height(16.dp))

            StrategyProgress(stock)

            Spacer(modifier = Modifier.height(16.dp))

            AccountProfit(stock)

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AccountHistory(stock)
        }
    }
}


@Composable
private fun StockBasicInfo(stock: StockSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = stock.ticker,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stock.fullName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stock.currentPrice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            val isProfit = stock.dailyChangeRate >= 0
            Text(
                text = "${if (isProfit) "+" else ""}${stock.dailyChangeRate.formatDecimal()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = getProfitColor(stock.dailyChangeRate),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun StrategyProgress(stock: StockSummary) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val progress = stock.tValue.toFloat() / stock.totalDivision.toFloat()
        AnimatedProgressIndicator(
            progress = progress,
            color = getPhaseColor(stock.phase),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            animationEnabled = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "T-Value: ${stock.tValue} / ${stock.totalDivision}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = getPhaseColor(stock.phase).copy(alpha = 0.2f)
            ) {
                Text(
                    text = stock.phase.displayName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = getPhaseColor(stock.phase),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
private fun AccountProfit(stock: StockSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "평단 ${stock.avgPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "보유 ${stock.quantity}주",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val isProfit = stock.profitRate >= 0
            Text(
                text = "${if (isProfit) "+" else ""}${stock.profitRate.formatDecimal()}%",
                style = MaterialTheme.typography.headlineSmall,
                color = getProfitColor(stock.profitRate),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stock.profitAmount,
                style = MaterialTheme.typography.bodyLarge,
                color = getProfitColor(stock.profitRate),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AccountHistory(stock: StockSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "1회 매수액 ${stock.oneTimeAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "매수 누적 ${stock.totalInvested}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "실현 손익: ${stock.realizedProfit}",
                style = MaterialTheme.typography.bodyLarge,
                color = getProfitColor(stock.realizedProfit),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
