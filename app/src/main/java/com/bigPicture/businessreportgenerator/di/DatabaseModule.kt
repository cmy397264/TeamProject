package com.bigPicture.businessreportgenerator.di

import com.bigPicture.businessreportgenerator.data.local.AppDatabase
import com.bigPicture.businessreportgenerator.data.local.StockViewModel
import com.bigPicture.businessreportgenerator.data.local.repository.AssetRepository
import com.bigPicture.businessreportgenerator.data.local.repository.ReportRepository
import com.bigPicture.businessreportgenerator.data.local.repository.StockRepository
import com.bigPicture.businessreportgenerator.data.remote.api.FinanceApiService
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import com.bigPicture.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.bigPicture.businessreportgenerator.presentation.features.portfolio.PortfolioViewModel
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

    // API Services
    single<FinanceApiService> { RetrofitClient.FinanceService }

    // 리포지토리
    single { AssetRepository(get()) }
    single { ReportRepository(get()) }
    single { StockRepository(get()) }

    // ViewModel - PortfolioViewModel에 두 개의 파라미터 주입
    viewModel { PortfolioViewModel(get(), get()) }
    viewModel { AnalystViewmodel(get())}
    viewModel { StockViewModel(get()) }
}