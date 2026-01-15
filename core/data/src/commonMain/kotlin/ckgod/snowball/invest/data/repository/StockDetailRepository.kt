package ckgod.snowball.invest.data.repository

import com.ckgod.snowball.model.StockDetailResponse

interface StockDetailRepository {
    suspend fun getStockDetail(ticker: String): StockDetailResponse
}
