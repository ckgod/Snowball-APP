package ckgod.snowball.invest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.theme.getProgressColor

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    corner: Shape = RoundedCornerShape(16.dp),
    content: @Composable (BoxScope.() -> Unit)
) {
    val progressColor = getProgressColor()
    val isDarkTheme = isSystemInDarkTheme()

    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(corner)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = corner
            )
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                scale(scaleX = 1.6f, scaleY = 1f, pivot = Offset.Zero) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                progressColor.copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            center = Offset.Zero,
                            radius = size.width * 0.6f
                        )
                    )
                }
            }
            .padding(20.dp)
    ) {
        content()
    }
}