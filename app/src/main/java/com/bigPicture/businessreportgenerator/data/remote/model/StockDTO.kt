package com.bigPicture.businessreportgenerator.data.remote.model

import com.bigPicture.businessreportgenerator.data.local.entity.StockEntity
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class StockData(
    val stockType: String,
    val stockName : String,
    val stockKeyword: String
)

data class StockRequest (
    val stockType : String,
    val stockName : String,
)

class StockResponse {
    @SerializedName("code") private var code: Int? = null
    @SerializedName("status") private var status: String? = null
    @SerializedName("message") private var message: String? = null
    @SerializedName("data") private var data: StockData? = null

    override fun toString() : String {
        return "StockResponse(code=$code, status=$status, message=$message, data=$data)"
    }

    fun toDomain() : StockEntity {
        return StockEntity(
            stockType = data?.stockType ?: "",
            stockName = data?.stockName ?: "",
            stockKeyword = data?.stockKeyword ?: "",
            date = LocalDate.now().minusDays(1).toString()
        )
    }
}