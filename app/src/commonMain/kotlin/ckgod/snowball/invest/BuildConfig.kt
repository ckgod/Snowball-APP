package ckgod.snowball.invest

/**
 * 앱 설정 정보
 * 플랫폼별로 구현됨 (expect/actual 패턴)
 */
expect object AppConfig {
    val API_BASE_URL: String
    val API_KEY: String
}
