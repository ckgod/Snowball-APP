package ckgod.snowball.invest.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import kotlin.math.abs

@Composable
fun BouncySlidingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalTextStyle.current.color
) {
    Row(modifier = modifier) {
        val length = text.length
        val centerIndex = length / 2f

        text.forEachIndexed { index, char ->
            key(index) {
                val isShort = length <= 2
                val isEdge = index == 0 || index == length - 1
                val shouldAnimate = isShort || !isEdge

                if (shouldAnimate) {
                    var charId by remember { mutableLongStateOf(0L) }
                    var lastChar by remember { mutableStateOf(char) }

                    if (lastChar != char) {
                        lastChar = char
                        charId++
                    }

                    val distFromCenter = abs(index - centerIndex)
                    val dynamicStiffness = 200f + (distFromCenter * 150f)

                    AnimatedContent(
                        targetState = char to charId,
                        transitionSpec = {
                            val slideUp = index % 2 == 0

                            val bounceSpec = spring<IntOffset>(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = dynamicStiffness
                            )

                            if (slideUp) {
                                (slideInVertically(animationSpec = bounceSpec) { height -> height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                            } else {
                                (slideInVertically(animationSpec = bounceSpec) { height -> -height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> height } + fadeOut())
                            }
                        },
                        label = "BouncyChar"
                    ) { (targetChar, _) ->
                        Text(
                            text = targetChar.toString(),
                            style = style,
                            color = color,
                            softWrap = false
                        )
                    }
                } else {
                    Text(
                        text = char.toString(),
                        style = style,
                        color = color,
                        softWrap = false
                    )
                }
            }
        }
    }
}