package ckgod.snowball.invest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ckgod.snowball.invest.feature.detail.StockDetailContent
import com.arkivanov.decompose.extensions.compose.stack.Children

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    Children(
        stack = component.childStack,
        modifier = modifier,
        animation = backAnimation(
            backHandler = component.backHandler,
            onBack = component::onBackClicked
        )
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Main -> MainContent(component = instance.component)
            is RootComponent.Child.StockDetail -> StockDetailContent(component = instance.component)
        }
    }
}
