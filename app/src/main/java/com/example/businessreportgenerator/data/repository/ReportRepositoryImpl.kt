package com.example.businessreportgenerator.data.repository

import com.example.businessreportgenerator.data.remote.api.ReportApiService
import com.example.businessreportgenerator.domain.model.Report
import com.example.businessreportgenerator.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReportRepositoryImpl(
    private val apiService: ReportApiService
) : ReportRepository {
    override fun getReports(): Flow<List<Report>> = flow {
        // API가 구현되기 전에는 임시 데이터로 대체
        // 실제 구현 시 apiService.getReports() 호출
        emit(emptyList())
    }

    override suspend fun getReportById(id: String): Report {
        // API가 구현되기 전에는 임시 데이터로 대체
        // 실제 구현 시 apiService.getReportById(id) 호출
        throw NotImplementedError("아직 구현되지 않음")
    }
}