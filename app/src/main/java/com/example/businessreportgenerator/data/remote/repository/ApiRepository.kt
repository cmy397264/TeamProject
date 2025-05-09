package com.example.businessreportgenerator.data.remote.repository

import com.example.businessreportgenerator.data.remote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiRepository {
    suspend fun fetchApi() : Boolean{
        return fetchSchedule()
    }

    suspend fun fetchSchedule() : Boolean = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.ScheduleService.getSchedule().execute()
            return@withContext response.isSuccessful
        } catch (e : Exception) {
            return@withContext false
        } as Boolean
    }
}