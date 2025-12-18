package ckgod.snowball.invest.di

import ckgod.snowball.invest.AppConfig
import ckgod.snowball.invest.data.remote.HttpClientFactory
import ckgod.snowball.invest.data.repository.PortfolioRepositoryImpl
import ckgod.snowball.invest.domain.repository.PortfolioRepository
import ckgod.snowball.invest.feature.home.HomeScreenModel
import org.koin.dsl.module

/**
 * Koin DI 모듈
 */
val appModule = module {
    // HttpClient
    single {
        HttpClientFactory.create(AppConfig.API_BASE_URL)
    }

    // Repository
    single<PortfolioRepository> {
        PortfolioRepositoryImpl(
            httpClient = get(),
            apiKey = AppConfig.API_KEY
        )
    }

    // ScreenModel
    factory {
        HomeScreenModel(
            portfolioRepository = get()
        )
    }
}
