package com.example.businessreportgenerator.presentation.features.portfolio

import com.example.businessreportgenerator.data.domain.Asset
import com.example.businessreportgenerator.data.domain.AssetType

object DummyPortfolioData{
    val portfolios = listOf(
        Asset(
            name = "삼성전자",
            type = AssetType.STOCK,
            purchasePrice = 8_200_000.0,
            details = emptyMap()
        ),
        Asset(
            name = "애플",
            type = AssetType.STOCK,
            purchasePrice = 11_200_000.0,
            details = emptyMap()
        ),
        Asset(
            name = "마이크로소프트",
            type = AssetType.STOCK,
            purchasePrice = 3_200_000.0,
            details = emptyMap()
        ),
        Asset(
            name = "KODEX 200",
            type = AssetType.ETF,
            purchasePrice = 2_000_000.0,
            details = emptyMap()
        )
    )
}