package com.example.businessreportgenerator.data.mapper

import com.example.businessreportgenerator.data.domain.AnalystReport
import com.example.businessreportgenerator.data.domain.ReportSentiment
import com.example.businessreportgenerator.data.local.entity.ReportEntity
import java.util.Date

/** Room Entity → 화면용 AnalystReport */
fun ReportEntity.toAnalystReport(): AnalystReport =
    AnalystReport(
        id              = id ?: 0L,          // Entity id 가 Long? 일 때 null → 0L
        title           = title,
        summary         = summary,
        date            = Date(date),
        sentiment       = ReportSentiment.NEUTRAL, // 서버에서 값 주면 바꿔주세요
        category        = type,                    // Entity.type(String) 그대로
        graphData       = emptyList(),             // 아직 그래프 없음
        detailedContent = content
    )
