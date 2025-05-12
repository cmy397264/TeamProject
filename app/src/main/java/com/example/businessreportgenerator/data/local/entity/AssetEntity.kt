package com.example.businessreportgenerator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.businessreportgenerator.data.domain.Asset
import com.example.businessreportgenerator.data.domain.AssetType

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val type: AssetType,
    val purchasePrice: Double,
    val purchaseDate: Long? = null,
    val details: Map<String, String> = emptyMap()
) {
    // 도메인 모델로 변환하는 확장 함수
    fun toAsset(): Asset {
        return Asset(
            id = id,
            name = name,
            type = type,
            purchasePrice = purchasePrice,
            purchaseDate = purchaseDate,
            details = details
        )
    }

    companion object {
        // Asset 도메인 모델을 Entity로 변환하는 함수
        fun fromAsset(asset: Asset): AssetEntity {
            return AssetEntity(
                id = 0L,                  // 새 삽입 시 항상 0L → Room 이 신규 id 발급
                name = asset.name,
                type = asset.type,
                purchasePrice = asset.purchasePrice,
                purchaseDate = asset.purchaseDate,
                details = asset.details
            )
        }
    }
}