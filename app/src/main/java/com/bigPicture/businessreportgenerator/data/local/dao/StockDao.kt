package com.bigPicture.businessreportgenerator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bigPicture.businessreportgenerator.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert
    fun insertStock(stock: StockEntity)
    @Delete
    fun deleteStock(stock: StockEntity)
    @Query("SELECT * FROM stocks")
    fun getAllStocks(): Flow<List<StockEntity>>
    @Query("""
    SELECT s.* FROM stocks s
    INNER JOIN (
        SELECT stockName, MAX(date) AS latestDate
        FROM stocks
        GROUP BY stockName
    ) grouped
    ON s.stockName = grouped.stockName AND s.date = grouped.latestDate
    ORDER BY s.date DESC
""")
    fun getLatestStocksGroupedByName(): Flow<List<StockEntity>>
    @Query("UPDATE stocks SET date = :newDate WHERE stockName = :stockName")
    fun updateStockDate(stockName: String, newDate: String)
}