package com.example.businessreportgenerator.data.remote.api

import com.example.businessreportgenerator.data.remote.model.ReportRequest
import com.example.businessreportgenerator.data.remote.model.ReportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApiService {
    @POST("report")
    fun createReport(@Body reportRequest: ReportRequest): Call<ReportResponse>
}