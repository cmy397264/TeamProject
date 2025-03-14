// presentation/features/portfolio/PortfolioState.kt
package com.example.businessreportgenerator.presentation.features.portfolio

import com.example.businessreportgenerator.domain.model.Asset

data class PortfolioState(
    val assets: List<Asset> = emptyList(),
    val isAddAssetDialogVisible: Boolean = false,
    val totalPortfolioValue: Double = 0.0
)