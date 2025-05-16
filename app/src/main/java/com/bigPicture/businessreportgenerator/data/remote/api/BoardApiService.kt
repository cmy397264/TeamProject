package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.model.ReportRequest
import com.bigPicture.businessreportgenerator.data.remote.model.ReportResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BoardApiService {
    @POST("board")
    suspend fun createReport(@Body reportRequest: ReportRequest): ReportResponse

}