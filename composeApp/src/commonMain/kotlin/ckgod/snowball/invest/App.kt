package ckgod.snowball.invest

import androidx.compose.runtime.Composable
import ckgod.snowball.invest.navigation.RootComponent
import ckgod.snowball.invest.navigation.RootContent
import ckgod.snowball.invest.ui.theme.SnowballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    rootComponent: RootComponent
) {
    SnowballTheme {
        RootContent(component = rootComponent)
    }
}