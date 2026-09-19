package ckgod.snowball.invest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import ckgod.snowball.invest.feature.backtest.DefaultBacktestComponent
import ckgod.snowball.invest.feature.backtest.DefaultBacktestResultComponent
import ckgod.snowball.invest.feature.account.DefaultAccountComponent
import ckgod.snowball.invest.feature.home.DefaultHomeComponent
import ckgod.snowball.invest.navigation.DefaultRootComponent
import ckgod.snowball.invest.feature.detail.DefaultStockDetailComponent
import ckgod.snowball.invest.navigation.DefaultMainComponent
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // retainedComponent 를 쓰면 구성 변경에도 컴포넌트 트리가 살아남는다.
        // 폴드를 접었다 펴면 화면 크기가 바뀌어 Activity 가 재생성되는데,
        // defaultComponentContext 였을 때는 트리가 통째로 다시 만들어져 네트워크를 다시 불렀다.
        val rootComponent = retainedComponent { componentContext ->
            DefaultRootComponent(
                componentContext = componentContext,
                mainComponentFactory = { context, output ->
                    DefaultMainComponent(
                        componentContext = context,
                        homeComponentFactory = { homeContext, onStockSelected ->
                            DefaultHomeComponent(
                                componentContext = homeContext,
                                onStockSelected = onStockSelected,
                                currencyRepository = get(),
                                getInvestmentStatusUseCase = get()
                            )
                        },
                        accountComponentFactory = { accountContext ->
                            DefaultAccountComponent(
                                componentContext = accountContext,
                                getAccountUseCase = get()
                            )
                        },
                        backtestComponentFactory = { backtestContext, onBacktestCompleted ->
                            DefaultBacktestComponent(
                                componentContext = backtestContext,
                                getStockPriceHistoryUseCase = get(),
                                runBacktestUseCase = get(),
                                onBacktestCompleted = onBacktestCompleted
                            )
                        },
                        output = output
                    )
                },
                stockDetailComponentFactory = { context, ticker, onBack ->
                    DefaultStockDetailComponent(
                        componentContext = context,
                        ticker = ticker,
                        onBack = onBack,
                        getStockDetailUseCase = get()
                    )
                },
                backtestResultComponentFactory = { context, response, onBack ->
                    DefaultBacktestResultComponent(
                        componentContext = context,
                        response = response,
                        onBack = onBack
                    )
                }
            )
        }

        setContent {
            App(rootComponent = rootComponent)
        }
    }
}
