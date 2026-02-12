package ckgod.snowball.invest.di

import ckgod.snowball.invest.AppConfig
import ckgod.snowball.invest.data.remote.HttpClientFactory
import ckgod.snowball.invest.data.repository.PortfolioRepositoryImpl
import ckgod.snowball.invest.data.repository.StockDetailRepositoryImpl
import ckgod.snowball.invest.data.repository.PortfolioRepository
import ckgod.snowball.invest.data.repository.StockDetailRepository
import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepository
import ckgod.snowball.invest.data.repository.CurrencyPreferencesRepositoryImpl
import ckgod.snowball.invest.data.repository.AccountRepository
import ckgod.snowball.invest.data.repository.AccountRepositoryImpl
import ckgod.snowball.invest.data.repository.BacktestRepository
import ckgod.snowball.invest.data.repository.BacktestRepositoryImpl
import ckgod.snowball.invest.domain.usecase.GetAccountUseCase
import ckgod.snowball.invest.domain.usecase.GetCurrencyInfoUseCase
import ckgod.snowball.invest.domain.usecase.GetInvestmentStatusUseCase
import ckgod.snowball.invest.domain.usecase.GetStockDetailUseCase
import ckgod.snowball.invest.domain.usecase.GetStockPriceHistoryUseCase
import ckgod.snowball.invest.domain.usecase.RunBacktestUseCase
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClientFactory.create(
            baseUrl = AppConfig.API_BASE_URL,
            apiKey = AppConfig.API_KEY
        )
    }

    // Repositories
    single<CurrencyPreferencesRepository> {
        CurrencyPreferencesRepositoryImpl()
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

    single<AccountRepository> {
        AccountRepositoryImpl(
            httpClient = get()
        )
    }

    single<BacktestRepository> {
        BacktestRepositoryImpl(
            httpClient = get()
        )
    }

    // UseCases
    factory {
        GetCurrencyInfoUseCase(
            currencyPreferencesRepository = get()
        )
    }

    factory {
        GetInvestmentStatusUseCase(
            currencyPreferencesRepository = get(),
            portfolioRepository = get()
        )
    }

    factory {
        GetStockDetailUseCase(
            currencyInfoUseCase = get(),
            stockDetailRepository = get()
        )
    }

    factory {
        GetAccountUseCase(
            currencyPreferencesRepository = get(),
            accountRepository = get()
        )
    }

    factory {
        GetStockPriceHistoryUseCase(
            backtestRepository = get()
        )
    }

    factory {
        RunBacktestUseCase(
            backtestRepository = get()
        )
    }
}
