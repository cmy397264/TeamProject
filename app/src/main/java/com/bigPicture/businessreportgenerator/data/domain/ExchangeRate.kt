package com.bigPicture.businessreportgenerator.data.domain

data class ExchangeRate(
    val rate: Double, // USD to KRW 환율
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun usdToKrw(usdAmount: Double): Double = usdAmount * rate
    fun krwToUsd(krwAmount: Double): Double = krwAmount / rate
}