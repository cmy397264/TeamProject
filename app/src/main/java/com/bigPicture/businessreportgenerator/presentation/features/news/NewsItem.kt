package com.bigPicture.businessreportgenerator.presentation.features.news

import java.util.Date

/**
 * 뉴스 아이템 데이터 모델
 */
data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val source: String,
    val publishedAt: Date,
    val imageUrl: String? = null,
    val category: NewsCategory
)

/**
 * 뉴스 카테고리
 */
enum class NewsCategory {
    ECONOMY, STOCK_MARKET, REAL_ESTATE, CRYPTO, INTERNATIONAL, POLICY
}

/**
 * 카테고리 확장 함수
 */
fun NewsCategory.getDisplayName(): String {
    return when (this) {
        NewsCategory.ECONOMY -> "경제"
        NewsCategory.STOCK_MARKET -> "주식"
        NewsCategory.REAL_ESTATE -> "부동산"
        NewsCategory.CRYPTO -> "암호화폐"
        NewsCategory.INTERNATIONAL -> "국제"
        NewsCategory.POLICY -> "정책"
    }
}

/**
 * 카테고리 색상 가져오기
 */
fun NewsCategory.getColor(): androidx.compose.ui.graphics.Color {
    return when (this) {
        NewsCategory.ECONOMY -> androidx.compose.ui.graphics.Color(0xFF007AFF) // 파란색
        NewsCategory.STOCK_MARKET -> androidx.compose.ui.graphics.Color(0xFF34C759) // 녹색
        NewsCategory.REAL_ESTATE -> androidx.compose.ui.graphics.Color(0xFFFF9500) // 주황색
        NewsCategory.CRYPTO -> androidx.compose.ui.graphics.Color(0xFF5856D6) // 보라색
        NewsCategory.INTERNATIONAL -> androidx.compose.ui.graphics.Color(0xFFAF52DE) // 연보라색
        NewsCategory.POLICY -> androidx.compose.ui.graphics.Color(0xFFFF2D55) // 빨간색
    }
}

/**
 * 더미 뉴스 데이터
 */
object DummyNewsData {
    val news = listOf(
        NewsItem(
            id = "1",
            title = "미 연준, 6월 기준금리 동결 가능성 시사",
            summary = "미국 연방준비제도(Fed·연준)가 6월 기준금리 동결 가능성을 시사했습니다. 연준은 인플레이션이 목표치인 2%에 근접했다고 판단하며, 경제 성장을 지원하기 위해 금리 인하를 고려하고 있습니다.",
            source = "경제신문",
            publishedAt = Date(System.currentTimeMillis() - 3600000), // 1시간 전
            category = NewsCategory.POLICY
        ),
        NewsItem(
            id = "2",
            title = "삼성전자, 2분기 실적 호전 예상",
            summary = "삼성전자가 반도체 시장 회복세에 힘입어 2분기 실적이 호전될 것으로 예상됩니다. 업계 전문가들은 메모리 가격 반등과 AI 관련 수요 증가로 매출과 영업이익이 모두 상승할 것으로 전망했습니다.",
            source = "테크뉴스",
            publishedAt = Date(System.currentTimeMillis() - 7200000), // 2시간 전
            category = NewsCategory.STOCK_MARKET
        ),
        NewsItem(
            id = "3",
            title = "서울 아파트 거래량 6개월 만에 반등",
            summary = "서울 아파트 거래량이 6개월 만에 반등했습니다. 한국부동산원에 따르면 지난달 서울 아파트 매매 거래량은 전월 대비 23% 증가했으며, 이는 정부의 부동산 규제 완화 정책이 시장에 긍정적인 영향을 미치고 있다는 분석입니다.",
            source = "부동산뉴스",
            publishedAt = Date(System.currentTimeMillis() - 14400000), // 4시간 전
            category = NewsCategory.REAL_ESTATE
        ),
        NewsItem(
            id = "4",
            title = "비트코인, 7만 달러 돌파 후 조정 국면",
            summary = "비트코인이 7만 달러를 돌파한 후 조정 국면에 들어갔습니다. 시장 전문가들은 단기적으로는 조정이 있을 수 있지만 장기적으로는 기관 투자자들의 참여와 ETF 출시 등이 가격 상승에 긍정적 요인으로 작용할 것이라고 전망했습니다.",
            source = "코인데일리",
            publishedAt = Date(System.currentTimeMillis() - 28800000), // 8시간 전
            category = NewsCategory.CRYPTO
        ),
        NewsItem(
            id = "5",
            title = "중국 경제, 1분기 GDP 5.3% 성장",
            summary = "중국 경제가 1분기 GDP 5.3% 성장률을 기록했습니다. 이는 시장 예상치인 4.8%를 상회하는 결과로, 코로나19 이후 경제 회복이 견고하게 진행되고 있음을 보여줍니다. 특히 소비와 서비스 부문이 성장을 견인했습니다.",
            source = "글로벌이코노믹",
            publishedAt = Date(System.currentTimeMillis() - 43200000), // 12시간 전
            category = NewsCategory.INTERNATIONAL
        )
    )
}