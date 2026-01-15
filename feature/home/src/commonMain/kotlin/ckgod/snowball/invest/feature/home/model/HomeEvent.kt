package ckgod.snowball.invest.feature.home.model

sealed interface HomeEvent {
    data object Refresh : HomeEvent
    data class OnStockClick(val ticker: String) : HomeEvent
}
