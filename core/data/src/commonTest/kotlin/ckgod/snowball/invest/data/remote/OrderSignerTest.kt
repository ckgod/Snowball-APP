package ckgod.snowball.invest.data.remote

import kotlin.test.Test
import kotlin.test.assertEquals

class OrderSignerTest {

    /** 서버 OrderGuardTest 와 같은 벡터. 한쪽 규칙만 바뀌면 둘 중 하나가 깨진다. */
    @Test
    fun `서버와 같은 서명을 만든다`() {
        val signature = OrderSigner.sign(
            key = "test-key",
            timestamp = 1_700_000_000,
            method = "post",
            path = "/sb/orders",
            body = """{"ticker":"TQQQ"}"""
        )
        assertEquals("8bf571b847039a9eea44b43cb0007f65398eb3da753996d422c4f59fe98fce65", signature)
    }
}
