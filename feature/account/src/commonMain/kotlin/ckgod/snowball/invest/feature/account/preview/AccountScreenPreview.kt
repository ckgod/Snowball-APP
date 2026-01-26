package ckgod.snowball.invest.feature.account.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ckgod.snowball.invest.feature.account.AccountContent
import ckgod.snowball.invest.ui.theme.SnowballTheme
import com.ckgod.snowball.model.AssetItemResponse
import com.ckgod.snowball.model.AssetType
import com.ckgod.snowball.model.TotalAssetResponse
import com.ckgod.snowball.model.CurrencyType
import com.ckgod.snowball.model.HoldingStockResponse

@Preview
@Composable
private fun AccountScreenPreview() {

    val sampleData = TotalAssetResponse(
        assets = listOf(
            AssetItemResponse(
                type = AssetType.OVERSEAS_STOCKS,
                purchaseAmount = 14500.0,
                evaluationAmount = 15950.0,
                evaluationProfitLoss = 1450.0,
                creditLoanAmount = 0.0,
                realNetAssetAmount = 15950.0,
                wholeWeightRate = 48.33
            ),
            AssetItemResponse(
                type = AssetType.FOREIGN_CURRENCY,
                purchaseAmount = 0.0,
                evaluationAmount = 5000.0,
                evaluationProfitLoss = 0.0,
                creditLoanAmount = 0.0,
                realNetAssetAmount = 5000.0,
                wholeWeightRate = 15.15
            ),
            AssetItemResponse(
                type = AssetType.TOTAL,
                purchaseAmount = 30000.0,
                evaluationAmount = 33000.0,
                evaluationProfitLoss = 3000.0,
                creditLoanAmount = 0.0,
                realNetAssetAmount = 33000.0,
                wholeWeightRate = 100.0
            )
        ),
        holdingStocks = listOf(
            HoldingStockResponse(
                ticker = "TQQQ",
                name = "ProShares UltraPro QQQ",
                quantity = 100,
                avgPrice = 50.0,
                currentPrice = 55.0,
                profitRate = 10.0,
                investedAmount = 5000.0,
                capital = 9900.0
            ),
            HoldingStockResponse(
                ticker = "SOXL",
                name = "Direxion Daily Semiconductor Bull 3X",
                quantity = 50,
                avgPrice = 30.0,
                currentPrice = 33.0,
                profitRate = 10.0,
                investedAmount = 1500.0,
                capital = 7920.0
            ),
            HoldingStockResponse(
                ticker = "NVDA",
                name = "NVIDIA Corporation",
                quantity = 20,
                avgPrice = 400.0,
                currentPrice = 300.0,
                profitRate = -25.0,
                investedAmount = 8000.0,
                capital = 14850.0
            )
        ),
        exchangeRate = 1450.0
    )

    SnowballTheme(darkTheme = true) {
        AccountContent(accountStatus = sampleData, currencyType = CurrencyType.USD, exchangeRate = 1450.0)
    }
}
