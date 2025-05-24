package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.model.InterestResponse
import retrofit2.Response
import retrofit2.http.GET

interface InterestApiService {
    @GET("interests/us")
    suspend fun getInterestUs(): Response<InterestResponse>

    @GET("interests/korea")
    suspend fun getInterestKo(): Response<InterestResponse>
}