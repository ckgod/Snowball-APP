package ckgod.snowball.invest.feature.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.component.SectionCard
import ckgod.snowball.invest.ui.theme.getBuySideColor
import ckgod.snowball.invest.ui.theme.getSellSideColor
import com.ckgod.snowball.model.OpenOrderResponse
import com.ckgod.snowball.model.OrderSide

/**
 * 미체결 주문 목록과 정정·취소.
 *
 * 실제 계좌 주문을 바꾸는 동작이라 두 버튼 모두 확인 창을 한 번 거친다.
 */
@Composable
fun OpenOrdersCard(
    orders: List<OpenOrderResponse>,
    isActionRunning: Boolean,
    onCancel: (orderNo: String) -> Unit,
    onModify: (orderNo: String, price: Double, quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var cancelTarget by remember { mutableStateOf<OpenOrderResponse?>(null) }
    var modifyTarget by remember { mutableStateOf<OpenOrderResponse?>(null) }

    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "미체결 주문 ${orders.size}건",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            orders.forEachIndexed { index, order ->
                if (index > 0) HorizontalDivider()
                OpenOrderRow(
                    order = order,
                    enabled = !isActionRunning,
                    onCancelClick = { cancelTarget = order },
                    onModifyClick = { modifyTarget = order }
                )
            }
        }
    }

    cancelTarget?.let { order ->
        AlertDialog(
            onDismissRequest = { cancelTarget = null },
            title = { Text("주문 취소") },
            text = { Text("${order.describe()}\n남은 ${order.unfilledQuantity}주를 취소합니다.") },
            confirmButton = {
                TextButton(onClick = {
                    onCancel(order.orderNo)
                    cancelTarget = null
                }) { Text("취소 요청") }
            },
            dismissButton = {
                TextButton(onClick = { cancelTarget = null }) { Text("닫기") }
            }
        )
    }

    modifyTarget?.let { order ->
        ModifyOrderDialog(
            order = order,
            onDismiss = { modifyTarget = null },
            onConfirm = { price, quantity ->
                onModify(order.orderNo, price, quantity)
                modifyTarget = null
            }
        )
    }
}

@Composable
private fun OpenOrderRow(
    order: OpenOrderResponse,
    enabled: Boolean,
    onCancelClick: () -> Unit,
    onModifyClick: () -> Unit
) {
    val sideColor = if (order.orderSide == OrderSide.BUY) getBuySideColor() else getSellSideColor()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.orderSide.displayName,
                color = sideColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$${order.orderPrice.formatPrice()} · 미체결 ${order.unfilledQuantity}/${order.orderQuantity}주",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedButton(onClick = onModifyClick, enabled = enabled) { Text("정정") }
        OutlinedButton(onClick = onCancelClick, enabled = enabled) { Text("취소") }
    }
}

@Composable
private fun ModifyOrderDialog(
    order: OpenOrderResponse,
    onDismiss: () -> Unit,
    onConfirm: (price: Double, quantity: Int) -> Unit
) {
    var priceText by remember { mutableStateOf(order.orderPrice.formatPrice()) }
    var quantityText by remember { mutableStateOf(order.unfilledQuantity.toString()) }

    val price = priceText.toDoubleOrNull()
    val quantity = quantityText.toIntOrNull()
    val error = when {
        price == null || price <= 0.0 -> "가격을 확인해 주세요"
        quantity == null || quantity !in 1..order.unfilledQuantity -> "수량은 1 ~ ${order.unfilledQuantity}주"
        else -> null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("주문 정정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(order.describe(), style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("가격 (USD)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("수량") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(price!!, quantity!!) },
                enabled = error == null
            ) { Text("정정 요청") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("닫기") }
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

private fun OpenOrderResponse.describe(): String =
    "${orderSide.displayName} $${orderPrice.formatPrice()} × ${orderQuantity}주 (주문번호 $orderNo)"

private fun Double.formatPrice(): String {
    val cents = kotlin.math.round(this * 100).toLong()
    val whole = cents / 100
    val fraction = (cents % 100).toString().padStart(2, '0')
    return "$whole.$fraction"
}
