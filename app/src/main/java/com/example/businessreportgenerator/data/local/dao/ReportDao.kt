package com.example.businessreportgenerator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.businessreportgenerator.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert
    fun insertReport(report: ReportEntity)

    @Delete
    fun deleteReport(report: ReportEntity)

    @Query("SELECT * FROM reports")
    fun selectAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports ORDER BY date DESC")
    fun observeReports(): Flow<List<ReportEntity>>
}