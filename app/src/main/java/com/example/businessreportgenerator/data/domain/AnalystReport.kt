package com.example.businessreportgenerator.data.domain

import androidx.compose.ui.graphics.Color
import java.util.Date

/**
 * 보고서 감정(분위기) 타입
 */
enum class ReportSentiment {
    POSITIVE, // 호재
    NEUTRAL,  // 중립
    CAUTION,  // 주의
    NEGATIVE  // 위험
}

/**
 * 보고서 감정 타입에 대한 확장 함수
 */
fun ReportSentiment.getDisplayName(): String {
    return when (this) {
        ReportSentiment.POSITIVE -> "호재"
        ReportSentiment.NEUTRAL -> "중립"
        ReportSentiment.CAUTION -> "주의"
        ReportSentiment.NEGATIVE -> "위험"
    }
}

/**
 * 보고서 감정 타입에 대한 색상 확장 함수
 */
fun ReportSentiment.getColor(): Color {
    return when (this) {
        ReportSentiment.POSITIVE -> Color(0xFF34C759) // 초록색
        ReportSentiment.NEUTRAL -> Color(0xFF007AFF) // 파란색
        ReportSentiment.CAUTION -> Color(0xFFFF9500) // 주황색
        ReportSentiment.NEGATIVE -> Color(0xFFFF3B30) // 빨간색
    }
}

/**
 * 그래프 데이터 타입
 */
enum class GraphType {
    LINE_CHART, BAR_CHART, PIE_CHART
}

/**
 * 보고서 데이터 모델
 */
data class AnalystReport(
    val id: String,
    val title: String,
    val summary: String,
    val date: Date,
    val sentiment: ReportSentiment,
    val category: String,
    val graphData: List<GraphData>,
    val detailedContent: String
)

/**
 * 그래프 데이터 모델
 */
data class GraphData(
    val type: GraphType,
    val title: String,
    val description: String,
    val data: Map<String, Float>
)