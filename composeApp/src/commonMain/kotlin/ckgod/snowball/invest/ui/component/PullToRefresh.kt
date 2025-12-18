package ckgod.snowball.invest.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CustomPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.Gray,
    content: @Composable () -> Unit
) {
    val refreshThreshold = 160f
    val indicatorSize = 20.dp

    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var wasRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (wasRefreshing && !isRefreshing) {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            )
        }
        wasRefreshing = isRefreshing
    }

    val nestedScrollConnection = remember(isRefreshing) {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 위로 스크롤할 때 (offset을 줄일 때)
                if (available.y < 0 && offsetY.value > 0) {
                    scope.launch {
                        val newOffset = (offsetY.value + available.y).coerceAtLeast(0f)
                        offsetY.snapTo(newOffset)
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val isDraggingDown = available.y > 0
                val isUserInput = source == NestedScrollSource.UserInput

                if (isDraggingDown && isUserInput && !isRefreshing) {
                    scope.launch {
                        val newOffset = (offsetY.value + available.y * 0.5f)
                            .coerceAtMost(refreshThreshold * 1.5f)
                        offsetY.snapTo(newOffset)
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                // 손을 뗐을 때
                if (offsetY.value > 0) {
                    if (offsetY.value >= refreshThreshold && !isRefreshing) {
                        // threshold까지 당겼으면 새로고침 시작
                        // offsetY를 약간 위로 올려서 indicator 위치 조정
                        onRefresh()
                        offsetY.animateTo(
                            targetValue = 120f,
                            animationSpec = tween(durationMillis = 200)
                        )
                    } else if (offsetY.value > 0 && !isRefreshing) {
                        // threshold 미만이면 원위치
                        offsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 200)
                        )
                    }

                    return available
                }
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
        ) {
            content()
        }

        if (offsetY.value > 0) {
            val alpha = (offsetY.value / refreshThreshold).coerceIn(0f, 1f)
            val indicatorOffsetY = (offsetY.value * 0.5f).roundToInt()

            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(0, indicatorOffsetY) }
                    .size(indicatorSize),
                strokeWidth = 2.dp,
                color = indicatorColor.copy(alpha = alpha)
            )
        }
    }
}
