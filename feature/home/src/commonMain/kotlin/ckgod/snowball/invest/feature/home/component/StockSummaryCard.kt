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
import ckgod.snowball.invest.ui.component.GradientProgressIndicator
import ckgod.snowball.invest.ui.theme.getPhaseColor
import ckgod.snowball.invest.ui.theme.getPhaseBrush
import ckgod.snowball.invest.ui.theme.getPhaseTrackColor
import ckgod.snowball.invest.ui.theme.getProfitColor
import ckgod.snowball.invest.ui.extensions.toDisplayPrice
import ckgod.snowball.invest.ui.extensions.toDisplayProfit
import ckgod.snowball.invest.ui.extensions.toDisplayProfitRate
import ckgod.snowball.invest.ui.extensions.withFixedHeight
import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.InvestmentStatusResponse
import com.ckgod.snowball.model.TradePhase

@Composable
fun StockSummaryCard(
    data: InvestmentStatusResponse,
    modifier: Modifier = Modifier,
    currencyType: CurrencyType,
    exchangeRate: Double,
    onClick: () -> Unit,
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
            StockBasicInfo(
                ticker = data.ticker,
                fullName = data.fullName ?: data.ticker,
                currentPrice = data.currentPrice.toDisplayPrice(currencyType, exchangeRate),
                dailyChangeRate = data.dailyChangeRate.toDisplayProfitRate()
            )

            Spacer(modifier = Modifier.height(16.dp))

            StrategyProgress(
                tValue = data.tValue,
                totalDivision = data.totalDivision,
                phase = data.phase
            )

            Spacer(modifier = Modifier.height(16.dp))

            AccountProfit(
                avgPrice = data.avgPrice.toDisplayPrice(currencyType, exchangeRate),
                quantity = data.quantity,
                profitRate = data.profitRate.toDisplayProfitRate(),
                profitAmount = data.profitAmount.toDisplayProfit(currencyType, exchangeRate)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AccountHistory(
                oneTimeAmount = data.oneTimeAmount.toDisplayPrice(currencyType, exchangeRate),
                totalInvested = data.totalInvested.toDisplayPrice(currencyType, exchangeRate),
                realizedProfit = data.realizedProfit.toDisplayProfit(currencyType, exchangeRate)
            )
        }
    }
}


@Composable
private fun StockBasicInfo(
    ticker: String,
    fullName: String,
    currentPrice: String,
    dailyChangeRate: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = ticker,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = fullName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = currentPrice,
                style = MaterialTheme.typography.titleMedium.withFixedHeight(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = dailyChangeRate,
                style = MaterialTheme.typography.bodyMedium.withFixedHeight(),
                color = getProfitColor(dailyChangeRate),
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}


@Composable
private fun StrategyProgress(
    tValue: Double,
    totalDivision: Int,
    phase: TradePhase
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val progress = tValue.toFloat() / totalDivision.toFloat()
        GradientProgressIndicator(
            progress = progress,
            brush = getPhaseBrush(phase),
            trackColor = getPhaseTrackColor(phase),
            animationEnabled = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "T-Value: $tValue / $totalDivision",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = getPhaseColor(phase).copy(alpha = 0.2f)
            ) {
                Text(
                    text = phase.displayName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = getPhaseColor(phase),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
private fun AccountProfit(
    avgPrice: String,
    quantity: Int,
    profitRate: String,
    profitAmount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "평단 $avgPrice",
                style = MaterialTheme.typography.bodyMedium.withFixedHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = "보유 ${quantity}주",
                style = MaterialTheme.typography.bodyMedium.withFixedHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = profitRate,
                style = MaterialTheme.typography.headlineSmall.withFixedHeight(),
                color = getProfitColor(profitRate),
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = profitAmount,
                style = MaterialTheme.typography.bodyLarge.withFixedHeight(),
                color = getProfitColor(profitAmount),
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AccountHistory(
    oneTimeAmount: String,
    totalInvested: String,
    realizedProfit: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "1회 매수액 $oneTimeAmount",
                style = MaterialTheme.typography.bodyMedium.withFixedHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = "매수 누적 $totalInvested",
                style = MaterialTheme.typography.bodyMedium.withFixedHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "실현 손익: $realizedProfit",
                style = MaterialTheme.typography.bodyLarge.withFixedHeight(),
                color = getProfitColor(realizedProfit),
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
