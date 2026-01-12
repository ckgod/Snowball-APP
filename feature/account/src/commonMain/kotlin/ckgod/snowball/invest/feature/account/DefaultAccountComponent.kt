package ckgod.snowball.invest.feature.account

import com.arkivanov.decompose.ComponentContext


interface AccountComponent

class DefaultAccountComponent(
    componentContext: ComponentContext
) : AccountComponent, ComponentContext by componentContext
