package ckgod.snowball.invest

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ckgod.snowball.invest.di.appModule
import ckgod.snowball.invest.feature.backtest.DefaultBacktestComponent
import ckgod.snowball.invest.feature.chart.DefaultChartComponent
import ckgod.snowball.invest.feature.home.DefaultHomeComponent
import ckgod.snowball.invest.navigation.DefaultRootComponent
import ckgod.snowball.invest.navigation.DefaultStockDetailComponent
import ckgod.snowball.invest.navigation.DefaultMainComponent
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    startKoin {
        modules(appModule)
    }

    val lifecycle = LifecycleRegistry()
    val rootComponent = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        mainComponentFactory = { ctx, output ->
            DefaultMainComponent(
                componentContext = ctx,
                homeComponentFactory = { homeCtx, onStockSelected ->
                    DefaultHomeComponent(
                        componentContext = homeCtx,
                        onStockSelected = onStockSelected
                    )
                },
                chartComponentFactory = { chartCtx ->
                    DefaultChartComponent(chartCtx)
                },
                backtestComponentFactory = { backtestCtx ->
                    DefaultBacktestComponent(backtestCtx)
                },
                output = output
            )
        },
        stockDetailComponentFactory = { ctx, ticker, onBack ->
            DefaultStockDetailComponent(
                componentContext = ctx,
                ticker = ticker,
                onBack = onBack
            )
        }
    )

    return ComposeUIViewController {
        App(rootComponent = rootComponent)
    }
}