package ckgod.snowball.invest.feature.detail.component

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
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.ui.theme.getBuySideColor
import ckgod.snowball.invest.ui.theme.getSellSideColor
import ckgod.snowball.invest.util.formatDecimal
import com.ckgod.snowball.model.OrderSide
import com.ckgod.snowball.model.TradeStatus

/**
 * MTS 전문 거래 내역 리스트 아이템 (콤팩트 디자인)
 * - 아이콘 없음, 텍스트와 색상만 사용
 * - 고밀도 레이아웃 (패딩 및 행간 최소화)
 * - 매수: Red, 매도: Blue
 */
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
        // 좌측: 시간
        Text(
            text = item.displayTime,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(42.dp),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 중앙: 주문 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // 첫 번째 줄: 매수/매도 + 주문 타입 뱃지
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

                // 주문 타입 뱃지 (각진 디자인)
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

            // 두 번째 줄: 가격 정보
            PriceFlowText(item, sideColor)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 우측: 상태 뱃지
        CompactStatusBadge(item, sideColor)
    }
}

/**
 * 주문가 -> 체결가 흐름 표시
 * - FILLED/PARTIAL: 주문가 → 체결가
 * - PENDING: 주문가만
 * - CANCELED: 주문가만 (희미하게)
 */
@Composable
private fun PriceFlowText(item: HistoryItem, sideColor: Color) {
    when (item.status) {
        TradeStatus.FILLED, TradeStatus.PARTIAL -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 주문가
                Text(
                    text = "$${item.price.formatDecimal()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 화살표 아이콘
                Icon(
                    painter = painterResource(Res.drawable.ic_double_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                // 체결가 (강조)
                Text(
                    text = "$${item.filledPrice.formatDecimal()}",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp
                    ),
                    color = sideColor
                )

                // 수량
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
            // 주문가만 표시
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
            // 취소된 주문 (희미하게)
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

/**
 * 콤팩트 상태 뱃지
 * - 최소한의 크기
 * - 텍스트와 색상만 사용
 */
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