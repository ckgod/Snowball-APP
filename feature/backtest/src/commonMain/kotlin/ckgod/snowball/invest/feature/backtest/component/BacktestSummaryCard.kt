package ckgod.snowball.invest.feature.backtest.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.extensions.formatDecimal
import ckgod.snowball.invest.ui.theme.getProfitColor
import com.ckgod.snowball.model.BacktestResponse

@Composable
fun BacktestSummaryCard(
    result: BacktestResponse,
    modifier: Modifier = Modifier
) {
    val summary = result.summary
    val profitColor = getProfitColor(summary.totalReturn)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "총 수익률",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${if (summary.totalReturn > 0) "+" else ""}${summary.totalReturn.formatDecimal()}%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = profitColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "최종 포트폴리오: $${summary.finalPortfolioValue.formatDecimal()}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
        )

        Spacer(modifier = Modifier.height(12.dp))

        SummaryRow("거래일 수", "${summary.totalTradingDays}일")
        SummaryRow("총 주문 / 체결", "${summary.totalOrders}건 / ${summary.filledOrders}건")
        SummaryRow("총 투자금", "$${summary.totalInvested.formatDecimal()}")
        SummaryRow("최종 수량", "${summary.finalQuantity}주")
        SummaryRow("최종 평단가", "$${summary.finalAvgPrice.formatDecimal()}")
        SummaryRow("최종 T값", summary.finalTValue.formatDecimal())
        SummaryRow(
            "실현 손익",
            "$${summary.realizedProfit.formatDecimal()}",
            valueColor = getProfitColor(summary.realizedProfit)
        )
        SummaryRow(
            "미실현 손익",
            "$${summary.unrealizedProfit.formatDecimal()}",
            valueColor = getProfitColor(summary.unrealizedProfit)
        )
        SummaryRow("잔여 현금", "$${summary.availableCash.formatDecimal()}")
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}
