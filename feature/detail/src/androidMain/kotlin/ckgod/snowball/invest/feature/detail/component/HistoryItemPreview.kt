package ckgod.snowball.invest.feature.detail.component

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ckgod.snowball.invest.domain.model.HistoryItem
import ckgod.snowball.invest.ui.theme.SnowballTheme
import com.ckgod.snowball.model.OrderSide
import com.ckgod.snowball.model.OrderType
import com.ckgod.snowball.model.TradeStatus

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryItemFilledPreview() {
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
                    status = TradeStatus.FILLED
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryItemPendingPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "14:30",
                    orderNo = "ORD123457",
                    orderSide = OrderSide.SELL,
                    orderType = OrderType.LOC,
                    price = 155.25,
                    quantity = 5,
                    status = TradeStatus.PENDING
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryItemCanceledPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "14:30",
                    orderNo = "ORD123458",
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

@Preview(showBackground = true)
@Composable
fun HistoryItemPartialPreview() {
    SnowballTheme(darkTheme = true) {
        Surface {
            HistoryItemRow(
                item = HistoryItem(
                    dateTime = "20251231143000",
                    displayTime = "14:30",
                    orderNo = "ORD123459",
                    orderSide = OrderSide.SELL,
                    orderType = OrderType.LIMIT,
                    price = 152.00,
                    quantity = 20,
                    status = TradeStatus.PARTIAL
                )
            )
        }
    }
}
