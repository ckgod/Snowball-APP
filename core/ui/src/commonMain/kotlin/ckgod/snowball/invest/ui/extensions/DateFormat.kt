package ckgod.snowball.invest.ui.extensions

/**
 * "2025-12-22T18:09:07" or "2025-12-22T18:09"
 * ->
 * 2025.12.22
 */
fun String.toDisplayDate(): String {
    return this.split("T")[0].replace("-", ".")
}

/**
 * "2025-12-22T18:09:07" or "2025-12-22T18:09"
 * ->
 * 18:09
 */
fun String.toDisplayTime(): String {
    return this.split("T")[1].take(5)
}