package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.domain.Schedule
import retrofit2.Call
import retrofit2.http.GET

interface ScheduleApiService {
    @GET("schedule")
    fun getSchedule() : Call<Schedule>
}