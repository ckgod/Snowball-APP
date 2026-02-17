package ckgod.snowball.invest.navigation

import ckgod.snowball.invest.feature.backtest.BacktestResultComponent
import ckgod.snowball.invest.feature.detail.StockDetailComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.ckgod.snowball.model.BacktestResponse
import kotlinx.serialization.Serializable

interface RootComponent : BackHandlerOwner {
    val childStack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed interface Child {
        data class Main(val component: MainComponent) : Child
        data class StockDetail(val component: StockDetailComponent) : Child
        data class BacktestResult(val component: BacktestResultComponent) : Child
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val mainComponentFactory: (ComponentContext, (MainComponent.Output) -> Unit) -> MainComponent,
    private val stockDetailComponentFactory: (ComponentContext, String, () -> Unit) -> StockDetailComponent,
    private val backtestResultComponentFactory: (ComponentContext, BacktestResponse?, () -> Unit) -> BacktestResultComponent,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private var latestBacktestResponse: BacktestResponse? = null

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Main,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child =
        when (config) {
            is Config.Main -> RootComponent.Child.Main(
                component = mainComponentFactory(
                    componentContext,
                    ::onMainOutput
                )
            )

            is Config.StockDetail -> RootComponent.Child.StockDetail(
                component = stockDetailComponentFactory(
                    componentContext,
                    config.ticker,
                    ::onStockDetailOutput
                )
            )

            is Config.BacktestResult -> {
                val response = latestBacktestResponse
                latestBacktestResponse = null
                RootComponent.Child.BacktestResult(
                    component = backtestResultComponentFactory(
                        componentContext,
                        response,
                        { navigation.pop() }
                    )
                )
            }
        }

    private fun onMainOutput(output: MainComponent.Output) {
        when (output) {
            is MainComponent.Output.NavigateToStockDetail -> {
                navigation.push(Config.StockDetail(output.ticker))
            }

            is MainComponent.Output.NavigateToBacktestResult -> {
                latestBacktestResponse = output.response
                navigation.push(Config.BacktestResult)
            }
        }
    }

    private fun onStockDetailOutput() {
        navigation.pop()
    }

    override fun onBackClicked() {
        navigation.pop()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Main : Config

        @Serializable
        data class StockDetail(val ticker: String) : Config

        @Serializable
        data object BacktestResult : Config
    }
}
