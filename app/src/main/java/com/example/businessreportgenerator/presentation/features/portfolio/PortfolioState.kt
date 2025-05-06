// presentation/features/portfolio/PortfolioState.kt
package com.example.businessreportgenerator.presentation.features.portfolio

import com.example.businessreportgenerator.domain.model.Asset

data class PortfolioState(
    val assets: List<Asset> = emptyList(),
    val sampleAssets : List<Asset> = emptyList(),
    val isAddAssetDialogVisible: Boolean = false,
    val isSamplePortfolioDialogVisible: Boolean = false, // 샘플 포트폴리오 모달 띄우기 유무
    val totalPortfolioValue: Double = 0.0
)