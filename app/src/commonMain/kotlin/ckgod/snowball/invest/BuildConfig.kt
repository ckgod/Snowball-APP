package ckgod.snowball.invest

/**
 * 앱 설정 정보
 * 플랫폼별로 구현됨 (expect/actual 패턴)
 */
expect object AppConfig {
    val API_BASE_URL: String
    val API_KEY: String
    /** 정정·취소용 키 (서버 ORDER_API_KEY). 비어 있으면 주문 버튼이 서버에서 거절된다. */
    val ORDER_API_KEY: String
}
