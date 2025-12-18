package ckgod.snowball.invest

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ckgod.snowball.invest.feature.main.MainScreen
import ckgod.snowball.invest.ui.theme.SnowballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    SnowballTheme {
        Navigator(MainScreen())
    }
}