package ckgod.snowball.invest.di

import ckgod.snowball.invest.AppConfig
import ckgod.snowball.invest.data.remote.HttpClientFactory
import ckgod.snowball.invest.data.repository.PortfolioRepositoryImpl
import ckgod.snowball.invest.data.repository.StockDetailRepositoryImpl
import ckgod.snowball.invest.data.repository.PortfolioRepository
import ckgod.snowball.invest.data.repository.StockDetailRepository
import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepository
import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepositoryImpl
import ckgod.snowball.invest.domain.usecase.GroupTradeHistoriesByDateUseCase
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<CurrencyPreferencesRepository> {
        CurrencyPreferencesRepositoryImpl()
    }

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

    // UseCases
    factory {
        GroupTradeHistoriesByDateUseCase()
    }
}
