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

    // 예시용 포트폴리오
    private val dummySample = listOf(
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


    fun showSamplePortfolioDialog() {
        _state.update { it.copy(
            isSamplePortfolioDialogVisible = true,
            sampleAssets                  = dummySample
        ) }
    }

    fun hideSamplePortfolioDialog() {
        _state.update { it.copy(isSamplePortfolioDialogVisible = false) }
    }

    /**
     * 샘플 포트폴리오 데이터를 생성하는 함수
     */
    fun loadSamplePortfolio() {
        // 기존 자산 초기화
        _state.update { it.copy(assets = emptyList(), totalPortfolioValue = 0.0) }
        }
}