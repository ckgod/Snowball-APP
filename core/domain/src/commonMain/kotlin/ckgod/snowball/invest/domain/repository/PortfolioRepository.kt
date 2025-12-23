package ckgod.snowball.invest.domain.repository

import ckgod.snowball.invest.domain.model.Portfolio

interface PortfolioRepository {
    suspend fun getPortfolioStatus(): Result<Portfolio>
}
