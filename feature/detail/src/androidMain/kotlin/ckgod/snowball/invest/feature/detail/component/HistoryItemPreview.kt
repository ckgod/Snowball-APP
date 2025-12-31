package ckgod.snowball.invest.feature.detail.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.ui.theme.SnowballTheme
import com.ckgod.snowball.model.OrderSide
import com.ckgod.snowball.model.OrderType
import com.ckgod.snowball.model.TradeStatus

// 매수 체결
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryItemBuyFilledPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "14:30",
                    orderNo = "ORD123456",
                    orderSide = OrderSide.BUY,
                    orderType = OrderType.LIMIT,
                    price = 150.75,
                    quantity = 10,
                    status = TradeStatus.FILLED,
                    filledPrice = 150.80,
                    filledQuantity = 10
                )
            )
        }
    }
}

// 매도 체결
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryItemSellFilledPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231091500",
                    displayTime = "09:15",
                    orderNo = "ORD234567",
                    orderSide = OrderSide.SELL,
                    orderType = OrderType.LOC,
                    price = 155.25,
                    quantity = 20,
                    status = TradeStatus.FILLED,
                    filledPrice = 155.30,
                    filledQuantity = 20
                )
            )
        }
    }
}

// 미체결 주문 (매수)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryItemPendingBuyPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "15:45",
                    orderNo = "ORD345678",
                    orderSide = OrderSide.BUY,
                    orderType = OrderType.LOC,
                    price = 142.50,
                    quantity = 5,
                    status = TradeStatus.PENDING
                )
            )
        }
    }
}

// 취소된 주문 (매도)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryItemCanceledPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "13:20",
                    orderNo = "ORD456789",
                    orderSide = OrderSide.SELL,
                    orderType = OrderType.MOC,
                    price = 148.50,
                    quantity = 15,
                    status = TradeStatus.CANCELED
                )
            )
        }
    }
}

// 부분 체결 (매수)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryItemPartialBuyPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "11:05",
                    orderNo = "ORD567890",
                    orderSide = OrderSide.BUY,
                    orderType = OrderType.LIMIT,
                    price = 152.00,
                    quantity = 20,
                    status = TradeStatus.PARTIAL,
                    filledPrice = 152.05,
                    filledQuantity = 12
                )
            )
        }
    }
}

// 전체 케이스 모음 (다크 모드)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, heightDp = 400)
@Composable
fun HistoryItemAllCasesPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            Column {
                HistoryItemRow(
                    item = HistoryItem(
                        dateTime = "20251231143000",
                        displayTime = "14:30",
                        orderNo = "ORD1",
                        orderSide = OrderSide.BUY,
                        orderType = OrderType.LIMIT,
                        price = 150.75,
                        quantity = 10,
                        status = TradeStatus.FILLED,
                        filledPrice = 150.80,
                        filledQuantity = 10
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                HistoryItemRow(
                    item = HistoryItem(
                        dateTime = "20251231091500",
                        displayTime = "09:15",
                        orderNo = "ORD2",
                        orderSide = OrderSide.SELL,
                        orderType = OrderType.LOC,
                        price = 155.25,
                        quantity = 20,
                        status = TradeStatus.FILLED,
                        filledPrice = 155.30,
                        filledQuantity = 20
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                HistoryItemRow(
                    item = HistoryItem(
                        dateTime = "20251231143000",
                        displayTime = "15:45",
                        orderNo = "ORD3",
                        orderSide = OrderSide.BUY,
                        orderType = OrderType.LOC,
                        price = 142.50,
                        quantity = 5,
                        status = TradeStatus.PENDING
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                HistoryItemRow(
                    item = HistoryItem(
                        dateTime = "20251231143000",
                        displayTime = "13:20",
                        orderNo = "ORD4",
                        orderSide = OrderSide.SELL,
                        orderType = OrderType.MOC,
                        price = 148.50,
                        quantity = 15,
                        status = TradeStatus.CANCELED
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                HistoryItemRow(
                    item = HistoryItem(
                        dateTime = "20251231143000",
                        displayTime = "11:05",
                        orderNo = "ORD5",
                        orderSide = OrderSide.BUY,
                        orderType = OrderType.LIMIT,
                        price = 152.00,
                        quantity = 20,
                        status = TradeStatus.PARTIAL,
                        filledPrice = 152.05,
                        filledQuantity = 12
                    )
                )
            }
        }
    }
}
