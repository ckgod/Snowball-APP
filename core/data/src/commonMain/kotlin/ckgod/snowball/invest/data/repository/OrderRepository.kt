package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.ModifyOrderRequest
import com.ckgod.snowball.model.OpenOrdersResponse
import com.ckgod.snowball.model.OrderActionResponse
import com.ckgod.snowball.model.PlaceOrderRequest

/**
 * 신규 주문, 미체결 주문 조회·정정·취소
 */
interface OrderRepository {
    suspend fun getOpenOrders(): OpenOrdersResponse
    suspend fun place(request: PlaceOrderRequest): OrderActionResponse
    suspend fun cancel(orderNo: String): OrderActionResponse
    suspend fun modify(orderNo: String, request: ModifyOrderRequest): OrderActionResponse
}
