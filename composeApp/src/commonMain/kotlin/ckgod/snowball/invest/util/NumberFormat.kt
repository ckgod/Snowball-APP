package ckgod.snowball.invest.util

import kotlin.math.roundToInt

/**
 * Double을 소수점 2자리 문자열로 변환
 */
fun Double.formatDecimal(decimalPlaces: Int = 2): String {
    val multiplier = when (decimalPlaces) {
        1 -> 10.0
        2 -> 100.0
        3 -> 1000.0
        else -> 100.0
    }
    val rounded = (this * multiplier).roundToInt() / multiplier

    // 정수 부분과 소수 부분 분리
    val intPart = rounded.toInt()
    val decPart = ((rounded - intPart) * multiplier).roundToInt()

    return when (decimalPlaces) {
        1 -> "$intPart.${decPart.toString().padStart(1, '0')}"
        2 -> "$intPart.${decPart.toString().padStart(2, '0')}"
        3 -> "$intPart.${decPart.toString().padStart(3, '0')}"
        else -> "$intPart.${decPart.toString().padStart(2, '0')}"
    }
}
