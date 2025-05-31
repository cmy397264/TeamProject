package com.bigPicture.businessreportgenerator.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "assets")
@TypeConverters(MapTypeConverter::class)
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AssetType,
    val purchasePrice: Double,
    val purchaseDate: Long? = null,
    val details: Map<String, String> = emptyMap(),
    // 새로 추가된 속성들
    val ticker: String? = null, // 주식 티커 (예: NVDA, 005930)
    val currentPrice: Double? = null, // 현재 주가
    val lastUpdated: Long? = null // 마지막 가격 업데이트 시간
) {
    // 수익률 계산 (현재가가 있을 때만)
    fun getReturnPercentage(): Double? {
        return currentPrice?.let { current ->
            val avgPrice = details["averagePrice"]?.toDoubleOrNull() ?: purchasePrice
            if (avgPrice > 0) {
                ((current - avgPrice) / avgPrice) * 100
            } else null
        }
    }

    // 수익금 계산
    fun getReturnAmount(): Double? {
        return currentPrice?.let { current ->
            val avgPrice = details["averagePrice"]?.toDoubleOrNull() ?: purchasePrice
            current - avgPrice
        }
    }

    // 현재 시장가치 계산 (보유 수량 기준)
    fun getCurrentValue(): Double? {
        return currentPrice?.let { current ->
            val avgPrice = details["averagePrice"]?.toDoubleOrNull() ?: purchasePrice
            if (avgPrice > 0) {
                val shares = purchasePrice / avgPrice
                current * shares
            } else current
        }
    }
}

enum class AssetType {
    STOCK, ETF, CRYPTO, BOND, REAL_ESTATE, OTHER
}

// Map<String, String>을 위한 TypeConverter
class MapTypeConverter {
    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        return try {
            Gson().fromJson<Map<String, String>>(
                value,
                object : TypeToken<Map<String, String>>() {}.type
            ) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}