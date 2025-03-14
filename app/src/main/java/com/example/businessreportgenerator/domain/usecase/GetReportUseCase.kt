package com.example.businessreportgenerator.domain.usecase

import com.example.businessreportgenerator.domain.model.Report
import com.example.businessreportgenerator.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow

class GetReportsUseCase(private val repository: ReportRepository) {
    operator fun invoke(): Flow<List<Report>> = repository.getReports()
}