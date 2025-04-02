package com.example.businessreportgenerator.di

import com.example.businessreportgenerator.data.local.AppDatabase
import com.example.businessreportgenerator.data.repository.AssetRepository
import com.example.businessreportgenerator.presentation.features.portfolio.PortfolioViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    // 데이터베이스
    single { AppDatabase.getDatabase(androidContext()) }

    // DAO
    single { get<AppDatabase>().assetDao() }

    // 리포지토리
    single { AssetRepository(get()) }

    // ViewModel
    viewModel { PortfolioViewModel(get()) }
}