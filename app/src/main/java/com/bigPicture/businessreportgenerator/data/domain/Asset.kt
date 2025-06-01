package com.bigPicture.businessreportgenerator.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bigPicture.businessreportgenerator.data.local.converter.MapTypeConverter

@Entity(tableName = "assets")
@TypeConverters(MapTypeConverter::class)
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AssetType,
    val purchasePrice: Double, // 원화로 구매한 총금액 (주식수 * 단가)
    val purchaseDate: Long? = null,
    val details: Map<String, String> = emptyMap(),
    val ticker: String? = null,
    val currentPrice: Double? = null, // 현재 주가 (해당 시장 통화)
    val lastUpdated: Long? = null
) {
    // Market 정보를 details에서 가져오기
    val market: Market?
        get() = details["market"]?.let { marketName ->
            try {
                Market.valueOf(marketName)
            } catch (e: Exception) {
                when (marketName) {
                    "코스피" -> Market.KOSPI
                    "코스닥" -> Market.KOSDAQ
                    "나스닥" -> Market.NASDAQ
                    "NYSE" -> Market.NYSE
                    else -> null
                }
            }
        }

    // 구매시 개별 주가 (해당 시장 통화)
    val purchasePricePerShare: Double?
        get() = details["purchasePricePerShare"]?.toDoubleOrNull()

    // 보유 주식 수
    val shares: Double?
        get() = details["shares"]?.toDoubleOrNull()

    // 수익률 계산 (%)
    fun getReturnPercentage(): Double? {
        return currentPrice?.let { current ->
            purchasePricePerShare?.let { purchasePerShare ->
                if (purchasePerShare > 0) {
                    ((current - purchasePerShare) / purchasePerShare) * 100
                } else null
            }
        }
    }

    // 수익금 계산 (해당 시장 통화)
    fun getReturnAmountInMarketCurrency(): Double? {
        return currentPrice?.let { current ->
            purchasePricePerShare?.let { purchasePerShare ->
                shares?.let { shareCount ->
                    (current - purchasePerShare) * shareCount
                }
            }
        }
    }

    // 수익금 계산 (원화)
    fun getReturnAmountInKRW(exchangeRate: ExchangeRate? = null): Double? {
        return getReturnAmountInMarketCurrency()?.let { returnAmount ->
            when (market?.currency) {
                Currency.KRW -> returnAmount
                Currency.USD -> exchangeRate?.usdToKrw(returnAmount) ?: (returnAmount * 1300.0)
                null -> returnAmount
            }
        }
    }

    // 현재 시장가치 (해당 시장 통화)
    fun getCurrentValueInMarketCurrency(): Double? {
        return currentPrice?.let { current ->
            shares?.let { shareCount ->
                current * shareCount
            }
        }
    }

    // 현재 시장가치 (원화)
    fun getCurrentValueInKRW(exchangeRate: ExchangeRate? = null): Double? {
        return getCurrentValueInMarketCurrency()?.let { currentValue ->
            when (market?.currency) {
                Currency.KRW -> currentValue
                Currency.USD -> exchangeRate?.usdToKrw(currentValue) ?: (currentValue * 1300.0)
                null -> currentValue
            }
        } ?: purchasePrice // 현재가가 없으면 구매가격 반환
    }

    // 구매가격 (해당 시장 통화)
    fun getPurchaseValueInMarketCurrency(): Double? {
        return purchasePricePerShare?.let { pricePerShare ->
            shares?.let { shareCount ->
                pricePerShare * shareCount
            }
        }
    }

    val Asset.purchasePricePerShare: Double?
        get() = details["purchasePricePerShare"]?.toDoubleOrNull()

    val Asset.shares: Double?
        get() = details["shares"]?.toDoubleOrNull()

    val Asset.market: Market?
        get() = details["market"]?.let { marketName ->
            try {
                Market.valueOf(marketName)
            } catch (e: Exception) {
                null
            }
        }

}
