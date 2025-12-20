package ckgod.snowball.invest.feature.main

import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator

/**
 * MainScreen의 Navigator에 접근하기 위한 CompositionLocal
 * Tab 내부에서 전체 화면으로 이동할 때 사용
 */
val LocalMainNavigator = staticCompositionLocalOf<Navigator> {
    error("MainNavigator not provided")
}
