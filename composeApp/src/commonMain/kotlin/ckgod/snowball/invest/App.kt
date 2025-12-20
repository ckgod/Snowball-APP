package ckgod.snowball.invest

import androidx.compose.runtime.Composable
import ckgod.snowball.invest.di.appModule
import ckgod.snowball.invest.feature.main.MainScreen
import ckgod.snowball.invest.ui.theme.SnowballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        SnowballTheme {
            MainScreen()
        }
    }
}