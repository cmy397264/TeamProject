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

    /**
     * 샘플 포트폴리오 데이터를 생성하는 함수
     */
    fun loadSamplePortfolio() {
        // 기존 자산 초기화
        _state.update { it.copy(assets = emptyList(), totalPortfolioValue = 0.0) }

        // 샘플 자산 목록
        val sampleAssets = listOf(
            Asset(
                name = "삼성전자",
                type = AssetType.STOCK,
                purchasePrice = 15600000.0,
                details = mapOf(
                    "market" to "코스피",
                    "averagePrice" to "78,000"
                )
            ),
            Asset(
                name = "애플",
                type = AssetType.STOCK,
                purchasePrice = 9800000.0,
                details = mapOf(
                    "market" to "나스닥",
                    "averagePrice" to "196.00"
                )
            ),
            Asset(
                name = "강남 아파트",
                type = AssetType.REAL_ESTATE,
                purchasePrice = 120000000.0,
                details = mapOf(
                    "address" to "서울특별시 강남구",
                    "purchaseDate" to "2021-06-15"
                )
            ),
            Asset(
                name = "KODEX 200",
                type = AssetType.ETF,
                purchasePrice = 8500000.0,
                details = mapOf(
                    "market" to "코스피",
                    "averagePrice" to "42,500"
                )
            ),
            Asset(
                name = "비트코인",
                type = AssetType.CRYPTO,
                purchasePrice = 7200000.0,
                details = mapOf(
                    "averagePrice" to "72,000,000"
                )
            ),
            Asset(
                name = "국채 3년물",
                type = AssetType.BOND,
                purchasePrice = 10000000.0,
                details = mapOf(
                    "averagePrice" to "10,000,000",
                    "maturityDate" to "2026-03-15"
                )
            ),
            Asset(
                name = "테슬라",
                type = AssetType.STOCK,
                purchasePrice = 5400000.0,
                details = mapOf(
                    "market" to "나스닥",
                    "averagePrice" to "180.00"
                )
            )
        )

        // 샘플 데이터 업데이트
        var totalValue = 0.0
        sampleAssets.forEach { totalValue += it.purchasePrice }

        _state.update {
            it.copy(
                assets = sampleAssets,
                totalPortfolioValue = totalValue
            )
        }
    }
}