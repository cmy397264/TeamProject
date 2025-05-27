package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.model.ExchangeResponse
import retrofit2.Response
import retrofit2.http.GET

interface ExchangeApiService {
    @GET("exchanges")
    suspend fun getExchangeUs(): Response<ExchangeResponse>
}