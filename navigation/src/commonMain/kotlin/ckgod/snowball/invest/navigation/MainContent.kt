package ckgod.snowball.invest.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ckgod.snowball.invest.feature.backtest.BacktestTab
import ckgod.snowball.invest.feature.account.AccountScreen
import ckgod.snowball.invest.feature.home.HomeScreen
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_backtest
import snowball.core.ui.generated.resources.ic_account
import snowball.core.ui.generated.resources.ic_home

@Composable
fun MainContent(
    component: MainComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.subscribeAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                MainComponent.Tab.entries.forEach { tab ->
                    val isSelected = when (childStack.active.instance) {
                        is MainComponent.Child.Home -> tab == MainComponent.Tab.HOME
                        is MainComponent.Child.Account -> tab == MainComponent.Tab.ACCOUNT
                        is MainComponent.Child.Backtest -> tab == MainComponent.Tab.BACKTEST
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { component.onTabSelected(tab) },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    when (tab) {
                                        MainComponent.Tab.HOME -> Res.drawable.ic_home
                                        MainComponent.Tab.ACCOUNT -> Res.drawable.ic_account
                                        MainComponent.Tab.BACKTEST -> Res.drawable.ic_backtest
                                    }
                                ),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                when (tab) {
                                    MainComponent.Tab.HOME -> "투자 현황"
                                    MainComponent.Tab.ACCOUNT -> "잔고"
                                    MainComponent.Tab.BACKTEST -> "백테스트"
                                }
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Children(
            stack = component.childStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            animation = stackAnimation(fade())
        ) { child ->
            when (val instance = child.instance) {
                is MainComponent.Child.Home -> HomeScreen(component = instance.component)
                is MainComponent.Child.Account -> AccountScreen(component = instance.component)
                is MainComponent.Child.Backtest -> BacktestTab()
            }
        }
    }
}
