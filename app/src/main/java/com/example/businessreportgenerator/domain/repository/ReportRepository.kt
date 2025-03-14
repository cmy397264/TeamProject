package com.example.businessreportgenerator.domain.repository

import com.example.businessreportgenerator.domain.model.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReports(): Flow<List<Report>>
    suspend fun getReportById(id: String): Report
    // 기타 필요한 메서드...
}