package ckgod.snowball.invest.data.mapper

import ckgod.snowball.invest.data.remote.dto.PortfolioResponse
import ckgod.snowball.invest.data.remote.dto.StockDto
import ckgod.snowball.invest.domain.model.Portfolio
import ckgod.snowball.invest.domain.model.StockSummary
import ckgod.snowball.invest.domain.model.TradePhase

fun PortfolioResponse.toDomain(): Portfolio {
    return Portfolio(
        totalCount = totalCount,
        stocks = stocks.map { it.toDomain() }
    )
}

fun StockDto.toDomain(): StockSummary {
    return StockSummary(
        ticker = ticker,
        fullName = fullName ?: ticker,
        currentPrice = currentPrice,
        dailyChangeRate = dailyChangeRate,
        tValue = tValue,
        totalDivision = totalDivision,
        starPercent = starPercent,
        phase = TradePhase.fromDisplayName(phase),
        avgPrice = avgPrice,
        quantity = quantity,
        profitRate = profitRate,
        profitAmount = profitAmount,
        oneTimeAmount = oneTimeAmount,
        totalInvested = totalInvested,
        capital = capital,
        nextSellStarPrice = nextSellStarPrice,
        nextSellTargetPrice = nextSellTargetPrice,
        nextBuyStarPrice = nextBuyStarPrice,
    )
}
