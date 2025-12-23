package ckgod.snowball.invest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import ckgod.snowball.invest.di.appModule
import ckgod.snowball.invest.feature.backtest.DefaultBacktestComponent
import ckgod.snowball.invest.feature.chart.DefaultChartComponent
import ckgod.snowball.invest.feature.home.DefaultHomeComponent
import ckgod.snowball.invest.navigation.DefaultMainComponent
import ckgod.snowball.invest.navigation.DefaultRootComponent
import ckgod.snowball.invest.feature.detail.DefaultStockDetailComponent
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController(): UIViewController {
    startKoin {
        modules(appModule)
    }

    val backDispatcher = BackDispatcher()

    val lifecycle = LifecycleRegistry()
    lifecycle.attachToDecomposeLifecycle()

    val rootComponent = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle, backHandler = backDispatcher),
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
        PredictiveBackGestureOverlay(
            backDispatcher = backDispatcher,
            backIcon = null,
            modifier = Modifier.fillMaxSize(),
            activationOffsetThreshold = 14.dp
        ) {
            App(rootComponent = rootComponent)
        }
    }
}