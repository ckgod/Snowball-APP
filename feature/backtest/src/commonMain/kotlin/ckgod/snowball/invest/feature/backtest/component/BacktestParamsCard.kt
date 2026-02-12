package ckgod.snowball.invest.feature.backtest.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.theme.ProgressBlueLight
import ckgod.snowball.invest.ui.theme.ProgressBlueDark
import com.ckgod.snowball.model.StarMode
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
fun BacktestParamsCard(
    initialCapital: Double,
    oneTimeAmount: Double,
    division: Int,
    targetRate: Double,
    starMode: StarMode,
    onInitialCapitalChanged: (Double) -> Unit,
    onDivisionChanged: (Int) -> Unit,
    onTargetRateChanged: (Double) -> Unit,
    onStarModeChanged: (StarMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) ProgressBlueLight else ProgressBlueDark

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 투자원금 + 분할수 (2열)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SliderParamCard(
                label = "투자 원금 ($)",
                value = initialCapital.toFloat(),
                valueDisplay = "$${initialCapital.roundToLong()}",
                min = 1_000f,
                max = 200_000f,
                step = 1_000f,
                accentColor = accentColor,
                onValueChange = { onInitialCapitalChanged(it.toDouble()) },
                modifier = Modifier.weight(1f)
            )
            SliderParamCard(
                label = "분할 수",
                value = division.toFloat(),
                valueDisplay = "$division",
                min = 5f,
                max = 100f,
                step = 1f,
                accentColor = accentColor,
                onValueChange = { onDivisionChanged(it.roundToInt()) },
                modifier = Modifier.weight(1f)
            )
        }

        // 목표 수익률
        SliderParamCard(
            label = "목표 수익률 (%)",
            value = targetRate.toFloat(),
            valueDisplay = "${targetRate}%",
            min = 0.5f,
            max = 30f,
            step = 0.5f,
            accentColor = accentColor,
            onValueChange = { onTargetRateChanged(it.toDouble()) },
            modifier = Modifier.fillMaxWidth()
        )

        StarModeToggle(
            selectedMode = starMode,
            onModeChanged = onStarModeChanged
        )

        // Auto-calculated display
        Text(
            text = "1회 매수금: $${oneTimeAmount.toLong()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun SliderParamCard(
    label: String,
    value: Float,
    valueDisplay: String,
    min: Float,
    max: Float,
    step: Float,
    accentColor: androidx.compose.ui.graphics.Color,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = ((max - min) / step).roundToInt() - 1

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = valueDisplay,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepButton(
                text = "-",
                enabled = value > min,
                accentColor = accentColor,
                onClick = { onValueChange((value - step).coerceAtLeast(min)) }
            )

            Slider(
                value = value,
                onValueChange = { raw ->
                    val snapped = (((raw - min) / step).roundToInt() * step + min)
                        .coerceIn(min, max)
                    onValueChange(snapped)
                },
                valueRange = min..max,
                steps = steps,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = accentColor.copy(alpha = 0.15f)
                )
            )

            StepButton(
                text = "+",
                enabled = value < max,
                accentColor = accentColor,
                onClick = { onValueChange((value + step).coerceAtMost(max)) }
            )
        }
    }
}

@Composable
private fun StepButton(
    text: String,
    enabled: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = if (enabled) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .size(32.dp)
            .background(
                color = if (enabled) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(top = 4.dp)
    )
}

@Composable
private fun StarModeToggle(
    selectedMode: StarMode,
    onModeChanged: (StarMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) ProgressBlueLight else ProgressBlueDark

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Star Mode (P1_2 / P2_3)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (selectedMode) {
                    StarMode.P1_2 -> "P1_2"
                    StarMode.P2_3 -> "P2_3"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Switch(
            checked = selectedMode == StarMode.P2_3,
            onCheckedChange = { checked ->
                onModeChanged(if (checked) StarMode.P2_3 else StarMode.P1_2)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.3f)
            )
        )
    }
}
