package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class InvestmentTip(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val gradient: Brush,
    val iconColor: Color
)

val InvestmentTips = listOf(
    InvestmentTip(
        icon = Icons.Rounded.Done,
        title = "분산 투자의 힘",
        description = "다양한 섹터와 자산에 분산 투자하여 리스크를 최소화하세요",
        gradient = Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2))),
        iconColor = Color.White
    ),
    InvestmentTip(
        icon = Icons.Rounded.DateRange,
        title = "장기 투자 마인드",
        description = "시장의 단기 변동성에 휘둘리지 말고 장기적 관점을 유지하세요",
        gradient = Brush.linearGradient(listOf(Color(0xFFf093fb), Color(0xFFf5576c))),
        iconColor = Color.White
    ),
    InvestmentTip(
        icon = Icons.Rounded.Edit,
        title = "정기적 리밸런싱",
        description = "포트폴리오 비중을 정기적으로 조정하여 목표 배분을 유지하세요",
        gradient = Brush.linearGradient(listOf(Color(0xFF4facfe), Color(0xFF00f2fe))),
        iconColor = Color.White
    ),
    InvestmentTip(
        icon = Icons.Outlined.PlayArrow,
        title = "꾸준한 적립식 투자",
        description = "시간의 힘을 활용하여 꾸준히 적립식으로 투자하세요",
        gradient = Brush.linearGradient(listOf(Color(0xFF43e97b), Color(0xFF38f9d7))),
        iconColor = Color.White
    ),
    InvestmentTip(
        icon = Icons.Rounded.ThumbUp,
        title = "데이터 기반 결정",
        description = "감정이 아닌 데이터와 분석을 바탕으로 투자 결정을 내리세요",
        gradient = Brush.linearGradient(listOf(Color(0xFFfa709a), Color(0xFFfee140))),
        iconColor = Color.White
    )
)