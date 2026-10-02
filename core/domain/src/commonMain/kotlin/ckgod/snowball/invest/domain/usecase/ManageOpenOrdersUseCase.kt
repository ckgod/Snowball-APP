package ckgod.snowball.invest.domain.usecase

import ckgod.snowball.invest.data.repository.OrderRepository
import com.ckgod.snowball.model.ModifyOrderRequest
import com.ckgod.snowball.model.OpenOrderResponse
import com.ckgod.snowball.model.OrderActionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * 종목 상세 화면의 미체결 주문 조회·정정·취소
 */
class ManageOpenOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend fun getOpenOrders(ticker: String): List<OpenOrderResponse> = withContext(Dispatchers.IO) {
        orderRepository.getOpenOrders().orders.filter { it.ticker == ticker }
    }

    suspend fun cancel(orderNo: String): OrderActionResponse = withContext(Dispatchers.IO) {
        runCatching { orderRepository.cancel(orderNo) }
            .getOrElse { OrderActionResponse(false, "취소 요청 실패: ${it.message}") }
    }

    suspend fun modify(orderNo: String, price: Double, quantity: Int?): OrderActionResponse = withContext(Dispatchers.IO) {
        runCatching { orderRepository.modify(orderNo, ModifyOrderRequest(price = price, quantity = quantity)) }
            .getOrElse { OrderActionResponse(false, "정정 요청 실패: ${it.message}") }
    }
}
