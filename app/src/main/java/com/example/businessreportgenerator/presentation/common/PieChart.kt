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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

data class PieChartData(
    val value: Float,
    val color: Color,
    val label: String
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    showLegend: Boolean = true
) {
    // 총액 계산
    val total = data.sumOf { it.value.toDouble() }.toFloat()

    // 포맷터 설정
    val numberFormat = NumberFormat.getPercentInstance(Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 파이 차트
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(240.dp)
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

                    startAngle += sweepAngle
                }
            }
        }

        // 범례
        if (data.isNotEmpty() && showLegend) {
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
                    data.forEachIndexed { index, item ->
                        val percentage = (item.value / total * 100).toInt()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 색상 원형
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // 자산 이름
                                Text(
                                    text = item.label,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row {
                                // 비율
                                Text(
                                    text = "$percentage%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // 금액 (옵션)
                                if (total > 10000) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = currencyFormat.format(item.value.toDouble()),
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // 마지막 항목이 아니면 구분선 추가
                        if (index < data.size - 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
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
    }
}