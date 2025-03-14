package com.example.businessreportgenerator.data.model

import com.example.businessreportgenerator.domain.model.Report
import com.example.businessreportgenerator.domain.model.ReportType
import java.util.Date

data class ReportDto(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val type: String
) {
    fun toDomain(): Report {
        return Report(
            id = id,
            title = title,
            description = description,
            createdAt = Date(createdAt), // 간단한 변환 예시
            type = when(type) {
                "daily" -> ReportType.DAILY
                "weekly" -> ReportType.WEEKLY
                "monthly" -> ReportType.MONTHLY
                else -> ReportType.CUSTOM
            }
        )
    }
}