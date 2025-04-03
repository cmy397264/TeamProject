package com.example.businessreportgenerator.data.local.converter

import androidx.room.TypeConverter
import com.example.businessreportgenerator.domain.model.AssetType

class AssetTypeConverter {
    @TypeConverter
    fun fromAssetType(assetType: AssetType): String {
        return assetType.name
    }

    @TypeConverter
    fun toAssetType(value: String): AssetType {
        return AssetType.valueOf(value)
    }
}