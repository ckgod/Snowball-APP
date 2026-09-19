package ckgod.snowball.invest.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ckgod.snowball.invest.feature.backtest.BacktestScreen
import ckgod.snowball.invest.feature.account.AccountScreen
import ckgod.snowball.invest.feature.home.HomeScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import snowball.core.ui.generated.resources.Res
import snowball.core.ui.generated.resources.ic_backtest
import snowball.core.ui.generated.resources.ic_account
import snowball.core.ui.generated.resources.ic_home

private fun MainComponent.Tab.icon(): DrawableResource =
    when (this) {
        MainComponent.Tab.HOME -> Res.drawable.ic_home
        MainComponent.Tab.ACCOUNT -> Res.drawable.ic_account
        MainComponent.Tab.BACKTEST -> Res.drawable.ic_backtest
    }

private fun MainComponent.Tab.label(): String =
    when (this) {
        MainComponent.Tab.HOME -> "투자 현황"
        MainComponent.Tab.ACCOUNT -> "잔고"
        MainComponent.Tab.BACKTEST -> "백테스트"
    }

/**
 * @param useNavigationRail 넓은 화면(2-pane)에서 true. 하단 바 대신 좌측 세로 레일을 쓴다.
 *   2-pane 이면 이 컴포저블이 화면의 왼쪽 일부만 차지하는데, 그 아래에만 하단 바가 걸리면
 *   화면을 가로지르지 못해 어색해진다.
 */
@Composable
fun MainContent(
    component: MainComponent,
    modifier: Modifier = Modifier,
    useNavigationRail: Boolean = false,
) {
    val childStack by component.childStack.subscribeAsState()

    val selectedTab = when (childStack.active.instance) {
        is MainComponent.Child.Home -> MainComponent.Tab.HOME
        is MainComponent.Child.Account -> MainComponent.Tab.ACCOUNT
        is MainComponent.Child.Backtest -> MainComponent.Tab.BACKTEST
    }

    // 레일을 쓸 때도 Scaffold 는 유지한다. edge-to-edge 인셋 처리를 Scaffold 가 해준다.
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (!useNavigationRail) {
                NavigationBar {
                    MainComponent.Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selectedTab,
                            onClick = { component.onTabSelected(tab) },
                            icon = {
                                Icon(
                                    painter = painterResource(tab.icon()),
                                    contentDescription = null
                                )
                            },
                            label = { Text(tab.label()) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (useNavigationRail) {
                NavigationRail {
                    MainComponent.Tab.entries.forEach { tab ->
                        NavigationRailItem(
                            selected = tab == selectedTab,
                            onClick = { component.onTabSelected(tab) },
                            icon = {
                                Icon(
                                    painter = painterResource(tab.icon()),
                                    contentDescription = null
                                )
                            },
                            label = { Text(tab.label()) }
                        )
                    }
                }
            }

            Children(
                stack = component.childStack,
                modifier = Modifier.fillMaxSize(),
                animation = stackAnimation(fade())
            ) { child ->
                when (val instance = child.instance) {
                    is MainComponent.Child.Home -> HomeScreen(component = instance.component)
                    is MainComponent.Child.Account -> AccountScreen(component = instance.component)
                    is MainComponent.Child.Backtest -> BacktestScreen(component = instance.component)
                }
            }
        }
    }
}
