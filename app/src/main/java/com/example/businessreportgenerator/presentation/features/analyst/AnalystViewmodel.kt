package com.example.businessreportgenerator.presentation.features.analyst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 외부에서 받아오는 데이터 목록
data class RemoteState(
    val reports: List<AnalystReport> = emptyList(),
    val categories: List<String> = emptyList(),
    val sentiments: List<ReportSentiment> = emptyList()
)

// UiState에 관여하는 데이터
data class AnalystUiState(
    val selectedReport: AnalystReport? = null,
    val selectedCategory: String? = null,
    val selectedSentiment: ReportSentiment? = null,
)

class AnalystViewmodel : ViewModel() {
    private val _remoteState = MutableStateFlow(RemoteState())
    private val _uiState = MutableStateFlow(AnalystUiState())
    val uiState : MutableStateFlow<AnalystUiState> = _uiState
    val remoteState : MutableStateFlow<RemoteState> = _remoteState

    val filteredReports : StateFlow<List<AnalystReport>> =
        combine(_remoteState, _uiState) { remote : RemoteState, ui : AnalystUiState ->
            remote.reports.filter { report ->
                (ui.selectedCategory == null || report.category == ui.selectedCategory) &&
                        (ui.selectedSentiment == null || report.sentiment == ui.selectedSentiment)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        fetchAnalystReports()
    }

    private fun fetchAnalystReports() {
        viewModelScope.launch {
            val reports = DummyReportData.reports
            val sentiments = ReportSentiment.entries
            _remoteState.value = _remoteState.value.copy(
                reports = reports,
                categories = reports.map { it.category }.distinct(),
                sentiments = sentiments
            )
        }
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
}