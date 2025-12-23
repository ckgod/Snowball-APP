package ckgod.snowball.invest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ckgod.snowball.invest.feature.detail.StockDetailScreen
import ckgod.snowball.invest.feature.detail.model.StockDetailEvent
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

@Composable
private fun StockDetailContent(
    component: StockDetailComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()

    StockDetailScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                StockDetailEvent.BackClick -> component.onBackClick()
                StockDetailEvent.Refresh -> component.onRefresh()
            }
        },
        modifier = modifier
    )
}
