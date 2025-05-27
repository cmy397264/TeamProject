package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.local.repository.AssetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModernPortfolioState(
    val assets: List<Asset> = emptyList(),
    val sampleAssets: List<Asset> = emptyList(),
    val isAddAssetDialogVisible: Boolean = false,
    val isSamplePortfolioDialogVisible: Boolean = false,
    val totalPortfolioValue: Double = 0.0,
    val isLoading: Boolean = false,
    val currentInvestmentTip: InvestmentTip? = null
)

class PortfolioViewModel(private val repository: AssetRepository) : ViewModel() {

    private val _state = MutableStateFlow(ModernPortfolioState())
    val state: StateFlow<ModernPortfolioState> = _state.asStateFlow()

    init {
        // 앱 시작 시 데이터베이스에서 자산 목록 로드
        loadAssets()
        // 랜덤 투자 팁 설정
        setRandomInvestmentTip()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getAllAssets().collect { assets ->
                val totalValue = assets.sumOf { it.purchasePrice }
                _state.update {
                    it.copy(
                        assets = assets,
                        totalPortfolioValue = totalValue,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun setRandomInvestmentTip() {
        _state.update {
            it.copy(currentInvestmentTip = ModernPortfolioSampleData.getRandomInvestmentTip())
        }
    }

    fun addAsset(asset: Asset) {
        viewModelScope.launch {
            repository.insertAsset(asset)
            hideAddAssetDialog()
        }
    }

    fun removeAsset(asset: Asset) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }

    fun showAddAssetDialog() {
        _state.update { it.copy(isAddAssetDialogVisible = true) }
    }

    fun hideAddAssetDialog() {
        _state.update { it.copy(isAddAssetDialogVisible = false) }
    }

    fun showSamplePortfolioDialog() {
        _state.update {
            it.copy(
                isSamplePortfolioDialogVisible = true,
                sampleAssets = ModernPortfolioSampleData.samplePortfolios
            )
        }
    }

    fun hideSamplePortfolioDialog() {
        _state.update { it.copy(isSamplePortfolioDialogVisible = false) }
    }

    fun loadSamplePortfolio() {
        viewModelScope.launch {
            // 기존 자산들 삭제
            _state.value.assets.forEach { asset ->
                repository.deleteAsset(asset)
            }

            // 샘플 자산들 추가
            ModernPortfolioSampleData.samplePortfolios.forEach { asset ->
                repository.insertAsset(asset)
            }
        }
    }

    fun refreshInvestmentTip() {
        setRandomInvestmentTip()
    }
}