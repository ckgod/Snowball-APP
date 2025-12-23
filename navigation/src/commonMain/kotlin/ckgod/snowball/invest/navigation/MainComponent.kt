package ckgod.snowball.invest.navigation

import ckgod.snowball.invest.feature.backtest.BacktestComponent
import ckgod.snowball.invest.feature.chart.ChartComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import ckgod.snowball.invest.feature.home.HomeComponent
import kotlinx.serialization.Serializable

interface MainComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun onTabSelected(tab: Tab)

    sealed interface Child {
        data class Home(val component: HomeComponent) : Child
        data class Chart(val component: ChartComponent) : Child
        data class Backtest(val component: BacktestComponent) : Child
    }

    enum class Tab {
        HOME, CHART, BACKTEST
    }

    sealed interface Output {
        data class NavigateToStockDetail(val ticker: String) : Output
    }
}

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val homeComponentFactory: (ComponentContext, (String) -> Unit) -> HomeComponent,
    private val chartComponentFactory: (ComponentContext) -> ChartComponent,
    private val backtestComponentFactory: (ComponentContext) -> BacktestComponent,
    private val output: (MainComponent.Output) -> Unit
) : MainComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, MainComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Home,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): MainComponent.Child =
        when (config) {
            Config.Home -> MainComponent.Child.Home(
                component = homeComponentFactory(
                    componentContext,
                    ::onHomeOutput
                )
            )

            Config.Chart -> MainComponent.Child.Chart(
                component = chartComponentFactory(componentContext)
            )

            Config.Backtest -> MainComponent.Child.Backtest(
                component = backtestComponentFactory(componentContext)
            )
        }

    private fun onHomeOutput(ticker: String) {
        output(MainComponent.Output.NavigateToStockDetail(ticker))
    }

    override fun onTabSelected(tab: MainComponent.Tab) {
        val config = when (tab) {
            MainComponent.Tab.HOME -> Config.Home
            MainComponent.Tab.CHART -> Config.Chart
            MainComponent.Tab.BACKTEST -> Config.Backtest
        }
        navigation.bringToFront(config)
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Home : Config

        @Serializable
        data object Chart : Config

        @Serializable
        data object Backtest : Config
    }
}


