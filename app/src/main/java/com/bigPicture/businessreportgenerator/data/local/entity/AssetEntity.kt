package com.bigPicture.businessreportgenerator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bigPicture.businessreportgenerator.data.domain.AssetType
import com.bigPicture.businessreportgenerator.data.local.converter.AssetTypeConverter
import com.bigPicture.businessreportgenerator.data.local.converter.MapConverter

@Entity(tableName = "assets")
@TypeConverters(AssetTypeConverter::class, MapConverter::class)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AssetType,
    val purchasePrice: Double,
    val purchaseDate: Long? = null,
    val details: Map<String, String> = emptyMap(),
    // 새로 추가된 필드들
    val ticker: String? = null, // 주식 티커 (예: NVDA, 005930)
    val currentPrice: Double? = null, // 현재 주가
    val lastUpdated: Long? = null // 마지막 가격 업데이트 시간
)