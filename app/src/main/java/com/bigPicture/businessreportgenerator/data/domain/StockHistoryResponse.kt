package com.bigPicture.businessreportgenerator.data.domain

data class StockHistoryResponse(
    val status: String,
    val message: String,
    val data: List<StockHistoryItem>,
    val code: Int
)