package com.example.businessreportgenerator.data.domain

import java.util.Date

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: Date,
    val type: ReportType
)

enum class ReportType {
    DAILY, WEEKLY, MONTHLY, CUSTOM
}