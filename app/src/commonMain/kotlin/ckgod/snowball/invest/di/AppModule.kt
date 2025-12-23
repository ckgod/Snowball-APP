package ckgod.snowball.invest.di

import ckgod.snowball.invest.AppConfig
import ckgod.snowball.invest.data.remote.HttpClientFactory
import ckgod.snowball.invest.data.repository.PortfolioRepositoryImpl
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import org.koin.dsl.module

/**
 * Koin DI 모듈
 */
val appModule = module {
    // HttpClient
    single {
        HttpClientFactory.create(
            baseUrl = AppConfig.API_BASE_URL,
            apiKey = AppConfig.API_KEY
        )
    }

    // Repository
    single<PortfolioRepository> {
        PortfolioRepositoryImpl(httpClient = get())
    }
}
