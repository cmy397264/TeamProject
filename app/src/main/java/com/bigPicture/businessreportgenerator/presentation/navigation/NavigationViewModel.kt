package com.bigPicture.businessreportgenerator.presentation.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NavigationItem.Portfolio.route)
    val state: StateFlow<String> = _state.asStateFlow()

    fun navigateTo(route: String) {
        _state.value = route
    }
}