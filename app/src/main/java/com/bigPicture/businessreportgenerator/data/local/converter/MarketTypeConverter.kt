package com.bigPicture.businessreportgenerator.data.local.converter

import androidx.room.TypeConverter
import com.bigPicture.businessreportgenerator.data.domain.Market

class MarketTypeConverter {
    @TypeConverter
    fun fromMarket(market: Market): String {
        return market.name
    }

    @TypeConverter
    fun toMarket(value: String): Market {
        return Market.valueOf(value)
    }
}