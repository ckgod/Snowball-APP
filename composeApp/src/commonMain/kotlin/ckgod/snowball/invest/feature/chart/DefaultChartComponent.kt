package ckgod.snowball.invest.feature.chart

import com.arkivanov.decompose.ComponentContext


interface ChartComponent

class DefaultChartComponent(
    componentContext: ComponentContext
) : ChartComponent, ComponentContext by componentContext
