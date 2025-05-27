package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType

object ModernPortfolioSampleData {
    val samplePortfolios = listOf(
        Asset(
            name = "삼성전자",
            type = AssetType.STOCK,
            purchasePrice = 12_500_000.0,
            details = mapOf(
                "market" to "코스피",
                "averagePrice" to "72000"
            )
        ),
        Asset(
            name = "애플",
            type = AssetType.STOCK,
            purchasePrice = 15_800_000.0,
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "180"
            )
        ),
        Asset(
            name = "엔비디아",
            type = AssetType.STOCK,
            purchasePrice = 8_200_000.0,
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "420"
            )
        ),
        Asset(
            name = "테슬라",
            type = AssetType.STOCK,
            purchasePrice = 6_300_000.0,
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "250"
            )
        ),
        Asset(
            name = "KODEX 나스닥100",
            type = AssetType.ETF,
            purchasePrice = 4_500_000.0,
            details = mapOf(
                "market" to "코스피",
                "averagePrice" to "18500"
            )
        ),
        Asset(
            name = "비트코인",
            type = AssetType.CRYPTO,
            purchasePrice = 3_200_000.0,
            details = mapOf(
                "averagePrice" to "95000000"
            )
        ),
        Asset(
            name = "이더리움",
            type = AssetType.CRYPTO,
            purchasePrice = 2_100_000.0,
            details = mapOf(
                "averagePrice" to "4200000"
            )
        )
    )

    // 랜덤 투자 팁 생성 함수
    fun getRandomInvestmentTip(): InvestmentTip {
        return InvestmentTips.random()
    }
}