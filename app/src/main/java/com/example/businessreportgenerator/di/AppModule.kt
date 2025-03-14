package com.example.businessreportgenerator.di

import com.example.businessreportgenerator.data.remote.ReportApiService
import com.example.businessreportgenerator.data.repository.ReportRepositoryImpl
import com.example.businessreportgenerator.domain.repository.ReportRepository
import com.example.businessreportgenerator.domain.usecase.GetReportsUseCase
import com.example.businessreportgenerator.presentation.features.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    // API
    single {
        Retrofit.Builder()
            .baseUrl("https://your-api-url.com/") // 실제 API URL로 변경 필요
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportApiService::class.java)
    }

    // Repository
    single<ReportRepository> { ReportRepositoryImpl(get()) }

    // Use Cases
    single { GetReportsUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
}