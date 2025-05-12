package com.example.businessreportgenerator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stockType: String,
    val stockName: String,
    val stockKeyword : String
)