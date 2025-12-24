package ckgod.snowball.invest.domain.repository

import ckgod.snowball.invest.domain.model.StockDetailState

interface StockDetailRepository {
    suspend fun getStockDetail(ticker: String): Result<StockDetailState>
}
