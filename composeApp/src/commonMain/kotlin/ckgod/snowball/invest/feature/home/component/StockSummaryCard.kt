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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.domain.model.StockSummary
import ckgod.snowball.invest.ui.theme.getProfitColor
import ckgod.snowball.invest.ui.theme.getProgressColor
import ckgod.snowball.invest.util.formatDecimal

/**
 * 종목 요약 카드
 * 3단 구조로 정보를 체계적으로 표시
 */
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
            // (1) 상단: 종목 기본 정보 및 현재가
            StockBasicInfo(stock)

            Spacer(modifier = Modifier.height(16.dp))

            // (2) 중단: 전략 진행 상황
            StrategyProgress(stock)

            Spacer(modifier = Modifier.height(16.dp))

            // (3) 하단: 내 계좌 수익 현황
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
        Column {
            Text(
                text = stock.ticker,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stock.fullName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${stock.currentPrice.formatDecimal()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            val isProfit = stock.dailyChangeRate >= 0
            Text(
                text = "${if (isProfit) "+" else ""}${stock.dailyChangeRate.formatDecimal()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = getProfitColor(isProfit),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun StrategyProgress(stock: StockSummary) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val progress = stock.tValue.toFloat() / stock.totalDivision.toFloat()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = getProgressColor(),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
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
                color = getProgressColor().copy(alpha = 0.2f)
            ) {
                Text(
                    text = stock.phase.displayName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = getProgressColor(),
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
                text = "평단 $${stock.avgPrice.formatDecimal()}",
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
                color = getProfitColor(isProfit),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${if (isProfit) "+" else ""}$${stock.profitAmount.formatDecimal()}",
                style = MaterialTheme.typography.bodyLarge,
                color = getProfitColor(isProfit),
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
                text = "1회 매수액 $${stock.oneTimeAmount.formatDecimal()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "매수 누적 $${stock.totalInvested.formatDecimal()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val isProfit = 0 >= 0 // TODO 실제 값으로 변경
            Text(
                text = "실현 손익: $0",
                style = MaterialTheme.typography.bodyLarge,
                color =
                    if (0.0 == 0.0) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else getProfitColor(isProfit),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
