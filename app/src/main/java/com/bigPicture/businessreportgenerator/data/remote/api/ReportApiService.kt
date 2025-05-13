package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.model.ReportRequest
import com.bigPicture.businessreportgenerator.data.remote.model.ReportResponse
import com.bigPicture.businessreportgenerator.data.remote.model.StockRequest
import com.bigPicture.businessreportgenerator.data.remote.model.StockResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApiService {
    @POST("report")
    suspend fun createReport(@Body reportRequest: ReportRequest): ReportResponse

    @POST("schedule/register")
    fun registerStock(@Body stockRequest : StockRequest) : Call<StockResponse>
}