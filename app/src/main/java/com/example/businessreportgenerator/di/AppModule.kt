package com.example.businessreportgenerator.di

import com.example.businessreportgenerator.presentation.features.portfolio.PortfolioViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { PortfolioViewModel() }
    // 다른 의존성들...
}