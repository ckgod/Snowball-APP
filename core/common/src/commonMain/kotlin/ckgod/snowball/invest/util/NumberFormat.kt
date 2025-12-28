package ckgod.snowball.invest.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

/**
 * Double을 소수점 N자리 문자열로 변환 (반올림)
 */
fun Double.formatDecimal(decimalPlaces: Int = 2): String {
    return BigDecimal.fromDouble(this)
        .roundToDigitPositionAfterDecimalPoint(
            decimalPlaces.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
        .toPlainString()
}
