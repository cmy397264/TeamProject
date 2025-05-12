package com.example.businessreportgenerator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.businessreportgenerator.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert
    fun insertStock(stock: StockEntity)
    @Delete
    fun deleteStock(stock: StockEntity)
    @Query("SELECT * FROM stocks")
    fun getAllStocks(): Flow<List<StockEntity>>
}