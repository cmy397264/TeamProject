package com.example.businessreportgenerator.presentation.features.analyst

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

/**
 * 더미 보고서 데이터
 */
object DummyReportData {
    val reports = listOf(
        AnalystReport(
            id = "1",
            title = "2024년 2분기 반도체 시장 전망",
            summary = "반도체 시장의 견조한 수요와 AI 칩 성장으로 인해 긍정적인 전망이 유지되고 있습니다.",
            date = Date(System.currentTimeMillis() - 2 * 24 * 3600 * 1000), // 2일 전
            sentiment = ReportSentiment.POSITIVE,
            category = "산업 분석",
            graphData = listOf(
                GraphData(
                    type = GraphType.LINE_CHART,
                    title = "반도체 수요 추이",
                    description = "2023-2024 분기별 반도체 시장 수요 추이",
                    data = mapOf(
                        "2023 Q1" to 65f,
                        "2023 Q2" to 72f,
                        "2023 Q3" to 78f,
                        "2023 Q4" to 85f,
                        "2024 Q1" to 90f,
                        "2024 Q2" to 96f
                    )
                ),
                GraphData(
                    type = GraphType.PIE_CHART,
                    title = "반도체 부문별 비중",
                    description = "2024년 2분기 반도체 부문별 시장 점유율",
                    data = mapOf(
                        "메모리" to 35f,
                        "비메모리" to 25f,
                        "AI 칩" to 20f,
                        "시스템 반도체" to 15f,
                        "기타" to 5f
                    )
                )
            ),
            detailedContent = """
                # 2024년 2분기 반도체 시장 전망
                
                ## 개요
                2024년 2분기 반도체 시장은 전반적인 경기 회복과 함께 AI 관련 수요 증가로 인해 긍정적인 성장세를 보일 것으로 예상됩니다. 특히 메모리 반도체 가격 상승과 함께 시스템 반도체의 수요도 지속적으로 확대될 전망입니다.
                
                ## 주요 포인트
                - 메모리 반도체 가격 상승세 지속
                - AI 관련 칩 수요 급증
                - 파운드리 업체들의 생산능력 확대
                - 주요 IT 기업들의 신제품 출시에 따른 수요 증가
                
                ## 투자 전략
                - 메모리 반도체 업체 비중 확대 고려
                - AI 칩 설계 기업에 대한 관심 필요
                - 반도체 장비 업체들의 실적 개선 기대
                
                ## 리스크 요인
                - 미중 무역 갈등 지속 가능성
                - 주요국 통화정책 변화에 따른 시장 변동성
                - 공급망 이슈 가능성
                
                ## 결론
                2024년 2분기는 반도체 업황의 본격적인 회복기로 진입하는 시점으로 판단됩니다. 장기 투자자들에게 유리한 매수 기회를 제공할 것으로 예상됩니다.
            """.trimIndent()
        ),
        AnalystReport(
            id = "2",
            title = "원자재 가격 상승과 인플레이션 영향 분석",
            summary = "글로벌 원자재 가격 상승으로 인한 인플레이션 압력이 높아지고 있어 관련 산업에 주의가 필요합니다.",
            date = Date(System.currentTimeMillis() - 5 * 24 * 3600 * 1000), // 5일 전
            sentiment = ReportSentiment.CAUTION,
            category = "거시경제",
            graphData = listOf(
                GraphData(
                    type = GraphType.LINE_CHART,
                    title = "원자재 가격 지수",
                    description = "2023-2024 주요 원자재 가격 지수 변화",
                    data = mapOf(
                        "2023-01" to 100f,
                        "2023-04" to 105f,
                        "2023-07" to 110f,
                        "2023-10" to 118f,
                        "2024-01" to 125f,
                        "2024-04" to 135f
                    )
                ),
                GraphData(
                    type = GraphType.BAR_CHART,
                    title = "주요국 물가상승률",
                    description = "2024년 1분기 주요국 물가상승률 비교",
                    data = mapOf(
                        "미국" to 3.8f,
                        "유로존" to 2.7f,
                        "한국" to 2.3f,
                        "일본" to 2.5f,
                        "중국" to 1.9f
                    )
                )
            ),
            detailedContent = """
                # 원자재 가격 상승과 인플레이션 영향 분석
                
                ## 개요
                최근 글로벌 원자재 가격이 상승세를 보이면서 인플레이션 압력이 증가하고 있습니다. 특히 에너지와 금속 가격의 상승이 두드러지며, 이로 인한 제조업체의 원가 부담이 커지고 있습니다.
                
                ## 주요 원인
                - 글로벌 경기 회복에 따른 수요 증가
                - 공급망 문제 지속
                - 지정학적 리스크 증가
                - 주요국 통화정책 전환
                
                ## 산업별 영향
                - 소비재: 마진 압박 심화
                - 에너지: 원가 상승으로 실적 개선
                - 제조업: 원가 상승으로 수익성 악화
                - 유통업: 가격 전가 어려움으로 수익성 악화
                
                ## 투자 전략
                - 가격 전가력이 높은 기업 선호
                - 원자재 관련주 단기 모멘텀 기대
                - 인플레이션 방어 자산 배분 고려
                
                ## 전망
                중앙은행들의 금리 인상 기조가 유지됨에 따라 하반기에는 인플레이션 압력이 다소 완화될 가능성이 있으나, 단기적으로는 원자재 가격 상승에 따른 영향에 주의가 필요합니다.
            """.trimIndent()
        ),
        AnalystReport(
            id = "3",
            title = "테슬라 1분기 실적 분석 및 전망",
            summary = "테슬라의 1분기 실적이 시장 예상을 하회하며 전기차 시장 경쟁 심화에 따른 우려가 커지고 있습니다.",
            date = Date(System.currentTimeMillis() - 8 * 24 * 3600 * 1000), // 8일 전
            sentiment = ReportSentiment.NEGATIVE,
            category = "기업 분석",
            graphData = listOf(
                GraphData(
                    type = GraphType.BAR_CHART,
                    title = "테슬라 분기별 판매량",
                    description = "2023-2024 테슬라 분기별 차량 판매량 (천 대)",
                    data = mapOf(
                        "2023 Q1" to 422f,
                        "2023 Q2" to 466f,
                        "2023 Q3" to 435f,
                        "2023 Q4" to 484f,
                        "2024 Q1" to 387f
                    )
                ),
                GraphData(
                    type = GraphType.LINE_CHART,
                    title = "테슬라 주가 추이",
                    description = "최근 6개월 테슬라 주가 변동",
                    data = mapOf(
                        "2023-11" to 235f,
                        "2023-12" to 252f,
                        "2024-01" to 220f,
                        "2024-02" to 202f,
                        "2024-03" to 175f,
                        "2024-04" to 165f
                    )
                )
            ),
            detailedContent = """
                # 테슬라 1분기 실적 분석 및 전망
                
                ## 실적 요약
                테슬라의 2024년 1분기 실적은 시장 예상을 하회했습니다. 매출액은 전년 동기 대비 3% 감소한 213억 달러, 순이익은 55% 감소한 13억 달러를 기록했습니다. 차량 판매량은 38.7만대로 전년 동기 대비 8.5% 감소했습니다.
                
                ## 실적 부진 원인
                - 중국 시장에서의 경쟁 심화
                - 주요 시장의 전기차 보조금 축소
                - 신모델 출시 지연
                - 글로벌 금리 인상에 따른 수요 감소
                
                ## 주요 경쟁 현황
                - 중국 전기차 업체들의 가격 경쟁력 강화
                - 전통 자동차 업체들의 전기차 라인업 확대
                - 애플 등 신규 업체들의 시장 진입 가능성
                
                ## 투자 의견
                현재 테슬라의 주가는 실적 부진과 경쟁 심화 우려로 하락세를 보이고 있습니다. 단기적으로는 추가적인 하락 가능성이 있으며, 2분기 실적 발표 이전까지는 관망하는 것이 바람직해 보입니다.
                
                ## 주가 전망
                3분기까지는 투자심리 개선이 어려울 것으로 보이며, 4분기 이후 신모델 출시와 함께 실적 개선 가능성이 있습니다. 현재 주가 수준에서의 신규 매수는 권장하지 않습니다.
            """.trimIndent()
        ),
        AnalystReport(
            id = "4",
            title = "국내 부동산 시장 동향과 투자 전략",
            summary = "국내 부동산 시장은 금리 인하 기대감으로 인한 관망세가 지속되고 있으며, 지역별 차별화가 심화되고 있습니다.",
            date = Date(System.currentTimeMillis() - 12 * 24 * 3600 * 1000), // 12일 전
            sentiment = ReportSentiment.NEUTRAL,
            category = "부동산",
            graphData = listOf(
                GraphData(
                    type = GraphType.LINE_CHART,
                    title = "주요 지역 부동산 가격 지수",
                    description = "2023-2024 주요 지역 부동산 가격 변동률(%)",
                    data = mapOf(
                        "2023 Q1" to -1.2f,
                        "2023 Q2" to -0.8f,
                        "2023 Q3" to -0.3f,
                        "2023 Q4" to 0.2f,
                        "2024 Q1" to 0.5f
                    )
                ),
                GraphData(
                    type = GraphType.BAR_CHART,
                    title = "지역별 거래량 변화",
                    description = "전년 동기 대비 2024년 1분기 거래량 변화율(%)",
                    data = mapOf(
                        "서울" to 15f,
                        "경기" to 12f,
                        "인천" to 8f,
                        "부산" to 5f,
                        "대구" to -2f,
                        "광주" to -5f
                    )
                )
            ),
            detailedContent = """
                # 국내 부동산 시장 동향과 투자 전략
                
                ## 시장 개요
                국내 부동산 시장은 2023년 하반기부터 하락세가 둔화되며 일부 지역에서는 반등 조짐을 보이고 있습니다. 금리 인하 기대감과 함께 정부의 규제 완화 정책이 시장 심리 개선에 영향을 미치고 있습니다.
                
                ## 지역별 동향
                - 서울: 강남권 위주로 거래량 회복세
                - 경기/인천: 교통망 개선 지역 중심으로 상승
                - 지방: 인구 감소 지역은 여전히 약세
                
                ## 유형별 동향
                - 아파트: 중소형 위주로 회복세
                - 오피스텔: 임대수익률 하락으로 수요 감소
                - 상업용 부동산: 업종별 양극화 심화
                
                ## 투자 전략
                - 단기: 금리 동향을 주시하며 관망 필요
                - 중기: 교통망 개선 지역 위주로 접근
                - 장기: 인구 유입 지역 중심의 선별적 투자
                
                ## 위험 요인
                - 글로벌 금리 상승 가능성
                - 가계 부채 부담 증가
                - 경기 침체 장기화 가능성
                
                ## 결론
                부동산 시장은 지역별, 유형별 차별화가 더욱 심화될 것으로 예상됩니다. 단기적인 가격 상승보다는 중장기적 관점에서의 선별적 투자가 필요한 시점입니다.
            """.trimIndent()
        ),
        AnalystReport(
            id = "5",
            title = "2024년 2분기 글로벌 경제 전망",
            summary = "글로벌 경제는 완만한 회복세를 보이고 있으나, 각국의 통화정책 변화와 지정학적 리스크에 주의가 필요합니다.",
            date = Date(System.currentTimeMillis() - 15 * 24 * 3600 * 1000), // 15일 전
            sentiment = ReportSentiment.NEUTRAL,
            category = "거시경제",
            graphData = listOf(
                GraphData(
                    type = GraphType.BAR_CHART,
                    title = "주요국 경제성장률 전망",
                    description = "2024년 주요국 GDP 성장률 전망(%)",
                    data = mapOf(
                        "미국" to 2.1f,
                        "중국" to 4.5f,
                        "유로존" to 1.3f,
                        "일본" to 1.0f,
                        "한국" to 2.4f,
                        "인도" to 6.8f
                    )
                ),
                GraphData(
                    type = GraphType.LINE_CHART,
                    title = "글로벌 금리 추이",
                    description = "주요국 기준금리 변화(%)",
                    data = mapOf(
                        "2023 Q2" to 4.7f,
                        "2023 Q3" to 5.0f,
                        "2023 Q4" to 5.25f,
                        "2024 Q1" to 5.25f,
                        "2024 Q2(E)" to 5.0f,
                        "2024 Q3(E)" to 4.75f
                    )
                )
            ),
            detailedContent = """
                # 2024년 2분기 글로벌 경제 전망
                
                ## 글로벌 경제 현황
                2024년 2분기 글로벌 경제는 인플레이션 압력 완화와 함께 완만한 회복세를 보일 것으로 예상됩니다. 다만 지역별로 경기 사이클의 차이가 크고, 지정학적 리스크가 지속되고 있어 변동성은 이어질 전망입니다.
                
                ## 주요국 경제 동향
                - 미국: 견조한 고용 시장과 함께 소비 회복세 지속
                - 유럽: 제조업 부진 속 서비스업 중심의 완만한 회복
                - 중국: 부동산 시장 불안 지속, 소비 회복 더딤
                - 일본: 엔화 약세에 따른 수출 개선 기대
                
                ## 통화정책 전망
                - 미 연준: 6월 금리 인하 가능성 높아짐
                - ECB: 2분기 내 금리 인하 예상
                - 한국은행: 기준금리 동결 기조 유지 전망
                
                ## 주요 위험 요인
                - 중동 및 우크라이나 지정학적 리스크
                - 인플레이션 재점화 가능성
                - 미중 무역 갈등 지속
                
                ## 자산별 투자 전략
                - 주식: 경기 민감주 비중 점진적 확대
                - 채권: 장기물 비중 확대 고려
                - 원자재: 지정학적 리스크에 따른 변동성 주의
                - 외환: 달러 약세 기조 속 신흥국 통화 선별적 접근
                
                ## 결론
                2분기는 글로벌 통화정책의 전환점이 될 가능성이 높습니다. 경기 회복과 금리 인하 기대감으로 위험자산에 우호적인 환경이 조성될 수 있으나, 지정학적 리스크와 인플레이션 향방에 대한 지속적인 모니터링이 필요합니다.
            """.trimIndent()
        )
    )
}