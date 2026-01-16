package ckgod.snowball.invest.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InvestmentProgressBar(
    totalPlan: Double,
    investedCash: Double,
    currentValue: Double,
    remainCash: Double,
    modifier: Modifier = Modifier,
    barHeight: Dp = 48.dp,
    cornerRadius: Dp = 12.dp
) {
    val profitColor = Color(0xFFFF4757)          // 수익 - 빨간색
    val lossColor = Color(0xFF3B82F6)            // 손실 - 파란색
    val emptySpaceColor = Color(0xFF1A1A2E)      // 빈 공간 (손실 영역) - 어두운 배경
    val ammoBackgroundColor = Color(0x4D1E2337)  // 빗금 배경 (alpha 30%)
    val ammoStripeColor = Color(0x992D3A5C)      // 빗금 라인 (alpha 60%)
    val ammoBorderColor = Color(0x992D3A5C)      // 빗금 테두리 (라인과 동일)

    val density = LocalDensity.current

    val stripeWidth = with(density) { 10.dp.toPx() }
    val stripeSpacing = with(density) { 8.dp.toPx() }
    val borderWidth = with(density) { 1.dp.toPx() }
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }

    val costRatio = if (totalPlan > 0) (investedCash / totalPlan).coerceIn(0.0, 1.0) else 0.0
    val currentRatio = if (totalPlan > 0) (currentValue / totalPlan).coerceIn(0.0, 1.0) else 0.0
    val ammoRatio = if (totalPlan > 0) (remainCash / totalPlan).coerceIn(0.0, 1.0) else 0.0

    val isProfit = currentValue >= investedCash
    val valueColor = if (isProfit) profitColor else lossColor

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
    ) {
        val totalWidth = size.width
        val totalHeight = size.height

        val costEndX = (totalWidth * costRatio).toFloat()
        val currentEndX = (totalWidth * currentRatio).toFloat()
        val ammoStartX = costEndX
        val ammoWidth = (totalWidth * ammoRatio).toFloat()

        if (!isProfit && currentEndX < costEndX) {
            drawRect(
                color = emptySpaceColor,
                topLeft = Offset(currentEndX, 0f),
                size = Size(costEndX - currentEndX, totalHeight)
            )
        }

        if (currentEndX > 0) {
            drawCurrentValueArea(
                endX = currentEndX,
                totalWidth = totalWidth,
                totalHeight = totalHeight,
                cornerRadius = cornerRadiusPx,
                color = valueColor,
                coversFullWidth = ammoWidth == 0f
            )
        }

        if (ammoWidth > 0) {
            drawAmmoArea(
                startX = ammoStartX,
                width = ammoWidth,
                totalWidth = totalWidth,
                totalHeight = totalHeight,
                cornerRadius = cornerRadiusPx,
                backgroundColor = ammoBackgroundColor,
                stripeColor = ammoStripeColor,
                borderColor = ammoBorderColor,
                stripeWidth = stripeWidth,
                stripeSpacing = stripeSpacing,
                borderWidth = borderWidth,
                isFullWidth = costRatio == 0.0
            )
        }
    }
}

private fun DrawScope.drawCurrentValueArea(
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
            quadraticBezierTo(totalWidth, 0f, totalWidth, cornerRadius)
            lineTo(totalWidth, totalHeight - cornerRadius)
            quadraticBezierTo(totalWidth, totalHeight, totalWidth - cornerRadius, totalHeight)
        } else {
            lineTo(endX, 0f)
            lineTo(endX, totalHeight)
        }

        lineTo(cornerRadius, totalHeight)
        quadraticBezierTo(0f, totalHeight, 0f, totalHeight - cornerRadius)
        lineTo(0f, cornerRadius)
        quadraticBezierTo(0f, 0f, cornerRadius, 0f)
        close()
    }

    drawPath(path = path, color = color)
}

private fun DrawScope.drawAmmoArea(
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

    val ammoPath = Path().apply {
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
                quadraticBezierTo(totalWidth, 0f, totalWidth, cornerRadius)
                lineTo(totalWidth, totalHeight - cornerRadius)
                quadraticBezierTo(totalWidth, totalHeight, totalWidth - cornerRadius, totalHeight)
            } else {
                lineTo(endX, 0f)
                lineTo(endX, totalHeight)
            }

            lineTo(startX, totalHeight)
            close()
        }
    }

    clipPath(ammoPath) {
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
        path = ammoPath,
        color = borderColor,
        style = Stroke(width = borderWidth)
    )
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