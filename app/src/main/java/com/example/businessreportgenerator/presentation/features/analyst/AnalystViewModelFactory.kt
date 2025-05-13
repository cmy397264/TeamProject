package com.example.businessreportgenerator.presentation.features.analyst

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnalystViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AnalystViewmodel(appContext) as T                // ★ Context만 전달
}
