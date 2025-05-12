package com.example.businessreportgenerator.data.local.repository

import com.example.businessreportgenerator.data.local.dao.ReportDao
import com.example.businessreportgenerator.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val dao: ReportDao) {

    fun observeReports(): Flow<List<ReportEntity>> = dao.observeReports()

    suspend fun insertReport(entity: ReportEntity) = dao.insertReport(entity)
}
