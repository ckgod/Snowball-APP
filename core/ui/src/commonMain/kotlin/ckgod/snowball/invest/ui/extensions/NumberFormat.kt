package ckgod.snowball.invest.ui.extensions

import com.ckgod.snowball.model.CurrencyType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlin.math.abs

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


fun Double.toDisplayPrice(
    currencyType: CurrencyType,
    exchangeRate: Double
): String {
    return when (currencyType) {
        CurrencyType.USD -> "$${this.formatDecimal()}"
        CurrencyType.KRW -> "${(this * exchangeRate).formatWithComma()}원"
    }
}

fun Double.toDisplayProfit(
    currencyType: CurrencyType,
    exchangeRate: Double
): String {
    val prefix = if (this > 0) "+" else if (this < 0) "-" else ""
    val absValue = abs(this)

    return when (currencyType) {
        CurrencyType.USD -> "$prefix$${absValue.formatDecimal()}"
        CurrencyType.KRW -> "$prefix${(absValue * exchangeRate).formatWithComma()}원"
    }
}

fun Double.toDisplayProfitRate(): String {
    val isProfit = this > 0
    return "${if (isProfit) "+" else ""}${this.formatDecimal()}%"
}

fun Double.toDisplayPercent(): String {
    return (this * 100).toInt().toString()
}