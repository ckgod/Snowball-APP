package ckgod.snowball.invest.data.remote

import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * 주문 요청 서명. 서버 OrderGuard.sign 과 같은 규칙이어야 한다.
 *
 * payload = "timestamp\nMETHOD\npath\nbody", 결과는 소문자 hex.
 * 주문 키 자체는 네트워크로 나가지 않는다.
 */
object OrderSigner {
    fun sign(key: String, timestamp: Long, method: String, path: String, body: String): String {
        val mac = HmacSHA256(key.encodeToByteArray())
        val payload = "$timestamp\n${method.uppercase()}\n$path\n$body"
        return mac.doFinal(payload.encodeToByteArray()).toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { byte ->
        (byte.toInt() and 0xff).toString(16).padStart(2, '0')
    }
}
