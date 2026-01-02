package ckgod.snowball.invest.di

import ckgod.snowball.invest.AppConfig
import ckgod.snowball.invest.data.remote.HttpClientFactory
import ckgod.snowball.invest.data.repository.PortfolioRepositoryImpl
import ckgod.snowball.invest.data.repository.StockDetailRepositoryImpl
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import ckgod.snowball.invest.domain.repository.StockDetailRepository
import ckgod.snowball.invest.util.CurrencyManager
import org.koin.dsl.module

/**
 * Koin DI 모듈
 */
val appModule = module {
    single { CurrencyManager() }

    // HttpClient
    single {
        HttpClientFactory.create(
            baseUrl = AppConfig.API_BASE_URL,
            apiKey = AppConfig.API_KEY
        )
    }

    // Repository
    single<PortfolioRepository> {
        PortfolioRepositoryImpl(
            httpClient = get(),
            currencyManager = get()
        )
    }

    single<StockDetailRepository> {
        StockDetailRepositoryImpl(
            httpClient = get(),
            currencyManager = get()
        )
    }
}
