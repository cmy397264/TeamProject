package com.example.businessreportgenerator.data.remote.network

import com.example.businessreportgenerator.data.remote.api.ScheduleApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://dockerel.o-r.kr/"

    val ScheduleService : ScheduleApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScheduleApiService::class.java)
    }
}