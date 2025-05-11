package com.example.businessreportgenerator.data.remote.api

import com.example.businessreportgenerator.data.remote.model.Schedule
import retrofit2.Call
import retrofit2.http.GET

interface ScheduleApiService {
    @GET("schedule")
    fun getSchedule() : Call<Schedule>
}