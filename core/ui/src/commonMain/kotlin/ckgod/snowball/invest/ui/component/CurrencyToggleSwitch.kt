package ckgod.snowball.invest.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CurrencyToggleSwitch(
    modifier: Modifier = Modifier,
    isKrw: Boolean = false,
    onToggleChange: (Boolean) -> Unit
) {
    var isKrwSelected by remember(isKrw) { mutableStateOf(isKrw) }

    val backgroundColor = Color(0xFF2C2C2E)
    val indicatorColor = Color(0xFF636366)
    val selectedTextColor = Color.White
    val unselectedTextColor = Color(0xFF8E8E93)

    val animatedBias by animateFloatAsState(
        targetValue = if (!isKrwSelected) -1f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "ToggleAnimation"
    )

    val animatedAlignment = BiasAlignment(horizontalBias = animatedBias, verticalBias = 0f)

    Box(
        modifier = modifier
            .width(84.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp)) // 둥근 모서리
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                val newState = !isKrwSelected
                isKrwSelected = newState
                onToggleChange(newState)
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            contentAlignment = animatedAlignment
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = 0.5f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(indicatorColor)
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyText(
                text = "$",
                isSelected = !isKrwSelected,
                selectedColor = selectedTextColor,
                unselectedColor = unselectedTextColor,
                modifier = Modifier.weight(1f)
            )

            CurrencyText(
                text = "원",
                isSelected = isKrwSelected,
                selectedColor = selectedTextColor,
                unselectedColor = unselectedTextColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CurrencyText(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "TextColorAnimation"
    )

    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}