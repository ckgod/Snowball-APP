package ckgod.snowball.invest.di

import ckgod.snowball.invest.AppConfig
import ckgod.snowball.invest.data.remote.HttpClientFactory
import ckgod.snowball.invest.data.repository.PortfolioRepositoryImpl
import ckgod.snowball.invest.data.repository.StockDetailRepositoryImpl
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import ckgod.snowball.invest.domain.repository.StockDetailRepository
import ckgod.snowball.invest.domain.state.CurrencyStateHolder
import org.koin.dsl.module

val appModule = module {
    single { CurrencyStateHolder() }

    single {
        HttpClientFactory.create(
            baseUrl = AppConfig.API_BASE_URL,
            apiKey = AppConfig.API_KEY
        )
    }

    single<PortfolioRepository> {
        PortfolioRepositoryImpl(
            httpClient = get()
        )
    }

    single<StockDetailRepository> {
        StockDetailRepositoryImpl(
            httpClient = get()
        )
    }
}
