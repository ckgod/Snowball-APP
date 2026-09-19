package ckgod.snowball.invest.navigation

import ckgod.snowball.invest.feature.backtest.BacktestResultComponent
import ckgod.snowball.invest.feature.detail.StockDetailComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.ckgod.snowball.model.BacktestResponse
import kotlinx.serialization.Serializable

/**
 * Main 위에 상세 화면을 쌓는 **2단 깊이 스택**을 유지한다.
 *
 * 한때 Decompose 의 `ChildPanels` 로 Main 과 상세를 같은 깊이의 형제로 바꿔봤지만 되돌렸다.
 * 폴더블 2-pane 자체는 되는데 대가가 컸다 — `extensions-compose` 에 panels 용 컴포저블이 없어
 * 화면 전환 애니메이션과 predictive back 을 전부 잃는다. 깊이를 없애서 얻을 게 없었다.
 *
 * 지금은 스택 하나를 두고 **그리는 방식만** 폭에 따라 바꾼다. [RootContent] 참고.
 */
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
                // 2-pane 에서 다른 종목을 누르면 상세가 쌓이는 게 아니라 바뀌어야 한다.
                // 한 화면일 때도 상세에서 상세로 건너뛰는 경로는 없으므로 항상 교체로 다룬다.
                if (childStack.value.active.instance is RootComponent.Child.StockDetail) {
                    navigation.replaceCurrent(Config.StockDetail(output.ticker))
                } else {
                    navigation.push(Config.StockDetail(output.ticker))
                }
            }

            is MainComponent.Output.NavigateToBacktestResult -> {
                latestBacktestResponse = output.response
                navigation.push(Config.BacktestResult)
            }

            // 2-pane 에서 잔고 탭으로 옮겼는데 오른쪽에 종목 상세가 남아 있으면 앞뒤가 안 맞는다.
            // 한 화면일 때는 상세가 전체를 덮어 탭을 누를 수 없으므로 여기 올 일이 없다.
            is MainComponent.Output.StockDetailNoLongerRelevant -> {
                if (childStack.value.active.instance is RootComponent.Child.StockDetail) {
                    navigation.pop()
                }
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
