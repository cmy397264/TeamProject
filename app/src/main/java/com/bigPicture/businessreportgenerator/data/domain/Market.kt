package com.bigPicture.businessreportgenerator.data.domain

enum class Market(
    val displayName: String,
    val currency: Currency
) {
    KOSPI("코스피", Currency.KRW),
    KOSDAQ("코스닥", Currency.KRW),
    NYSE("NYSE", Currency.USD),
    NASDAQ("나스닥", Currency.USD)
}