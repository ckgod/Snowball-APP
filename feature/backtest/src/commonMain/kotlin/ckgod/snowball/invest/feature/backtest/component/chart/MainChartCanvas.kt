package ckgod.snowball.invest.feature.backtest.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ckgod.snowball.invest.ui.extensions.formatDecimal

@Composable
fun MainChartCanvas(
    candles: List<CandleData>,
    chartState: InteractiveChartState,
    viewMode: ChartViewMode,
    accentColor: Color,
    bullColor: Color,
    bearColor: Color,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 240.dp
) {
    if (candles.isEmpty()) return

    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
    val chartBgColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val neutralColor = MaterialTheme.colorScheme.onSurfaceVariant

    val visibleStart = chartState.visibleStartIndex.coerceIn(0, candles.lastIndex)
    val visibleEnd = chartState.visibleEndIndex.coerceIn(visibleStart, candles.lastIndex)
    val visibleCount = visibleEnd - visibleStart + 1

    val visibleCandles = remember(candles, visibleStart, visibleEnd) {
        candles.subList(visibleStart, (visibleEnd + 1).coerceAtMost(candles.size))
    }

    val priceMin = remember(visibleCandles) { visibleCandles.minOfOrNull { it.low } ?: 0.0 }
    val priceMax = remember(visibleCandles) { visibleCandles.maxOfOrNull { it.high } ?: 1.0 }

    val pricePadding = (priceMax - priceMin) * 0.1
    val adjustedMin = priceMin - pricePadding
    val adjustedMax = priceMax + pricePadding
    val priceRange = adjustedMax - adjustedMin

    val priceLevels = remember(adjustedMin, adjustedMax) {
        generatePriceLevels(adjustedMin, adjustedMax, 4)
    }

    val dateLabels = remember(candles, visibleStart, visibleEnd, viewMode) {
        generateDateLabels(candles, viewMode, visibleStart, visibleEnd)
    }

    val density = LocalDensity.current

    var canvasWidthPxState by remember { mutableFloatStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        chartState.applyZoom(zoomChange, 0.5f)
        if (canvasWidthPxState > 0) {
            chartState.applyPan(-panChange.x / canvasWidthPxState * chartState.visibleFraction)
        }
    }

    val startFraction = chartState.selectionStartFraction
    val endFraction = chartState.selectionEndFraction

    Column(modifier = modifier) {
        // Chart area
        Row(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(chartHeight)
                    .transformable(state = transformableState)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                canvasWidthPxState = canvasWidth

                val topPad = 8f
                val bottomPad = 8f
                val drawHeight = canvasHeight - topPad - bottomPad

                if (visibleCount == 0 || priceRange <= 0) return@Canvas

                // Chart background
                drawRect(color = chartBgColor, topLeft = Offset.Zero, size = size)
                drawRect(color = borderColor, topLeft = Offset.Zero, size = size, style = Stroke(width = with(density) { 1.dp.toPx() }))

                // Grid lines
                priceLevels.forEach { price ->
                    val y = topPad + drawHeight - ((price - adjustedMin) / priceRange * drawHeight).toFloat()
                    drawLine(gridColor, Offset(0f, y), Offset(canvasWidth, y), strokeWidth = with(density) { 0.5.dp.toPx() })
                }

                // Selection highlight
                val selLeftX = startFraction * canvasWidth
                val selRightX = endFraction * canvasWidth
                if (selRightX > selLeftX) {
                    drawRect(
                        color = accentColor.copy(alpha = 0.08f),
                        topLeft = Offset(selLeftX, 0f),
                        size = Size(selRightX - selLeftX, canvasHeight)
                    )
                    // Vertical edge lines
                    val edgeStroke = with(density) { 1.dp.toPx() }
                    drawLine(accentColor.copy(alpha = 0.3f), Offset(selLeftX, 0f), Offset(selLeftX, canvasHeight), edgeStroke)
                    drawLine(accentColor.copy(alpha = 0.3f), Offset(selRightX, 0f), Offset(selRightX, canvasHeight), edgeStroke)
                }

                // Candlesticks
                val candleSpacing = canvasWidth / visibleCount
                val candleWidth = (candleSpacing * 0.7f).coerceIn(
                    with(density) { 2.dp.toPx() },
                    with(density) { 20.dp.toPx() }
                )
                val wickWidth = with(density) { 1.5.dp.toPx() }

                visibleCandles.forEachIndexed { localIndex, candle ->
                    val centerX = candleSpacing * (localIndex + 0.5f)
                    val color = when {
                        candle.close > candle.open -> bullColor
                        candle.close < candle.open -> bearColor
                        else -> neutralColor
                    }
                    drawCandle(candle, centerX, candleWidth, wickWidth, topPad, drawHeight, adjustedMin, priceRange, color)
                }
            }

            // Y-axis price labels
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .height(chartHeight)
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                priceLevels.reversed().forEach { price ->
                    Text(
                        text = price.formatDecimal(1),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = labelColor,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // X-axis date labels
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 56.dp)
        ) {
            val chartWidth = maxWidth
            if (visibleCount > 0) {
                dateLabels.forEach { (index, label) ->
                    val fraction = (index - visibleStart + 0.5f) / visibleCount
                    if (fraction in 0f..1f) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = labelColor,
                            modifier = Modifier.offset(x = chartWidth * fraction - 12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selection range bar (below chart)
        SelectionRangeBar(
            chartState = chartState,
            visibleCount = visibleCount,
            accentColor = accentColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 56.dp)
        )

        // Selection date labels
        if (candles.isNotEmpty()) {
            val startDate = candles.getOrNull(chartState.selectionStartIndex)?.date ?: ""
            val endDate = candles.getOrNull(chartState.selectionEndIndex)?.date ?: ""
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 56.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = startDate, style = MaterialTheme.typography.labelSmall, color = accentColor)
                Text(text = endDate, style = MaterialTheme.typography.labelSmall, color = accentColor)
            }
        }
    }
}

@Composable
private fun SelectionRangeBar(
    chartState: InteractiveChartState,
    visibleCount: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
    val handleWidth = 12.dp
    val handleTouchWidth = 48.dp
    val barHeight = 32.dp
    val minGap = if (visibleCount > 0) 1f / visibleCount else 0.02f

    val startFraction = chartState.selectionStartFraction
    val endFraction = chartState.selectionEndFraction

    BoxWithConstraints(
        modifier = modifier
            .height(barHeight)
            .clip(RoundedCornerShape(6.dp))
            .background(trackColor)
    ) {
        val barWidthPx = with(density) { maxWidth.toPx() }
        val barWidth = maxWidth

        // Selected range fill
        Canvas(modifier = Modifier.matchParentSize()) {
            val leftX = startFraction * size.width
            val rightX = endFraction * size.width
            if (rightX > leftX) {
                drawRect(
                    color = accentColor.copy(alpha = 0.15f),
                    topLeft = Offset(leftX, 0f),
                    size = Size(rightX - leftX, size.height)
                )
            }
        }

        // Start handle
        val startHandleX = barWidth * startFraction
        Box(
            modifier = Modifier
                .offset(x = startHandleX - handleTouchWidth / 2)
                .width(handleTouchWidth)
                .height(barHeight)
                .pointerInput(barWidthPx, chartState, minGap) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val delta = dragAmount / barWidthPx
                            chartState.selectionStartFraction = (chartState.selectionStartFraction + delta)
                                .coerceIn(0f, chartState.selectionEndFraction - minGap)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(handleWidth)
                    .height(20.dp)
                    .background(accentColor, RoundedCornerShape(3.dp))
            )
        }

        // End handle
        val endHandleX = barWidth * endFraction
        Box(
            modifier = Modifier
                .offset(x = endHandleX - handleTouchWidth / 2)
                .width(handleTouchWidth)
                .height(barHeight)
                .pointerInput(barWidthPx, chartState, minGap) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val delta = dragAmount / barWidthPx
                            chartState.selectionEndFraction = (chartState.selectionEndFraction + delta)
                                .coerceIn(chartState.selectionStartFraction + minGap, 1f)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(handleWidth)
                    .height(20.dp)
                    .background(accentColor, RoundedCornerShape(3.dp))
            )
        }
    }
}

private fun DrawScope.drawCandle(
    candle: CandleData,
    centerX: Float,
    candleWidth: Float,
    wickWidth: Float,
    topPad: Float,
    drawHeight: Float,
    adjustedMin: Double,
    priceRange: Double,
    color: Color
) {
    fun priceToY(price: Double): Float =
        topPad + drawHeight - ((price - adjustedMin) / priceRange * drawHeight).toFloat()

    drawLine(color, Offset(centerX, priceToY(candle.high)), Offset(centerX, priceToY(candle.low)), wickWidth)

    val bodyTop = minOf(priceToY(candle.open), priceToY(candle.close))
    val bodyBottom = maxOf(priceToY(candle.open), priceToY(candle.close))
    drawRect(color, Offset(centerX - candleWidth / 2, bodyTop), Size(candleWidth, (bodyBottom - bodyTop).coerceAtLeast(1f)))
}
