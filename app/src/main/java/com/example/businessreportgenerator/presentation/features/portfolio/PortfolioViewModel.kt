package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.lifecycle.ViewModel
import com.example.businessreportgenerator.domain.model.Asset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PortfolioViewModel : ViewModel() {
    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    fun addAsset(asset: Asset) {
        _state.update { currentState ->
            val updatedAssets = currentState.assets + asset
            val totalValue = updatedAssets.sumOf { it.purchasePrice }

            currentState.copy(
                assets = updatedAssets,
                totalPortfolioValue = totalValue,
                isAddAssetDialogVisible = false
            )
        }
    }

    fun showAddAssetDialog() {
        _state.update { it.copy(isAddAssetDialogVisible = true) }
    }

    fun hideAddAssetDialog() {
        _state.update { it.copy(isAddAssetDialogVisible = false) }
    }
}