package ckgod.snowball.invest.feature.detail.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LocalShimmerBrush = compositionLocalOf {
    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
}

@Composable
fun StockDetailSkeleton(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    CompositionLocalProvider(LocalShimmerBrush provides shimmerBrush) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Header Skeleton
            item {
                SummaryHeaderSkeleton()
            }

            // Strategy Dashboard Skeleton
            item {
                StrategyDashboardSkeleton()
            }

            // Order Plan Card Skeleton
            item {
                OrderPlanCardSkeleton()
            }

            // Date Header Skeleton
            item {
                DateHeaderSkeleton()
            }

            // History Items Skeleton
            items(3) {
                HistoryItemSkeleton()
            }
        }
    }
}

@Composable
private fun SummaryHeaderSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 현재가
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 손익 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    ShimmerBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ShimmerBox(
                            modifier = Modifier
                                .width(80.dp)
                                .height(28.dp)
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .width(60.dp)
                                .height(24.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .width(50.dp)
                            .height(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StrategyDashboardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(2) {
                    Column {
                        ShimmerBox(
                            modifier = Modifier
                                .width(60.dp)
                                .height(14.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerBox(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderPlanCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 별% 타이틀
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .width(40.dp)
                        .height(20.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 매수/매도 섹션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OrderSectionSkeleton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                OrderSectionSkeleton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun OrderSectionSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // title
        ShimmerBox(
            modifier = Modifier
                .width(60.dp)
                .height(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // firstPrice
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .width(70.dp)
                    .height(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // secondPrice
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .width(70.dp)
                    .height(20.dp)
            )
        }
    }
}

@Composable
private fun DateHeaderSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(16.dp)
        )
    }
}

@Composable
private fun HistoryItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(
            modifier = Modifier
                .width(48.dp)
                .height(16.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        ShimmerBox(
            modifier = Modifier
                .width(60.dp)
                .height(24.dp)
        )
    }
}

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(LocalShimmerBrush.current)
    )
}
