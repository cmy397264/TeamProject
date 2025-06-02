package com.bigPicture.businessreportgenerator.data.domain


data class StockHistoryItem(
    val stockDate: String,
    val stockPrice: Double
)

data class StockHistoryResponse(
    val status: String,
    val message: String,
    val data: List<StockHistoryItem>,
    val code: Int
)

data class PortfolioAnalysisData(
    val asset: Asset,
    val priceHistory: List<StockHistoryItem>,
    val currentValue: Double,
    val purchaseValue: Double,
    val returnPercentage: Double,
    val returnAmount: Double
)