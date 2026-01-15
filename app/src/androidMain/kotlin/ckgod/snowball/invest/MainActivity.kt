package ckgod.snowball.invest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import ckgod.snowball.invest.di.appModule
import ckgod.snowball.invest.feature.backtest.DefaultBacktestComponent
import ckgod.snowball.invest.feature.account.DefaultAccountComponent
import ckgod.snowball.invest.feature.home.DefaultHomeComponent
import ckgod.snowball.invest.navigation.DefaultRootComponent
import ckgod.snowball.invest.feature.detail.DefaultStockDetailComponent
import ckgod.snowball.invest.navigation.DefaultMainComponent
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
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
                            accountRepository = get()
                        )
                    },
                    backtestComponentFactory = { backtestContext ->
                        DefaultBacktestComponent(backtestContext)
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
            }
        )

        setContent {
            App(rootComponent = rootComponent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}