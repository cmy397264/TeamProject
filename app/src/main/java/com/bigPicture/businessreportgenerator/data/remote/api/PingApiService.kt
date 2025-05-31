package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.domain.PingInfo
import retrofit2.Call
import retrofit2.http.GET

interface PingApiService {
    @GET("test/spring")
    fun getPing() : Call<PingInfo>
}