package ckgod.snowball.invest.ui.extensions

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

fun TextStyle.withFixedHeight(ratio: Float = 1.3f): TextStyle = copy(
    lineHeight = fontSize * ratio,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)
