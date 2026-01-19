package ckgod.snowball.invest.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.theme.PortfolioColors

@Composable
fun InvestmentProgressBar(
    totalPlan: Double,
    investedCash: Double,
    evaluatedCash: Double,
    modifier: Modifier = Modifier,
    color: Color = PortfolioColors[0],
    stripeColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    barHeight: Dp = 48.dp,
    cornerRadius: Dp = 12.dp,
    remainText: String? = null,
    remainTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    remainTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val stripeWidth = with(density) { 8.dp.toPx() }
    val stripeSpacing = with(density) { 10.dp.toPx() }
    val borderWidth = with(density) { 1.dp.toPx() }
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }

    val investedRatio = if (totalPlan > 0) (investedCash / totalPlan).coerceIn(0.0, 1.0) else 0.0
    val evaluatedRatio = if (totalPlan > 0) (evaluatedCash / totalPlan).coerceIn(0.0, 1.0) else 0.0
    val remainRatio = if (totalPlan > 0) ((totalPlan - investedCash) / totalPlan).coerceIn(0.0, 1.0) else 0.0

    val isGain = evaluatedCash >= investedCash

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
    ) {
        val totalWidth = size.width
        val totalHeight = size.height

        val investedEndX = (totalWidth * investedRatio).toFloat()
        val evaluatedEndX = (totalWidth * evaluatedRatio).toFloat()
        val remainStartX = investedEndX
        val remainWidth = (totalWidth * remainRatio).toFloat()

        if (!isGain && evaluatedEndX < investedEndX) {
            drawRect(
                color = backgroundColor,
                topLeft = Offset(evaluatedEndX, 0f),
                size = Size(investedEndX - evaluatedEndX, totalHeight)
            )
        }

        if (remainWidth > 0) {
            drawRemainArea(
                startX = remainStartX,
                width = remainWidth,
                totalWidth = totalWidth,
                totalHeight = totalHeight,
                cornerRadius = cornerRadiusPx,
                backgroundColor = backgroundColor,
                stripeColor = stripeColor,
                borderColor = stripeColor,
                stripeWidth = stripeWidth,
                stripeSpacing = stripeSpacing,
                borderWidth = borderWidth,
                isFullWidth = investedRatio == 0.0
            )

            if (remainText != null) {
                drawRemainText(
                    text = remainText,
                    textMeasurer = textMeasurer,
                    textStyle = remainTextStyle,
                    textColor = remainTextColor,
                    startX = remainStartX,
                    width = remainWidth,
                    totalHeight = totalHeight
                )
            }
        }

        if (evaluatedEndX > 0) {
            drawEvaluatedArea(
                endX = evaluatedEndX,
                totalWidth = totalWidth,
                totalHeight = totalHeight,
                cornerRadius = cornerRadiusPx,
                color = color,
                coversFullWidth = remainWidth == 0f
            )
        }
    }
}

private fun DrawScope.drawEvaluatedArea(
    endX: Float,
    totalWidth: Float,
    totalHeight: Float,
    cornerRadius: Float,
    color: Color,
    coversFullWidth: Boolean
) {
    val path = Path().apply {
        moveTo(cornerRadius, 0f)

        if (coversFullWidth && endX >= totalWidth - cornerRadius) {
            lineTo(totalWidth - cornerRadius, 0f)
            quadraticTo(totalWidth, 0f, totalWidth, cornerRadius)
            lineTo(totalWidth, totalHeight - cornerRadius)
            quadraticTo(totalWidth, totalHeight, totalWidth - cornerRadius, totalHeight)
        } else {
            lineTo(endX, 0f)
            lineTo(endX, totalHeight)
        }

        lineTo(cornerRadius, totalHeight)
        quadraticTo(0f, totalHeight, 0f, totalHeight - cornerRadius)
        lineTo(0f, cornerRadius)
        quadraticTo(0f, 0f, cornerRadius, 0f)
        close()
    }

    drawPath(path = path, color = color.copy(0.8f))
}

private fun DrawScope.drawRemainArea(
    startX: Float,
    width: Float,
    totalWidth: Float,
    totalHeight: Float,
    cornerRadius: Float,
    backgroundColor: Color,
    stripeColor: Color,
    borderColor: Color,
    stripeWidth: Float,
    stripeSpacing: Float,
    borderWidth: Float,
    isFullWidth: Boolean
) {
    val endX = startX + width

    val remainPath = Path().apply {
        if (isFullWidth) {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = startX,
                    top = 0f,
                    right = endX.coerceAtMost(totalWidth),
                    bottom = totalHeight,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
        } else {
            moveTo(startX, 0f)

            if (endX >= totalWidth - cornerRadius) {
                lineTo(totalWidth - cornerRadius, 0f)
                quadraticTo(totalWidth, 0f, totalWidth, cornerRadius)
                lineTo(totalWidth, totalHeight - cornerRadius)
                quadraticTo(totalWidth, totalHeight, totalWidth - cornerRadius, totalHeight)
            } else {
                lineTo(endX, 0f)
                lineTo(endX, totalHeight)
            }

            lineTo(startX, totalHeight)
            close()
        }
    }

    clipPath(remainPath) {
        drawRect(
            color = backgroundColor,
            topLeft = Offset(startX, 0f),
            size = Size(width, totalHeight)
        )

        drawStripePattern(
            startX = startX,
            width = width,
            totalHeight = totalHeight,
            stripeColor = stripeColor,
            stripeWidth = stripeWidth,
            stripeSpacing = stripeSpacing
        )
    }

    drawPath(
        path = remainPath,
        color = borderColor,
        style = Stroke(width = borderWidth)
    )
}

private fun DrawScope.drawRemainText(
    text: String,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    textColor: Color,
    startX: Float,
    width: Float,
    totalHeight: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = textStyle.copy(color = textColor)
    )

    val textWidth = textLayoutResult.size.width
    val textHeight = textLayoutResult.size.height
    val padding = 8f

    if (textWidth + padding * 2 <= width) {
        val textX = startX + (width - textWidth) / 2
        val textY = (totalHeight - textHeight) / 2

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(textX, textY)
        )
    }
}

private fun DrawScope.drawStripePattern(
    startX: Float,
    width: Float,
    totalHeight: Float,
    stripeColor: Color,
    stripeWidth: Float,
    stripeSpacing: Float
) {
    val stripeStep = stripeWidth + stripeSpacing
    val startOffset = startX - totalHeight
    val endOffset = startX + width + totalHeight

    var x = startOffset
    while (x < endOffset) {
        drawLine(
            color = stripeColor,
            start = Offset(x, totalHeight),
            end = Offset(x + totalHeight, 0f),
            strokeWidth = stripeWidth
        )
        x += stripeStep
    }
}