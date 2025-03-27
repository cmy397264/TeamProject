package com.example.businessreportgenerator.presentation.features.analyst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalystUiState(
    val selectedReport: AnalystReport? = null,
    // 필터 상태
    val selectedCategory: String? = null,
    val selectedSentiment: ReportSentiment? = null,
    val reports: List<AnalystReport> = emptyList(),
    val categories: List<String> = emptyList(),
    val sentiments: List<ReportSentiment> = ReportSentiment.entries,
)

class AnalystViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(AnalystUiState())
    val uiState : MutableStateFlow<AnalystUiState> = _uiState

    init {
        fetchAnalystReports()
    }

    // 외부에서 Report를 받아와 UiState에 저장하는 함수
    private fun fetchAnalystReports() {
        viewModelScope.launch {
            val reports = DummyReportData.reports
            _uiState.value = _uiState.value.copy(
                reports = reports
            )
            fetchCategories(reports)
        }
    }

    private fun fetchCategories(reports: List<AnalystReport>) {
        _uiState.value = _uiState.value.copy(
            categories = reports.map { it.category }.distinct()
        )
    }

    fun setSelectedReport(report: AnalystReport?) {
        _uiState.update { it.copy(selectedReport = report) }
    }

    fun setSelectedCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSelectedSentiment(sentiment: ReportSentiment?) {
        _uiState.update { it.copy(selectedSentiment = sentiment) }
    }

    fun getFilteredReport() : List<AnalystReport> {
        val reports = _uiState.value.reports
        val selectedCategory = _uiState.value.selectedCategory
        val selectedSentiment = _uiState.value.selectedSentiment

        return when {
            selectedCategory != null && selectedSentiment != null -> {
                reports.filter { it.category == selectedCategory && it.sentiment == selectedSentiment }
            }
            selectedCategory != null -> {
                reports.filter { it.category == selectedCategory }
            }
            selectedSentiment != null -> {
                reports.filter { it.sentiment == selectedSentiment }
            }
            else -> reports
        }
    }
}