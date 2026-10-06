package ckgod.snowball.invest.data.repository

import ckgod.snowball.invest.data.remote.OrderSigner
import com.ckgod.snowball.model.ModifyOrderRequest
import com.ckgod.snowball.model.OpenOrdersResponse
import com.ckgod.snowball.model.OrderActionResponse
import com.ckgod.snowball.model.PlaceOrderRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @param orderKey 주문(신규·정정·취소) 요청 서명에만 쓴다. 헤더로 보내지 않는다.
 */
class OrderRepositoryImpl(
    private val httpClient: HttpClient,
    private val orderKey: String
) : OrderRepository {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    override suspend fun getOpenOrders(): OpenOrdersResponse {
        val response = httpClient.get("sb/orders/open")
        if (!response.status.isSuccess()) error(response.errorMessage())
        return response.body()
    }

    override suspend fun place(request: PlaceOrderRequest): OrderActionResponse =
        signedPost("sb/orders", json.encodeToString(request))

    override suspend fun cancel(orderNo: String): OrderActionResponse =
        signedPost("sb/orders/$orderNo/cancel", body = "")

    override suspend fun modify(orderNo: String, request: ModifyOrderRequest): OrderActionResponse =
        signedPost("sb/orders/$orderNo/modify", json.encodeToString(request))

    /**
     * 서명한 본문 문자열을 그대로 보낸다. 직렬화를 두 번 하면 공백·순서가 달라져 서명이 깨질 수 있다.
     * 요청마다 Idempotency-Key 를 새로 만든다 (같은 버튼 연타는 화면에서 막고, 서버는 재시도를 한 번만 처리).
     */
    @OptIn(ExperimentalUuidApi::class)
    private suspend fun signedPost(relativePath: String, body: String): OrderActionResponse {
        val timestamp = Clock.System.now().epochSeconds
        val signature = OrderSigner.sign(orderKey, timestamp, "POST", "/$relativePath", body)

        val response = httpClient.post(relativePath) {
            header(TIMESTAMP_HEADER, timestamp.toString())
            header(SIGNATURE_HEADER, signature)
            header(IDEMPOTENCY_HEADER, Uuid.random().toString())
            setBody(TextContent(body, ContentType.Application.Json))
        }
        return response.toActionResponse()
    }

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
        const val TIMESTAMP_HEADER = "X-Order-Timestamp"
        const val SIGNATURE_HEADER = "X-Order-Signature"
        const val IDEMPOTENCY_HEADER = "Idempotency-Key"
    }
}
