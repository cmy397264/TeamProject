package com.bigPicture.businessreportgenerator.presentation.features.analyst

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigPicture.businessreportgenerator.data.domain.AnalystReport
import com.bigPicture.businessreportgenerator.data.domain.ReportSentiment
import com.bigPicture.businessreportgenerator.data.local.entity.ReportEntity
import com.bigPicture.businessreportgenerator.data.local.repository.ReportRepository
import com.bigPicture.businessreportgenerator.data.mapper.toAnalystReport
import com.bigPicture.businessreportgenerator.data.remote.model.ReportRequest
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import com.bigPicture.businessreportgenerator.di.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Room → Repository → ViewModel → UI   (DI 프레임워크 없이)
 */
class AnalystViewmodel(
    context: Context                    // Factory 에서 전달
) : ViewModel() {

    /* ──────────────────────────────────────────────── *
     * 1) Repository & DB stream
     * ──────────────────────────────────────────────── */
    private val repository: ReportRepository =
        ServiceLocator.provideReportRepository(context)

    /** Room → Domain(AnalystReport) */
    private val reportsFlow: StateFlow<List<AnalystReport>> =
        repository.observeReports()
            .map { list -> list.map { it.toAnalystReport() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /* ──────────────────────────────────────────────── *
     * 2) Filter 상태
     * ──────────────────────────────────────────────── */
    data class FilterState(
        val category: String? = null,
        val sentiment: ReportSentiment? = null
    )
    private val _filter = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filter        // 화면에서 collect 가능

    /* 새 이름 setter */
    fun setCategory(c: String?)           = _filter.update { it.copy(category = c) }
    fun setSentiment(s: ReportSentiment?) = _filter.update { it.copy(sentiment = s) }

    /* ──────────────────────────────────────────────── *
     * 3) UI에 노출할 Flow
     * ──────────────────────────────────────────────── */
    val filteredReports: StateFlow<List<AnalystReport>> =
        combine(reportsFlow, _filter) { list, f ->
            list.filter {
                (f.category  == null || it.category  == f.category) &&
                        (f.sentiment == null || it.sentiment == f.sentiment)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Chip에 쓸 목록 */
    val categoriesFlow: StateFlow<List<String>> =
        reportsFlow
            .map { list -> list.map { it.category }.distinct() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val sentiments: List<ReportSentiment> = ReportSentiment.entries

    /* ──────────────────────────────────────────────── *
     * 4) 상세 화면 선택 상태
     * ──────────────────────────────────────────────── */
    private val _selectedReport = MutableStateFlow<AnalystReport?>(null)
    val selectedReport: StateFlow<AnalystReport?> = _selectedReport
    fun setSelectedReport(r: AnalystReport?) { _selectedReport.value = r }

    /* ──────────────────────────────────────────────── *
     * 5)  옛 화면 코드 호환용 alias
     * ──────────────────────────────────────────────── */
    val selectedCategory  get() = _filter.value.category
    val selectedSentiment get() = _filter.value.sentiment
    fun setSelectedCategory(c: String?)           = setCategory(c)
    fun setSelectedSentiment(s: ReportSentiment?) = setSentiment(s)

    /* ──────────────────────────────────────────────── *
     * 6)  서버 요청 → DB 저장
     * ──────────────────────────────────────────────── */
    suspend fun requestReport(req: ReportRequest) {
        try {
            val response = RetrofitClient.ReportService.createReport(req)
            Log.d("BigPicture", "requestReport : $response")
            save(response.toDomain())
        } catch (e : Exception) {
            Log.e("BigPicture", "requestReport : ${e.message}")
        }
    }

    private fun save(entity: ReportEntity) {
        viewModelScope.launch(Dispatchers.IO) {         // ❸
            repository.insertReport(entity)
        }
    }
}
