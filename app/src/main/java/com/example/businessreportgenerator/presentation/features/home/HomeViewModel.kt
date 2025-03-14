package com.example.businessreportgenerator.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.businessreportgenerator.domain.usecase.GetReportsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getReportsUseCase: GetReportsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                getReportsUseCase().collect { reports ->
                    _state.update { it.copy(reports = reports, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "알 수 없는 오류", isLoading = false)
                }
            }
        }
    }
}