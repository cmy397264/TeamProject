package com.example.businessreportgenerator.presentation.features.analyst

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.businessreportgenerator.data.domain.AnalystReport
import com.example.businessreportgenerator.data.domain.ReportSentiment
import com.example.businessreportgenerator.data.local.entity.ReportEntity
import com.example.businessreportgenerator.data.local.repository.ReportRepository
import com.example.businessreportgenerator.data.remote.model.ReportRequest
import com.example.businessreportgenerator.data.remote.model.ReportResponse
import com.example.businessreportgenerator.data.remote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class AnalystUiState(
    val selectedReport: AnalystReport? = null,
    val selectedCategory: String? = null,
    val selectedSentiment: ReportSentiment? = null,
)

data class RemoteState(
    val reports: List<AnalystReport> = emptyList(),
    val categories: List<String> = emptyList(),
    val sentiments: List<ReportSentiment> = emptyList()
)

class AnalystViewmodel(private val repository: ReportRepository? = null) : ViewModel() {
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

    fun requestReport(report: ReportRequest) {
        val call = RetrofitClient.ReportService.createReport(report)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                Log.d("reportRequest", report.toString())
                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    if (reportResponse != null) {
                        addReport(reportResponse.toDomain())
                    }
                    Log.d("BigPicture", reportResponse.toString())
                    Log.d("BigPicture", "reportRequest : Success")
                } else {
                    Log.d("BigPicture", "reportRequest : invalid body")
                }
            }
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.d("BigPicture", "reportRequest : network error")
            }
        })
    }

    fun addReport(report: ReportEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository?.insertReport(report)
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