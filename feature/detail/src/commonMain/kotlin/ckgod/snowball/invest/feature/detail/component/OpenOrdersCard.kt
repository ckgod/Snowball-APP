package ckgod.snowball.invest.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import ckgod.snowball.invest.ui.theme.getBuySideColor
import ckgod.snowball.invest.ui.theme.getSellSideColor
import com.ckgod.snowball.model.OpenOrderResponse
import com.ckgod.snowball.model.OrderSide
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_double_arrow_right

/**
 * 미체결 주문 카드.
 *
 * 목록에는 버튼을 두지 않고 행을 누르면 바텀시트에서 정정·취소를 고른다.
 * 실제 계좌 주문을 바꾸는 동작이라 한 화면에 버튼이 줄지어 있으면 잘못 누르기 쉽고,
 * 다른 카드들과 시각적 무게도 맞지 않는다.
 */
@Composable
fun OpenOrdersCard(
    orders: List<OpenOrderResponse>,
    isActionRunning: Boolean,
    onCancel: (orderNo: String) -> Unit,
    onModify: (orderNo: String, price: Double, quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<OpenOrderResponse?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "미체결 주문",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                CountBadge(orders.size)
            }

            Spacer(modifier = Modifier.height(8.dp))

            orders.forEach { order ->
                OpenOrderRow(
                    order = order,
                    enabled = !isActionRunning,
                    onClick = { selected = order }
                )
            }
        }
    }

    selected?.let { order ->
        OrderActionSheet(
            order = order,
            onDismiss = { selected = null },
            onCancel = {
                onCancel(order.orderNo)
                selected = null
            },
            onModify = { price, quantity ->
                onModify(order.orderNo, price, quantity)
                selected = null
            }
        )
    }
}

@Composable
private fun CountBadge(count: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    ) {
        Text(
            text = "$count",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OpenOrderRow(
    order: OpenOrderResponse,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SideLabel(order.orderSide)

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "$${order.orderPrice.formatPrice()}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = order.quantityText(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            painter = painterResource(Res.drawable.ic_double_arrow_right),
            contentDescription = "주문 관리",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SideLabel(side: OrderSide) {
    val color = if (side == OrderSide.BUY) getBuySideColor() else getSellSideColor()
    Text(
        text = side.displayName,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

private enum class SheetMode { Menu, Modify, ConfirmCancel }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderActionSheet(
    order: OpenOrderResponse,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onModify: (price: Double, quantity: Int) -> Unit
) {
    var mode by remember { mutableStateOf(SheetMode.Menu) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OrderSummary(order)

            when (mode) {
                SheetMode.Menu -> {
                    FilledTonalButton(onClick = { mode = SheetMode.Modify }, modifier = Modifier.fillMaxWidth()) {
                        Text("가격·수량 정정")
                    }
                    TextButton(
                        onClick = { mode = SheetMode.ConfirmCancel },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("주문 취소")
                    }
                }

                SheetMode.Modify -> ModifyForm(
                    order = order,
                    onBack = { mode = SheetMode.Menu },
                    onConfirm = onModify
                )

                SheetMode.ConfirmCancel -> {
                    Text(
                        text = "남은 ${order.unfilledQuantity}주를 취소할까요?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("취소 요청")
                    }
                    TextButton(onClick = { mode = SheetMode.Menu }, modifier = Modifier.fillMaxWidth()) {
                        Text("돌아가기")
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummary(order: OpenOrderResponse) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SideLabel(order.orderSide)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${order.ticker}  $${order.orderPrice.formatPrice()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${order.quantityText()} · 주문번호 ${order.orderNo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModifyForm(
    order: OpenOrderResponse,
    onBack: () -> Unit,
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

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("가격 (USD)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it },
            label = { Text("수량") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(0.6f)
        )
    }
    if (error != null) {
        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
    Button(
        onClick = { onConfirm(price!!, quantity!!) },
        enabled = error == null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("정정 요청")
    }
    TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
        Text("돌아가기")
    }
}

private fun OpenOrderResponse.quantityText(): String =
    if (filledQuantity > 0) "${unfilledQuantity}주 남음 (${filledQuantity}/${orderQuantity}주 체결)"
    else "${orderQuantity}주"

private fun Double.formatPrice(): String {
    val cents = kotlin.math.round(this * 100).toLong()
    val whole = cents / 100
    val fraction = (cents % 100).toString().padStart(2, '0')
    return "$whole.$fraction"
}
