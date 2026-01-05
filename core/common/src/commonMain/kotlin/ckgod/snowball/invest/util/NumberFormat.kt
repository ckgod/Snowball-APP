package ckgod.snowball.invest.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

fun Double.formatDecimal(decimalPlaces: Int = 2): String {
    return BigDecimal.fromDouble(this)
        .roundToDigitPositionAfterDecimalPoint(
            decimalPlaces.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
        .toPlainString()
}

fun Double.formatWithComma(): String {
    val rounded = BigDecimal.fromDouble(this)
        .roundToDigitPositionAfterDecimalPoint(
            0,
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
        .toPlainString()

    val integerPart = rounded.split(".")[0]

    return integerPart.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}
