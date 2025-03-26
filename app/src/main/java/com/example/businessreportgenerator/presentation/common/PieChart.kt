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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    assets: List<com.example.businessreportgenerator.domain.model.Asset> = emptyList(),
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val total = data.sumOf { it.value.toDouble() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .padding(8.dp)
        ) {
            var startAngle = -90f
            val radius = size.minDimension / 2
            val centerX = size.width / 2
            val centerY = size.height / 2

            // 각 자산별 파이 조각 그리기
            data.forEachIndexed { index, pieData ->
                val sweepAngle = 360f * (pieData.value / total.toFloat())

                // 파이 조각 그리기
                drawArc(
                    color = pieData.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // 외곽선 (선택 사항)
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    style = Stroke(width = 2f),
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // 중심각의 중간 위치 계산 (라벨 배치용)
                val medianAngle = (startAngle + sweepAngle / 2) * (PI / 180f)

                // 조각이 충분히 큰 경우에만 텍스트 표시 (최소 20도)
                if (sweepAngle >= 20) {
                    // 반지름의 60% 위치에 라벨 배치
                    val labelRadius = radius * 0.6f
                    val labelX = centerX + (labelRadius * cos(medianAngle).toFloat())
                    val labelY = centerY + (labelRadius * sin(medianAngle).toFloat())

                    // 동적 텍스트 크기 조정 구현
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
                        }

                        // 기본 텍스트 크기 (sp 단위를 px로 변환)
                        var textSize = (12 + (sweepAngle / 36)).coerceAtMost(16f)
                        paint.textSize = textSize.sp.toPx()

                        // 텍스트의 실제 너비 측정
                        val textWidth = paint.measureText(pieData.label)
                        // 파이 조각의 호 길이 계산 (라디안 단위 변환 후, 호 길이 = radius * 각(라디안))
                        val arcLength = radius * (sweepAngle * (PI / 180)).toFloat()
                        // 텍스트가 들어갈 수 있는 최대 너비 (호 길이의 80% 정도 사용)
                        val maxAllowedWidth = arcLength * 0.8f

                        if (textWidth > maxAllowedWidth) {
                            // 텍스트 크기가 너무 크면 비율에 맞춰 조정
                            val scalingFactor = maxAllowedWidth / textWidth
                            textSize *= scalingFactor
                            paint.textSize = textSize.sp.toPx()
                        }

                        // 최종적으로 텍스트 그리기
                        drawText(
                            pieData.label,
                            labelX,
                            labelY,
                            paint
                        )
                    }
                }

                startAngle += sweepAngle
            }
        }

        // 외부 범례 추가 (차트 아래에 배치)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 280.dp, bottom = 8.dp)
        ) {
            // 범례 항목들
            data.chunked(2).forEach { rowData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowData.forEach { pieData ->
                        // 각 범례 항목
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // 색상 표시
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(pieData.color, CircleShape)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // 라벨
                            Text(
                                text = pieData.label,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // 비율
                            val proportion = (pieData.value / total.toFloat()) * 100
                            Text(
                                text = "(${proportion.toInt()}%)",
                                fontSize = 12.sp,
                                color = pieData.color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
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
