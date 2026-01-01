package ckgod.snowball.invest.feature.detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_close
import snowball.core.ui.generated.resources.ic_double_arrow_right
import snowball.core.ui.generated.resources.ic_drop_down
import snowball.core.ui.generated.resources.ic_drop_up
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.ui.theme.getBuySideColor
import ckgod.snowball.invest.ui.theme.getSellSideColor
import ckgod.snowball.invest.util.formatDecimal
import com.ckgod.snowball.model.OrderSide
import com.ckgod.snowball.model.TradeStatus

@Composable
fun HistoryItemRow(item: HistoryItem) {
    val sideColor = if (item.orderSide == OrderSide.BUY) {
        getBuySideColor()
    } else {
        getSellSideColor()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = item.displayTime,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(42.dp),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.orderSide.displayName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp
                    ),
                    color = sideColor
                )

                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = item.orderType.displayName,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        style = TextStyle(
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            PriceFlowText(item, sideColor)
        }

        Spacer(modifier = Modifier.width(8.dp))

        CompactStatusBadge(item, sideColor)
    }
}

@Composable
private fun PriceFlowText(item: HistoryItem, sideColor: Color) {
    when (item.status) {
        TradeStatus.FILLED, TradeStatus.PARTIAL -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${item.price.formatDecimal()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_double_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Text(
                    text = "$${item.filledPrice.formatDecimal()}",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp
                    ),
                    color = sideColor
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${item.filledQuantity}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        TradeStatus.PENDING -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${item.price.formatDecimal()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${item.quantity}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        TradeStatus.CANCELED -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${item.price.formatDecimal()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                Text(
                    text = "${item.quantity}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun CompactStatusBadge(item: HistoryItem, sideColor: Color) {
    val (text, color) = when (item.status) {
        TradeStatus.PENDING -> "주문" to MaterialTheme.colorScheme.onSurfaceVariant
        TradeStatus.FILLED -> "체결" to sideColor
        TradeStatus.CANCELED -> "취소" to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        TradeStatus.PARTIAL -> "부분" to sideColor
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}

@Composable
fun CrashProtectionAccordion(items: List<HistoryItem>) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = items.firstOrNull()?.displayTime ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(42.dp),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "방어 매수",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${items.size}건",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = TextStyle(
                                fontSize = 10.sp,
                                lineHeight = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val crashRates = items.mapNotNull { it.crashRate }.joinToString(", ") { "-${it}%" }

                Text(
                    text = crashRates,
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(if (isExpanded) Res.drawable.ic_drop_up else Res.drawable.ic_drop_down),
                contentDescription = if (isExpanded) "접기" else "펼치기",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                items.forEach { item ->
                    HistoryItemRow(item)
                }
            }
        }
    }
}
