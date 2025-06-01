package com.bigPicture.businessreportgenerator.data.remote.dto

import com.bigPicture.businessreportgenerator.data.domain.ReportType
import com.bigPicture.businessreportgenerator.data.local.entity.ReportEntity
import com.google.gson.annotations.SerializedName

data class Data(
    val title : String,
    val summaryReport : String,
    val economyReport : String,
    val stockReport : String
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

    fun toDomain(): ReportEntity = ReportEntity(
        title   = data?.title ?: "",
        content = (data?.economyReport + "\n" + data?.stockReport),
        summary = data?.summaryReport ?: "",
        date    = System.currentTimeMillis(),
        type    = ReportType.CUSTOM.toString()
    )
}