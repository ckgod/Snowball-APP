package ckgod.snowball.invest.feature.backtest.component.chart

import com.ckgod.snowball.model.PriceData

enum class ChartViewMode(val label: String) {
    DAILY("일"),
    MONTHLY("월"),
    YEARLY("년")
}

data class CandleData(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val originalStartIndex: Int,
    val originalEndIndex: Int
)

fun aggregateCandles(prices: List<PriceData>, mode: ChartViewMode): List<CandleData> {
    if (prices.isEmpty()) return emptyList()

    return when (mode) {
        ChartViewMode.DAILY -> {
            prices.mapIndexed { index, price ->
                CandleData(
                    date = price.date,
                    open = price.open,
                    high = price.high,
                    low = price.low,
                    close = price.close,
                    originalStartIndex = index,
                    originalEndIndex = index
                )
            }
        }
        ChartViewMode.MONTHLY -> {
            prices.withIndex().groupBy { (_, price) ->
                price.date.substring(0, 7) // YYYY-MM
            }.map { (_, group) ->
                val items = group.map { it.value }
                val indices = group.map { it.index }
                CandleData(
                    date = items.last().date,
                    open = items.first().open,
                    high = items.maxOf { it.high },
                    low = items.minOf { it.low },
                    close = items.last().close,
                    originalStartIndex = indices.first(),
                    originalEndIndex = indices.last()
                )
            }
        }
        ChartViewMode.YEARLY -> {
            prices.withIndex().groupBy { (_, price) ->
                price.date.substring(0, 4) // YYYY
            }.map { (_, group) ->
                val items = group.map { it.value }
                val indices = group.map { it.index }
                CandleData(
                    date = items.last().date,
                    open = items.first().open,
                    high = items.maxOf { it.high },
                    low = items.minOf { it.low },
                    close = items.last().close,
                    originalStartIndex = indices.first(),
                    originalEndIndex = indices.last()
                )
            }
        }
    }
}

fun generateDateLabels(
    candles: List<CandleData>,
    mode: ChartViewMode,
    visibleStartIndex: Int,
    visibleEndIndex: Int,
    maxLabels: Int = 6
): List<Pair<Int, String>> {
    if (candles.isEmpty()) return emptyList()

    val start = visibleStartIndex.coerceIn(0, candles.lastIndex)
    val end = visibleEndIndex.coerceIn(start, candles.lastIndex)
    val visibleCount = end - start + 1

    if (visibleCount <= 1) {
        return listOf(start to formatDateLabel(candles[start].date, mode))
    }

    val step = (visibleCount / maxLabels).coerceAtLeast(1)
    val labels = mutableListOf<Pair<Int, String>>()

    var lastLabel = ""
    for (i in start..end step step) {
        val label = formatDateLabel(candles[i].date, mode)
        if (label != lastLabel) {
            labels.add(i to label)
            lastLabel = label
        }
    }

    return labels
}

private fun formatDateLabel(date: String, mode: ChartViewMode): String {
    return when (mode) {
        ChartViewMode.DAILY -> {
            // Show month: "1월", "2월" ...
            val month = date.substring(5, 7).toIntOrNull() ?: return date
            "${month}월"
        }
        ChartViewMode.MONTHLY -> {
            // Show year: "'21", "'22"
            "'${date.substring(2, 4)}"
        }
        ChartViewMode.YEARLY -> {
            date.substring(0, 4)
        }
    }
}

fun generatePriceLevels(minPrice: Double, maxPrice: Double, levels: Int = 4): List<Double> {
    if (maxPrice <= minPrice) return listOf(minPrice)

    val range = maxPrice - minPrice
    val step = range / (levels - 1)
    return (0 until levels).map { minPrice + step * it }
}
