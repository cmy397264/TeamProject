package com.bigPicture.businessreportgenerator.data.remote

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

enum class ApiStatus { LOADING, ERROR, DONE }

data class ApiState(
    val isLoading: ApiStatus = ApiStatus.LOADING,
    val data: String? = null,
    val error: String? = null
)

class ApiViewModel() : ViewModel() {
    private val _state = MutableStateFlow(ApiState())
    val state : StateFlow<ApiState> = _state.asStateFlow()

    suspend fun sendPing() : Boolean = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.ScheduleService.getSchedule().execute()
            Log.d("BigPicture", "ping : ${response.isSuccessful}")
            return@withContext response.isSuccessful
        } catch (e : Exception) {
            Log.d("BigPicture", "ping : ${e.message}")
            return@withContext false
        } as Boolean
    }

    fun updateApiStatus(status: ApiStatus) {
        _state.value = _state.value.copy(isLoading = status)
    }
}