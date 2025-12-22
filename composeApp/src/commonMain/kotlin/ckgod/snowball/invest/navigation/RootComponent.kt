package ckgod.snowball.invest.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.serialization.Serializable

interface RootComponent : BackHandlerOwner {
    val childStack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed interface Child {
        data class Main(val component: MainComponent) : Child
        data class StockDetail(val component: StockDetailComponent) : Child
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val mainComponentFactory: (ComponentContext, (MainComponent.Output) -> Unit) -> MainComponent,
    private val stockDetailComponentFactory: (ComponentContext, String, () -> Unit) -> StockDetailComponent,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

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
        }

    private fun onMainOutput(output: MainComponent.Output) {
        when (output) {
            is MainComponent.Output.NavigateToStockDetail -> {
                navigation.push(Config.StockDetail(output.ticker))
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
    }
}
