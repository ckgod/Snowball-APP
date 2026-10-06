package ckgod.snowball.invest.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import com.ckgod.snowball.model.OrderSide
import com.ckgod.snowball.model.OrderType

/**
 * 신규 주문 시트. 입력 → 확인 두 단계로 나눠, 실제 주문이 나가기 직전에 내용을 한 번 더 보게 한다.
 *
 * 미국 주식은 시장가 매수가 없다(KIS). 매수는 지정가·LOC·LOO 만, 매도는 MOC·MOO 까지 고를 수 있다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderSheet(
    ticker: String,
    currentPrice: Double,
    holdingQuantity: Int,
    onDismiss: () -> Unit,
    onSubmit: (side: OrderSide, type: OrderType, price: Double, quantity: Int) -> Unit
) {
    var side by remember { mutableStateOf(OrderSide.BUY) }
    var type by remember { mutableStateOf(OrderType.LIMIT) }
    var priceText by remember { mutableStateOf(if (currentPrice > 0.0) currentPrice.formatUsd() else "") }
    var quantityText by remember { mutableStateOf("") }
    var confirming by remember { mutableStateOf(false) }

    val price = if (type.isMarket) 0.0 else priceText.toDoubleOrNull()
    val quantity = quantityText.toIntOrNull()
    val error = when {
        price == null || (!type.isMarket && price <= 0.0) -> "가격을 확인해 주세요"
        quantity == null || quantity <= 0 -> "수량을 입력해 주세요"
        else -> null
    }
    // 시장가 계열은 체결가를 모르므로 현재가로 어림한다
    val estimatedAmount = quantity?.let { (if (type.isMarket) currentPrice else price ?: 0.0) * it }

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
            Text(
                text = "$ticker 주문",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (!confirming) {
                SideSelector(
                    selected = side,
                    onSelect = {
                        side = it
                        if (!type.supports(it)) type = OrderType.LIMIT
                    }
                )

                TypeSelector(side = side, selected = type, onSelect = { type = it })

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!type.isMarket) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { input -> priceText = input.filter { it.isDigit() || it == '.' } },
                            label = { Text("가격 (USD)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { input -> quantityText = input.filter { it.isDigit() } },
                        label = { Text("수량") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(if (type.isMarket) 1f else 0.6f)
                    )
                }

                OrderHints(
                    side = side,
                    currentPrice = currentPrice,
                    holdingQuantity = holdingQuantity,
                    quantity = quantity,
                    estimatedAmount = estimatedAmount,
                    isMarket = type.isMarket
                )

                if (error != null && (priceText.isNotEmpty() || quantityText.isNotEmpty())) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = { confirming = true },
                    enabled = error == null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("다음")
                }
            } else {
                ConfirmStep(
                    side = side,
                    type = type,
                    price = price ?: 0.0,
                    quantity = quantity ?: 0,
                    estimatedAmount = estimatedAmount ?: 0.0,
                    onBack = { confirming = false },
                    onSubmit = { onSubmit(side, type, price ?: 0.0, quantity ?: 0) }
                )
            }
        }
    }
}

@Composable
private fun SideSelector(selected: OrderSide, onSelect: (OrderSide) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OrderSide.entries.forEach { side ->
            val color = side.color()
            val isSelected = side == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(side) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = side.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TypeSelector(side: OrderSide, selected: OrderType, onSelect: (OrderType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeOrder.filter { it.supports(side) }.forEach { type ->
                FilterChip(
                    selected = type == selected,
                    onClick = { onSelect(type) },
                    label = { Text(type.displayName) }
                )
            }
        }
        Text(
            text = selected.description(side),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OrderHints(
    side: OrderSide,
    currentPrice: Double,
    holdingQuantity: Int,
    quantity: Int?,
    estimatedAmount: Double?,
    isMarket: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        HintRow("현재가", "$${currentPrice.formatUsd()}")
        if (side == OrderSide.SELL) HintRow("보유 수량", "${holdingQuantity}주")
        if (estimatedAmount != null) {
            HintRow(if (isMarket) "예상 금액 (현재가 기준)" else "예상 금액", "$${estimatedAmount.formatUsd()}")
        }
        // 막지는 않는다. 계좌 잔고가 기준이고, 넘치면 KIS 가 거부해 내역에 사유가 남는다.
        if (side == OrderSide.SELL && quantity != null && quantity > holdingQuantity) {
            Text(
                text = "보유 수량보다 많습니다",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun HintRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ConfirmStep(
    side: OrderSide,
    type: OrderType,
    price: Double,
    quantity: Int,
    estimatedAmount: Double,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val color = side.color()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${side.displayName} · ${type.displayName}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        HintRow("가격", if (type.isMarket) "시장가 (${type.displayName})" else "$${price.formatUsd()}")
        HintRow("수량", "${quantity}주")
        HintRow(if (type.isMarket) "예상 금액 (현재가 기준)" else "주문 금액", "$${estimatedAmount.formatUsd()}")
    }
    Text(
        text = "실제 계좌로 주문이 나갑니다.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = MaterialTheme.colorScheme.surface)
    ) {
        Text("${side.displayName} 주문 전송")
    }
    TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
        Text("수정")
    }
}

/** 칩 순서. 자주 쓰는 지정가·LOC 를 앞에 둔다. */
private val TypeOrder = listOf(OrderType.LIMIT, OrderType.LOC, OrderType.LOO, OrderType.MOC, OrderType.MOO)

/** 체결가를 정하지 않는 유형 (매도 전용) */
private val OrderType.isMarket: Boolean get() = this == OrderType.MOC || this == OrderType.MOO

private fun OrderType.supports(side: OrderSide): Boolean = side == OrderSide.SELL || !isMarket

private fun OrderType.description(side: OrderSide): String {
    val better = if (side == OrderSide.BUY) "이하" else "이상"
    return when (this) {
        OrderType.LIMIT -> "지정한 가격 $better 이면 바로 체결됩니다"
        OrderType.LOC -> "장 마감 종가가 지정가 $better 이면 종가로 체결됩니다"
        OrderType.LOO -> "장 시작 시가가 지정가 $better 이면 시가로 체결됩니다"
        OrderType.MOC -> "가격과 상관없이 장 마감 종가로 체결됩니다"
        OrderType.MOO -> "가격과 상관없이 장 시작 시가로 체결됩니다"
    }
}

@Composable
private fun OrderSide.color() = if (this == OrderSide.BUY) getBuySideColor() else getSellSideColor()

private fun Double.formatUsd(): String {
    val cents = kotlin.math.round(this * 100).toLong()
    val whole = cents / 100
    val fraction = (cents % 100).toString().padStart(2, '0')
    return "$whole.$fraction"
}
