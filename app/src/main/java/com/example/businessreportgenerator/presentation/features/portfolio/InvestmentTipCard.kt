package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

// 1) 팁 데이터를 담는 모델
data class InvestmentTip(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val backgroundColor: Color
)

// 2) 예시 팁 리스트
val investmentTips = listOf(
    InvestmentTip(
        icon            = Icons.Rounded.Search,
        title           = "분산 투자",
        description     = "다양한 자산 클래스에 투자하여 리스크를 분산시키세요.",
        backgroundColor = Color(0xFFE8F5E9)
    ),
    InvestmentTip(
        icon            = Icons.Rounded.DateRange,
        title           = "장기 투자",
        description     = "단기 변동성에 흔들리지 말고 장기적 관점으로 투자하세요.",
        backgroundColor = Color(0xFFE3F2FD)
    ),
    InvestmentTip(
        icon            = Icons.Rounded.Refresh,
        title           = "정기적 리밸런싱",
        description     = "포트폴리오를 정기적으로 조정하여 목표 배분을 유지하세요.",
        backgroundColor = Color(0xFFFFF3E0)
    )
)

// 3) 실제 카드 Composable (최상위에 선언!)
@Composable
fun InvestmentTipCard(tip: InvestmentTip) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(160.dp),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors    = CardDefaults.cardColors(containerColor = tip.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector     = tip.icon,
                contentDescription = tip.title,
                modifier        = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = tip.title,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = tip.description,
                fontSize   = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
