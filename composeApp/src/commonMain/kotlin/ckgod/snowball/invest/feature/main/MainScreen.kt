package ckgod.snowball.invest.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ckgod.snowball.invest.feature.backtest.BacktestTab
import ckgod.snowball.invest.feature.chart.ChartTab
import ckgod.snowball.invest.feature.home.HomeTab

class MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            Scaffold(
                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(ChartTab)
                        TabNavigationItem(BacktestTab)
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title) },
        icon = {
            tab.options.icon?.let {
                Icon(painter = it, contentDescription = tab.options.title)
            }
        }
    )
}
