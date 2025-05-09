package com.example.businessreportgenerator.data.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.businessreportgenerator.data.remote.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

data class ApiState(
    val isLoading: ApiStatus = ApiStatus.LOADING,
    val data: String? = null,
    val error: String? = null
)

class ApiViewModel(apiRepository: ApiRepository) : ViewModel() {
    private val _state = MutableStateFlow(ApiState())
    val state : StateFlow<ApiState> = _state.asStateFlow()

    init {
        fetchData(apiRepository)
    }

    fun fetchData(apiRepository : ApiRepository) {
        viewModelScope.launch {
            try {
                val data = apiRepository.fetchApi()
                if (data)
                    _state.value = ApiState(isLoading = ApiStatus.DONE)
                else {
                    _state.value = ApiState(isLoading = ApiStatus.ERROR, error = "body is blank")
                }
            } catch (e: Exception) {
                _state.value = ApiState(isLoading = ApiStatus.ERROR, error = e.message)
            }
        }
    }
}