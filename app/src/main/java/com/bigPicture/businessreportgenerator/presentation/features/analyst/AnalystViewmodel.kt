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
import com.bigPicture.businessreportgenerator.data.remote.dto.ReportRequest
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import com.bigPicture.businessreportgenerator.data.remote.repository.FinanceStatsRepository
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
import java.util.Date

/**
 * Room → Repository → ViewModel → UI   (DI 프레임워크 없이)
 */
class AnalystViewmodel(
    context: Context                    // Factory 에서 전달
) : ViewModel() {

    private val financeRepository = FinanceStatsRepository(RetrofitClient.FinanceService)

    fun createAnalystReportWithFinance(stockName: String) {
        viewModelScope.launch {
            val exchanges = financeRepository.api.getExchanges().data
            val stocks = financeRepository.api.getStocks("TSLA").data
            val usRates = financeRepository.api.getUsInterests().data
            val krRates = financeRepository.api.getKrInterests().data

            Log.d("BigPicture", "환율 데이터: $exchanges")
            Log.d("BigPicture", "TSLA 주가 데이터: $stocks")
            Log.d("BigPicture", "미국 금리 데이터: $usRates")
            Log.d("BigPicture", "한국 금리 데이터: $krRates")

            val graphs = financeRepository.fetchFinanceGraphs(stockName)
            val report = AnalystReport(
                title = "${stockName} 분석 리포트",
                summary = "AI 기반 금융지표와 시장 주요 동향 요약",
                category = "금융지표",
                date = Date(),
                sentiment = ReportSentiment.NEUTRAL,
                detailedContent = "# ${stockName} 주요 이슈\n\n- 최근 시장 동향\n- 주요 금융지표 변화\n",
                graphData = graphs
            )
            // 예: Room에 저장
            save(report.toEntity()) // toEntity()는 Domain → Entity 변환 함수
            // 필요시 _selectedReport.value = report 등 UI에 바로 할당도 가능
        }
    }


    /* ──────────────────────────────────────────────── *
     * 1) Repository & DB stream
     * ──────────────────────────────────────────────── */
    private val repository: ReportRepository =
        ServiceLocator.provideReportRepository(context)

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
