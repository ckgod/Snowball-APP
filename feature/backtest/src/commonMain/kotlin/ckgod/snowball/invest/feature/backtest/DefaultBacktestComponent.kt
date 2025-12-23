package ckgod.snowball.invest.feature.backtest

import com.arkivanov.decompose.ComponentContext

interface BacktestComponent

class DefaultBacktestComponent(
    componentContext: ComponentContext
) : BacktestComponent, ComponentContext by componentContext
