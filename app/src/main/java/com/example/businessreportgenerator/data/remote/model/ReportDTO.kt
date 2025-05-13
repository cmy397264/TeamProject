package com.example.businessreportgenerator.data.remote.model

import com.example.businessreportgenerator.data.domain.ReportType
import com.example.businessreportgenerator.data.local.entity.ReportEntity
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

    fun toDomain(): ReportEntity = ReportEntity(
        title   = data?.insight?.take(40) ?: "",
        content = data?.insight ?: "",
        summary = data?.summary ?: "",
        date    = System.currentTimeMillis(),
        type    = ReportType.CUSTOM.toString()          // ★ 기본값 넣기 (또는 서버 값 매핑)
    )
}