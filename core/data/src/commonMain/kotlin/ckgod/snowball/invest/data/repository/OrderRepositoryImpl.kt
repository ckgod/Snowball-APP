package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.ModifyOrderRequest
import com.ckgod.snowball.model.OpenOrdersResponse
import com.ckgod.snowball.model.OrderActionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * @param orderKey 정정·취소 요청에만 X-Order-Key 로 붙는다. 조회에는 쓰지 않는다.
 */
class OrderRepositoryImpl(
    private val httpClient: HttpClient,
    private val orderKey: String
) : OrderRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getOpenOrders(): OpenOrdersResponse {
        val response = httpClient.get("sb/orders/open")
        if (!response.status.isSuccess()) error(response.errorMessage())
        return response.body()
    }

    override suspend fun cancel(orderNo: String): OrderActionResponse =
        httpClient.post("sb/orders/$orderNo/cancel") {
            header(ORDER_KEY_HEADER, orderKey)
        }.toActionResponse()

    override suspend fun modify(orderNo: String, request: ModifyOrderRequest): OrderActionResponse =
        httpClient.post("sb/orders/$orderNo/modify") {
            header(ORDER_KEY_HEADER, orderKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toActionResponse()

    /**
     * 422 같은 업무 실패는 OrderActionResponse 로 오고,
     * 403·429·503 은 {"error": "..."} 로 온다. 둘 다 실패 결과로 바꿔 화면에 사유를 보여 준다.
     */
    private suspend fun HttpResponse.toActionResponse(): OrderActionResponse {
        val text = bodyAsText()
        return runCatching { json.decodeFromString<OrderActionResponse>(text) }
            .getOrElse { OrderActionResponse(success = false, message = errorMessage(text)) }
    }

    private suspend fun HttpResponse.errorMessage(text: String? = null): String {
        val body = text ?: bodyAsText()
        val error = runCatching { json.parseToJsonElement(body).jsonObject["error"]?.jsonPrimitive?.content }.getOrNull()
        return error ?: "요청 실패 (${status.value})"
    }

    private companion object {
        const val ORDER_KEY_HEADER = "X-Order-Key"
    }
}
