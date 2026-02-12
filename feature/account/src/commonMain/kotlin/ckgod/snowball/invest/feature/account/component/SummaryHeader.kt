package ckgod.snowball.invest.feature.account.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.component.BouncySlidingText
import ckgod.snowball.invest.ui.component.SectionCard
import ckgod.snowball.invest.ui.extensions.toDisplayPrice
import ckgod.snowball.invest.ui.extensions.toDisplayProfit
import ckgod.snowball.invest.ui.extensions.toDisplayProfitRate
import ckgod.snowball.invest.ui.theme.getProfitColor
import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.TotalAssetResponse
import com.ckgod.snowball.model.orderableCashUsd
import com.ckgod.snowball.model.totalAssetValueUsd
import com.ckgod.snowball.model.totalEvalValueUsd
import com.ckgod.snowball.model.totalProfitRate
import com.ckgod.snowball.model.totalProfitUsd
import com.ckgod.snowball.model.rpAmountUsd
import com.ckgod.snowball.model.totalRPProfitUsd
import com.ckgod.snowball.model.totalStockProfitUsd

@Composable
fun SummaryHeader(
    data: TotalAssetResponse,
    currencyType: CurrencyType = CurrencyType.USD,
    exchangeRate: Double = 1400.0
) {
    SectionCard {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 자산",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            BouncySlidingText(
                text = data.totalAssetValueUsd.toDisplayPrice(currencyType, exchangeRate),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.totalProfitUsd.toDisplayProfit(currencyType, exchangeRate),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = getProfitColor(data.totalProfitUsd)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${data.totalProfitRate.toDisplayProfitRate()})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = getProfitColor(data.totalProfitUsd)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f, fill = true),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "주식",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = data.totalEvalValueUsd.toDisplayPrice(currencyType = currencyType, exchangeRate = exchangeRate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "(${data.totalStockProfitUsd.toDisplayProfit(currencyType, exchangeRate)})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = getProfitColor(data.totalStockProfitUsd)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f, fill = true),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "현금",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.orderableCashUsd.toDisplayPrice(currencyType, exchangeRate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(
                    modifier = Modifier.weight(1f, fill = true),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "외화 RP",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = data.rpAmountUsd.toDisplayPrice(currencyType, exchangeRate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "(${data.totalRPProfitUsd.toDisplayProfit(currencyType, exchangeRate)})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = getProfitColor(data.totalRPProfitUsd)
                        )
                    }
                }
            }
        }
    }
}
