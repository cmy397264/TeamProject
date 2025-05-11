package com.example.businessreportgenerator.data.remote.model

import com.google.gson.annotations.SerializedName

data class Data(
    val summary : String,
    val insight : String
)

data class ReportRequest(
    val reportType: String?,
    val stockName: String?,
    val riskTolerance: String,
    val reportDifficultyLevel: String,
    val interestAreas: List<String>
)

class ReportResponse {
    @SerializedName("code") private var code: Int? = null
    @SerializedName("status") private var status: String? = null
    @SerializedName("message") private var message: String? = null
    @SerializedName("data") private var data: Data? = null

    override fun toString() : String {
        return "ReportResponse(code=$code, status=$status, message=$message, data=$data)"
    }

    fun toDomain() {

    }
}