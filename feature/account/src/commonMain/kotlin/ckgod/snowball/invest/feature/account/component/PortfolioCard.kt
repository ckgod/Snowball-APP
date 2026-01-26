package ckgod.snowball.invest.feature.account.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.component.InvestmentProgressBar
import ckgod.snowball.invest.ui.extensions.formatDecimal
import ckgod.snowball.invest.ui.extensions.toDisplayPercent
import ckgod.snowball.invest.ui.extensions.toDisplayPrice
import ckgod.snowball.invest.ui.theme.PortfolioColors
import com.ckgod.snowball.model.TotalAssetResponse
import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.HoldingStockResponse
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_pie_chart

@Composable
fun PortfolioCard(
    modifier: Modifier = Modifier,
    data: TotalAssetResponse,
    currencyType: CurrencyType = CurrencyType.USD,
    exchangeRate: Double = 1400.0
) {
    val totalCapital = remember(data.holdingStocks) {
        data.holdingStocks.sumOf { it.capital }
    }

    val stockRatios = remember(data.holdingStocks, totalCapital) {
        if (totalCapital > 0) {
            data.holdingStocks.mapIndexed { index, stock ->
                StockRatio(
                    stock = stock,
                    ratio = (stock.capital / totalCapital * 100).toInt(),
                    color = PortfolioColors[index % PortfolioColors.size]
                )
            }.sortedByDescending { it.ratio }
        } else {
            emptyList()
        }
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_pie_chart),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "포트폴리오 배분",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "총 계획 $${totalCapital.formatDecimal()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "전체 자산 배분 (계획 기준)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (stockRatios.isNotEmpty()) {
                PortfolioRatioBar(stockRatios = stockRatios)
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stockRatios.forEach { stockRatio ->
                    LegendItem(
                        color = stockRatio.color,
                        ticker = stockRatio.stock.ticker,
                        ratio = stockRatio.ratio
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "종목별 계획 진행률",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                stockRatios.forEach {
                    StockStatusBar(
                        data = it,
                        currencyType = currencyType,
                        exchangeRate = exchangeRate
                    )
                }
            }
        }
    }


}

@Composable
private fun PortfolioRatioBar(
    stockRatios: List<StockRatio>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
    ) {
        stockRatios.forEach { stockRatio ->
            Box(
                modifier = Modifier
                    .weight(stockRatio.ratio.toFloat().coerceAtLeast(1f))
                    .height(48.dp)
                    .background(stockRatio.color),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = stockRatio.stock.ticker,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${stockRatio.ratio}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    ticker: String,
    ratio: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$ticker (${ratio}%)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StockStatusBar(
    data: StockRatio,
    currencyType: CurrencyType,
    exchangeRate: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = data.color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = data.stock.ticker,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${data.stock.investedAmount.toDisplayPrice(currencyType, exchangeRate)} / ${data.stock.capital.toDisplayPrice(currencyType, exchangeRate)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                modifier = Modifier.weight(1f),
                text = "${(data.stock.investedAmount / data.stock.capital).toDisplayPercent()}% 투입",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                fontWeight = SemiBold,
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        InvestmentProgressBar(
            totalPlan = data.stock.capital,
            investedCash = data.stock.investedAmount,
            evaluatedCash = data.stock.currentPrice * data.stock.quantity,
            color = data.color,
            barHeight = 20.dp,
            cornerRadius = 4.dp,
            remainText = (data.stock.capital - data.stock.investedAmount).toDisplayPrice(currencyType, exchangeRate)
        )
    }
}

private data class StockRatio(
    val stock: HoldingStockResponse,
    val ratio: Int,
    val color: Color
)