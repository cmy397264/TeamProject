package com.bigPicture.businessreportgenerator.data.domain

data class PortfolioAnalysisData(
    val asset: Asset,
    val priceHistory: List<StockHistoryItem>,
    val currentValue: Double,
    val purchaseValue: Double,
    val returnPercentage: Double,
    val returnAmount: Double
)