package ckgod.snowball.invest.feature.backtest.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.ui.theme.ProgressBlueLight
import ckgod.snowball.invest.ui.theme.ProgressBlueDark
import com.ckgod.snowball.model.StarMode

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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ParamCard(
                label = "투자 원금 ($)",
                value = initialCapital.toLong().toString(),
                onValueChange = { it.toDoubleOrNull()?.let(onInitialCapitalChanged) },
                modifier = Modifier.weight(1f)
            )
            ParamCard(
                label = "분할 수",
                value = division.toString(),
                onValueChange = { it.toIntOrNull()?.let(onDivisionChanged) },
                modifier = Modifier.weight(1f)
            )
        }

        ParamCard(
            label = "목표 수익률 (%)",
            value = targetRate.toString(),
            onValueChange = { it.toDoubleOrNull()?.let(onTargetRateChanged) },
            keyboardType = KeyboardType.Decimal,
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
private fun ParamCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Number,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.fillMaxWidth()
        )
    }
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
