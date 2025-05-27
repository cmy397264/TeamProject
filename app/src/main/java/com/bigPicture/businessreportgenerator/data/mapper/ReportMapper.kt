package com.bigPicture.businessreportgenerator.data.mapper

import android.util.Log
import com.bigPicture.businessreportgenerator.data.domain.AnalystReport
import com.bigPicture.businessreportgenerator.data.domain.GraphData
import com.bigPicture.businessreportgenerator.data.domain.ReportSentiment
import com.bigPicture.businessreportgenerator.data.local.entity.ReportEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date


fun ReportEntity.toAnalystReport(): AnalystReport {
    val gson = Gson()
    val typeToken = object : TypeToken<List<GraphData>>(){}.type
    val graphList = graphDataJson?.let { gson.fromJson<List<GraphData>>(it, typeToken) } ?: emptyList()
    Log.d("BigPicture", "toAnalystReport 파싱 결과: $graphList")

    return AnalystReport(
        id = id ?: 0L,
        title = title,
        summary = summary,
        date = Date(date),
        sentiment = ReportSentiment.NEUTRAL, // 필요시 따로 저장/복원
        category = type,        // ← ReportEntity의 type 컬럼!
        graphData = graphList,  // ← 위에서 파싱한 값!
        detailedContent = content
    )
}
