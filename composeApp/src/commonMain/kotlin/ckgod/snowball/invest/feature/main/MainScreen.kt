package ckgod.snowball.invest.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.navigator.Navigator

/**
 * 메인 화면 - 앱의 네비게이션을 관리
 * TabsScreen(탭 화면)을 기본으로 보여주고,
 * 필요시 전체 화면(StockDetailScreen 등)으로 이동
 */
@Composable
fun MainScreen() {
    Navigator(screen = TabsScreen()) { navigator ->
        CompositionLocalProvider(LocalMainNavigator provides navigator) {
            navigator.lastItem.Content()
        }
    }
}
