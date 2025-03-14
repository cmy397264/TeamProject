package com.example.businessreportgenerator.presentation.features.home

import com.example.businessreportgenerator.domain.model.Report

data class HomeState(
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)