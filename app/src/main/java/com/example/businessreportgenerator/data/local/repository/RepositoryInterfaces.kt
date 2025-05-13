package com.example.businessreportgenerator.data.local.repository

import com.example.businessreportgenerator.data.domain.Asset
import com.example.businessreportgenerator.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

//interface ReportRepositoryInterface {
//    fun insertReport(report: ReportEntity)
//    fun deleteReport(report: ReportEntity)
//    fun getAllReports(): Flow<List<ReportEntity>>
//}

interface AssetRepositoryInterface {
    suspend fun insertAsset(asset: Asset)
    suspend fun deleteAsset(asset: Asset)
    fun getAllAssets(): Flow<List<Asset>>
    suspend fun loadSampleAssets(sampleAssets: List<Asset>)
}

interface StockRepositoryInterface {
    suspend fun insertStock(stock: StockEntity)
    suspend fun deleteStock(stock: StockEntity)
    fun getLatestStocksGroupedByName(): Flow<List<StockEntity>>
    fun getAllStocks(): Flow<List<StockEntity>>
    fun updateStockDate(stockName: String, newDate: String)
}