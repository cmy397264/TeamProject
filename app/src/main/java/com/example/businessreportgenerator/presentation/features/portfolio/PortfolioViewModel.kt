package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.businessreportgenerator.data.repository.AssetRepository
import com.example.businessreportgenerator.domain.model.Asset
import com.example.businessreportgenerator.domain.model.AssetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PortfolioViewModel(private val repository: AssetRepository) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    init {
        // 앱 시작 시 데이터베이스에서 자산 목록 로드
        viewModelScope.launch {
            repository.getAllAssets().collect { assets ->
                val totalValue = assets.sumOf { it.purchasePrice }
                _state.update {
                    it.copy(
                        assets = assets,
                        totalPortfolioValue = totalValue
                    )
                }
            }
        }
    }

    fun addAsset(asset: Asset) {
        viewModelScope.launch {
            repository.insertAsset(asset)
            hideAddAssetDialog()
        }
    }

    fun showAddAssetDialog() {
        _state.update { it.copy(isAddAssetDialogVisible = true) }
    }

    fun hideAddAssetDialog() {
        _state.update { it.copy(isAddAssetDialogVisible = false) }
    }


    /** 샘플 포트폴리오 모달 열기 */
    fun showSamplePortfolioDialog() {
        _state.update { it.copy(isSamplePortfolioDialogVisible = true) }
    }
    /** 샘플 포트폴리오 모달 닫기 */
    fun hideSamplePortfolioDialog() {
        _state.update { it.copy(isSamplePortfolioDialogVisible = false) }
    }

}