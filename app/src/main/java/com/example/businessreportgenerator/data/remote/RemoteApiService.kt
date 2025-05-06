package com.example.businessreportgenerator.data.remote

import retrofit2.Call
import retrofit2.http.GET

interface RemoteApiService {
    @GET("api/v1/schedule")
    fun getSchedule() : Call<Schedule>
}