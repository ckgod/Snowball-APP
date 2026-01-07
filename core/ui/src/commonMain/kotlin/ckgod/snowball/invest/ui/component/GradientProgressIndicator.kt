package ckgod.snowball.invest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientProgressIndicator(
    progress: Float,
    brush: Brush,
    trackColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    cornerRadius: Dp = 4.dp,
    animationEnabled: Boolean = true,
    animationKey: Any = "default"
) {
    var animationPlayed by rememberSaveable(animationKey) { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed && animationEnabled) progress else if (animationEnabled) 0f else progress,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100
        ),
        label = "gradient_progress_animation"
    )

    LaunchedEffect(progress) {
        if (animationEnabled) {
            animationPlayed = true
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = cornerRadius.toPx()

        drawRoundRect(
            color = trackColor,
            topLeft = Offset.Zero,
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = CornerRadius(radius, radius)
        )

        if (animatedProgress > 0f) {
            val progressWidth = canvasWidth * animatedProgress.coerceIn(0f, 1f)
            drawRoundRect(
                brush = brush,
                topLeft = Offset.Zero,
                size = Size(progressWidth, canvasHeight),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}
