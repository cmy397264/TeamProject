package com.example.businessreportgenerator.data.local.repository

import com.example.businessreportgenerator.data.local.dao.ReportDao
import com.example.businessreportgenerator.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) : ReportRepositoryInterface {
    override fun insertReport(report: ReportEntity) = reportDao.insertReport(report)

    override fun deleteReport(report: ReportEntity) = reportDao.deleteReport(report)

    override fun getAllReports(): Flow<List<ReportEntity>> = reportDao.selectAllReports()
}