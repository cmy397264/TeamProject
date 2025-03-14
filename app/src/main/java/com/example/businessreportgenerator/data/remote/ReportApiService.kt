package com.example.businessreportgenerator.data.remote

import com.example.businessreportgenerator.data.model.ReportDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ReportApiService {
    @GET("reports")
    suspend fun getReports(): List<ReportDto>

    @GET("reports/{id}")
    suspend fun getReportById(@Path("id") id: String): ReportDto
}