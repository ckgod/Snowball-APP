package ckgod.snowball.invest.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressIndicator(
    progress: Float,
    color: Color,
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
        label = "progress_animation"
    )

    LaunchedEffect(progress) {
        if (animationEnabled) {
            animationPlayed = true
        }
    }

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius)),
        color = color,
        trackColor = trackColor,
    )
}
