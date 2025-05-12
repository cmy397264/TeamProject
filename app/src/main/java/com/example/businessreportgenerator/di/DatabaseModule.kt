package com.example.businessreportgenerator.di

import com.example.businessreportgenerator.data.local.AppDatabase
import com.example.businessreportgenerator.data.local.StockViewModel
import com.example.businessreportgenerator.data.local.repository.ReportRepository
import com.example.businessreportgenerator.data.local.repository.AssetRepository
import com.example.businessreportgenerator.data.local.repository.StockRepository
import com.example.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.example.businessreportgenerator.presentation.features.portfolio.PortfolioViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    // 데이터베이스
    single { AppDatabase.getDatabase(androidContext()) }

    // DAO
    single { get<AppDatabase>().reportDao() }
    single { get<AppDatabase>().assetDao() }
    single { get<AppDatabase>().stockDao() }

    // 리포지토리
    single { AssetRepository(get()) }
    single { ReportRepository(get()) }
    single { StockRepository(get()) }


    // ViewModel
    viewModel { PortfolioViewModel(get())}
    viewModel { AnalystViewmodel(get())}
    viewModel { StockViewModel(get()) }
}