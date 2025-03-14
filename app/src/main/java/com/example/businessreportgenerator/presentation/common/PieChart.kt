package com.example.businessreportgenerator.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.businessreportgenerator.domain.model.Asset
import com.example.businessreportgenerator.domain.model.AssetType
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class PieChartData(
    val value: Float,
    val color: Color,
    val label: String
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    assets: List<Asset>,
    modifier: Modifier = Modifier
) {
    // 총액 계산
    val total = data.sumOf { it.value.toDouble() }.toFloat()

    // 텍스트 측정기
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 파이 차트
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        clip = false
                    )
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val radius = min(canvasWidth, canvasHeight) / 2
                val center = Offset(canvasWidth / 2, canvasHeight / 2)

                var startAngle = -90f  // 12시 방향부터 시작

                // 기본 원 그리기 (배경)
                drawCircle(
                    color = Color(0xFFF5F5F5),
                    radius = radius,
                    center = center
                )

                // 각 부분 그리기
                data.forEach { pieData ->
                    val sweepAngle = 360f * (pieData.value / total)

                    // 섹션 그리기 (채워진 파이)
                    drawArc(
                        color = pieData.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // 테두리 그리기 (옵션)
                    if (data.size > 1) {
                        drawArc(
                            color = Color.White,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            style = Stroke(width = 2f),
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    // 각 섹션에 라벨 그리기
                    if (sweepAngle > 15) { // 너무 작은 조각에는 텍스트 표시 안함
                        val middleAngle = startAngle + (sweepAngle / 2)
                        val middleAngleInRadians = middleAngle * (PI / 180f)

                        // 텍스트 위치 계산 (중심에서 바깥쪽으로 60~70% 지점)
                        val labelRadius = radius * 0.65f
                        val labelX = center.x + (labelRadius * cos(middleAngleInRadians)).toFloat()
                        val labelY = center.y + (labelRadius * sin(middleAngleInRadians)).toFloat()

                        // 텍스트 그리기
                        drawText(
                            textMeasurer = textMeasurer,
                            text = pieData.label,
                            topLeft = Offset(
                                labelX - 60, // 정확한 위치를 위해 텍스트 크기의 절반 빼기
                                labelY - 10
                            ),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    startAngle += sweepAngle
                }
            }
        }

        // 종목 상세 정보 카드
        if (data.isNotEmpty()) {
            StockDetailsCard(data, assets)
        }
    }
}

@Composable
fun StockDetailsCard(data: List<PieChartData>, assets: List<Asset>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "종목",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(2f)
                )

                Text(
                    text = "진입가",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )

                Text(
                    text = "현재가",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )

                Text(
                    text = "수익률",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            // 구분선
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEEEEEE))
            )

            // 종목 목록
            assets.forEachIndexed { index, asset ->
                val entryPrice = when (asset.type) {
                    AssetType.STOCK, AssetType.ETF, AssetType.BOND, AssetType.CRYPTO -> {
                        asset.details["averagePrice"]?.toDoubleOrNull() ?: 0.0
                    }
                    else -> {
                        0.0  // 부동산이나 다른 유형은 진입가 개념이 다를 수 있음
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 종목명
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(2f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(data[index].color)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = asset.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // 진입가 (평단가)
                    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)
                    Text(
                        text = if (entryPrice > 0) formatter.format(entryPrice) else "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )

                    // 현재가 (API 연동 전 임시)
                    Text(
                        text = "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )

                    // 수익률 (API 연동 전 임시)
                    Text(
                        text = "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                // 마지막 항목이 아니면 구분선 추가
                if (index < assets.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFEEEEEE))
                    )
                }
            }
        }
    }
}