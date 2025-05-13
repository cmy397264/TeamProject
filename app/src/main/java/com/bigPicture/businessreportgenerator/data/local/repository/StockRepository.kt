package com.bigPicture.businessreportgenerator.data.local.repository

import com.bigPicture.businessreportgenerator.data.local.dao.StockDao
import com.bigPicture.businessreportgenerator.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

class StockRepository(private val stockDao: StockDao) : StockRepositoryInterface {
    override suspend fun insertStock(stock: StockEntity) = stockDao.insertStock(stock)

    override suspend fun deleteStock(stock: StockEntity) = stockDao.deleteStock(stock)

    override fun getLatestStocksGroupedByName(): Flow<List<StockEntity>> = stockDao.getLatestStocksGroupedByName()

    override fun getAllStocks(): Flow<List<StockEntity>> = stockDao.getAllStocks()
    override fun updateStockDate(stockName: String, newDate: String) = stockDao.updateStockDate(stockName, newDate)
}