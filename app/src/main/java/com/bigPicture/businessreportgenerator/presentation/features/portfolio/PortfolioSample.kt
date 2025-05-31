package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import InvestmentTip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType

object ModernPortfolioSampleData {

    val samplePortfolios = listOf(
        Asset(
            name = "애플",
            type = AssetType.STOCK,
            purchasePrice = 5000000.0, // 500만원
            ticker = "AAPL",
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "150.0" // 150달러 가정
            )
        ),
        Asset(
            name = "엔비디아",
            type = AssetType.STOCK,
            purchasePrice = 3000000.0, // 300만원
            ticker = "NVDA",
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "900.0" // 900달러 가정
            )
        ),
        Asset(
            name = "삼성전자",
            type = AssetType.STOCK,
            purchasePrice = 2000000.0, // 200만원
            ticker = "005930",
            details = mapOf(
                "market" to "코스피",
                "averagePrice" to "70000.0" // 7만원 가정
            )
        ),
        Asset(
            name = "마이크로소프트",
            type = AssetType.STOCK,
            purchasePrice = 4000000.0, // 400만원
            ticker = "MSFT",
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "300.0" // 300달러 가정
            )
        ),
        Asset(
            name = "구글",
            type = AssetType.STOCK,
            purchasePrice = 3500000.0, // 350만원
            ticker = "GOOGL",
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "140.0" // 140달러 가정
            )
        ),
        Asset(
            name = "테슬라",
            type = AssetType.STOCK,
            purchasePrice = 2500000.0, // 250만원
            ticker = "TSLA",
            details = mapOf(
                "market" to "나스닥",
                "averagePrice" to "200.0" // 200달러 가정
            )
        )
    )

    private val investmentTips = listOf(
        InvestmentTip(
            title = "분산 투자의 힘",
            description = "여러 종목에 분산 투자하여 리스크를 줄이고 안정적인 수익을 추구하세요.",
            icon = Icons.Rounded.Star,
            iconColor = Color.White,
            gradient = Brush.linearGradient(
                listOf(
                    Color(0xFF667eea),
                    Color(0xFF764ba2)
                )
            )
        ),
        InvestmentTip(
            title = "장기 투자 전략",
            description = "시간의 힘을 활용한 장기 투자로 복리 효과를 누려보세요.",
            icon = Icons.Rounded.Star,
            iconColor = Color.White,
            gradient = Brush.linearGradient(
                listOf(
                    Color(0xFF10B981),
                    Color(0xFF059669)
                )
            )
        ),
        InvestmentTip(
            title = "정기적인 모니터링",
            description = "포트폴리오를 정기적으로 점검하고 필요시 리밸런싱하세요.",
            icon = Icons.Rounded.Edit,
            iconColor = Color.White,
            gradient = Brush.linearGradient(
                listOf(
                    Color(0xFF8B5CF6),
                    Color(0xFF7C3AED)
                )
            )
        ),
        InvestmentTip(
            title = "감정적 거래 피하기",
            description = "시장 변동성에 휩쓸리지 말고 장기적 관점을 유지하세요.",
            icon = Icons.Rounded.Star,
            iconColor = Color.White,
            gradient = Brush.linearGradient(
                listOf(
                    Color(0xFFF59E0B),
                    Color(0xFFD97706)
                )
            )
        ),
        InvestmentTip(
            title = "지속적인 학습",
            description = "투자 지식을 꾸준히 쌓아가며 현명한 투자자가 되세요.",
            icon = Icons.Rounded.Edit,
            iconColor = Color.White,
            gradient = Brush.linearGradient(
                listOf(
                    Color(0xFF06B6D4),
                    Color(0xFF0891B2)
                )
            )
        )
    )

    fun getRandomInvestmentTip(): InvestmentTip {
        return investmentTips.random()
    }
}