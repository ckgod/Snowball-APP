package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.HomeTabResponse

interface PortfolioRepository {
    suspend fun getPortfolioStatus(): Result<HomeTabResponse>
}
