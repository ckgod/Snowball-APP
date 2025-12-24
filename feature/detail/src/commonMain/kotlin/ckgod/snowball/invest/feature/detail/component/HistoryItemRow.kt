package ckgod.snowball.invest.feature.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.domain.model.TradeStatus
import ckgod.snowball.invest.domain.model.TradeType
import ckgod.snowball.invest.ui.theme.getProfitColor
import ckgod.snowball.invest.ui.theme.getProgressColor
import ckgod.snowball.invest.util.formatDecimal

@Composable
fun HistoryItemRow(item: HistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.displayTime,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 메인 정보
        Column(modifier = Modifier.weight(1f)) {
            when (item) {
                is HistoryItem.Trade -> {
                    Text(
                        text = "${item.orderType.displayName} ${item.type.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$${item.price.formatDecimal()} × ${item.quantity}ea",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                is HistoryItem.Sync -> {
                    Text(
                        text = "일일 정산",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "실현 손익 ${if (item.profit >= 0) "+" else ""}$${item.profit.formatDecimal()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = getProfitColor(item.profit)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 상태 뱃지
        StatusBadge(item)
    }
}

/**
 * 상태 뱃지
 */
@Composable
private fun StatusBadge(item: HistoryItem) {
    val (text, color) = when (item) {
        is HistoryItem.Trade -> {
            when (item.status) {
                TradeStatus.PENDING -> "주문" to MaterialTheme.colorScheme.onSurfaceVariant
                TradeStatus.FILLED -> {
                    val tradeColor = when (item.type) {
                        TradeType.BUY -> getProgressColor()
                        TradeType.SELL -> getProfitColor(-1.0) // Red for sell
                    }
                    "체결" to tradeColor
                }
                TradeStatus.CANCELED -> "취소" to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        }
        is HistoryItem.Sync -> "정산" to getProgressColor()
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
