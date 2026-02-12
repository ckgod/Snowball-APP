package ckgod.snowball.invest.feature.backtest.component.chart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

class InteractiveChartState(
    val totalDataPoints: Int,
    initialVisibleCount: Int = totalDataPoints
) {
    var viewportStart by mutableFloatStateOf(0f)
        private set
    var viewportEnd by mutableFloatStateOf(1f)
        private set

    var zoomLevel by mutableFloatStateOf(1f)
        private set

    init {
        if (totalDataPoints > initialVisibleCount && initialVisibleCount > 0) {
            val fraction = initialVisibleCount.toFloat() / totalDataPoints
            zoomLevel = (1f / fraction).coerceIn(1f, maxZoom)
            val visibleFrac = 1f / zoomLevel
            viewportStart = (1f - visibleFrac).coerceAtLeast(0f)
            viewportEnd = 1f
        }
    }

    // Selection as screen fractions (0.0 = left edge, 1.0 = right edge of visible area)
    var selectionStartFraction by mutableFloatStateOf(0.25f)
    var selectionEndFraction by mutableFloatStateOf(0.75f)

    val visibleFraction: Float get() = 1f / zoomLevel

    val maxZoom: Float get() = (totalDataPoints / 20f).coerceAtLeast(1f)

    val visibleStartIndex: Int
        get() = (viewportStart * (totalDataPoints - 1)).roundToInt().coerceIn(0, totalDataPoints - 1)

    val visibleEndIndex: Int
        get() = (viewportEnd * (totalDataPoints - 1)).roundToInt().coerceIn(0, totalDataPoints - 1)

    fun indexAtFraction(fraction: Float): Int {
        val visStart = visibleStartIndex
        val visEnd = visibleEndIndex
        val visRange = visEnd - visStart
        if (visRange <= 0) return visStart
        return (visStart + fraction * visRange).roundToInt().coerceIn(0, totalDataPoints - 1)
    }

    val selectionStartIndex: Int get() = indexAtFraction(selectionStartFraction)
    val selectionEndIndex: Int get() = indexAtFraction(selectionEndFraction)

    fun initSelectionFromDates(startDate: String, endDate: String, candles: List<CandleData>) {
        if (candles.isEmpty()) return

        val startIdx = candles.indexOfFirst { it.date >= startDate }.takeIf { it >= 0 } ?: 0
        val endIdx = candles.indexOfLast { it.date <= endDate }.takeIf { it >= 0 } ?: candles.lastIndex

        val visStart = visibleStartIndex
        val visEnd = visibleEndIndex
        val visRange = (visEnd - visStart).toFloat()

        if (visRange <= 0f) return

        val minGap = 1f / visRange
        selectionStartFraction = ((startIdx - visStart) / visRange).coerceIn(0f, 1f - minGap)
        selectionEndFraction = ((endIdx - visStart) / visRange).coerceIn(selectionStartFraction + minGap, 1f)
    }

    fun applyZoom(zoomDelta: Float, focalFraction: Float) {
        if (totalDataPoints < 20) return

        val newZoom = (zoomLevel * zoomDelta).coerceIn(1f, maxZoom)
        if (newZoom == zoomLevel) return

        val oldVisibleFraction = 1f / zoomLevel
        val newVisibleFraction = 1f / newZoom

        val focalPoint = viewportStart + focalFraction * oldVisibleFraction

        val newStart = (focalPoint - focalFraction * newVisibleFraction).coerceIn(0f, 1f - newVisibleFraction)
        val newEnd = (newStart + newVisibleFraction).coerceAtMost(1f)

        zoomLevel = newZoom
        viewportStart = newStart
        viewportEnd = newEnd
    }

    fun applyPan(panFraction: Float) {
        val visible = visibleFraction
        val newStart = (viewportStart + panFraction).coerceIn(0f, 1f - visible)
        val newEnd = (newStart + visible).coerceAtMost(1f)

        viewportStart = newStart
        viewportEnd = newEnd
    }
}
