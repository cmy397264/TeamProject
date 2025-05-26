package com.bigPicture.businessreportgenerator.presentation.common

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType
import java.text.NumberFormat
import java.util.Locale

data class PieChartData(
    val value: Float,
    val color: Color,
    val label: String
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    assets: List<Asset> = emptyList(),
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle.MODERN_DONUT
) {
    if (data.isEmpty()) return

    when (style) {
        ChartStyle.MODERN_DONUT -> ModernDonutChart(data, assets, modifier)
        ChartStyle.GRADIENT_PIE -> GradientPieChart(data, assets, modifier)
        ChartStyle.MINIMAL -> MinimalPieChart(data, assets, modifier)
    }
}

enum class ChartStyle {
    MODERN_DONUT, GRADIENT_PIE, MINIMAL
}

@Composable
private fun ModernDonutChart(
    data: List<PieChartData>,
    assets: List<Asset>,
    modifier: Modifier
) {
    val total = data.sumOf { it.value.toDouble() }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            ) {
                drawModernDonut(data, total)
            }

            // 중앙 정보 표시
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "총 자산",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
                Text(
                    text = "${formatter.format(total.toLong())}원",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 현대적인 범례
        ModernLegend(data, total)
    }
}

@Composable
private fun GradientPieChart(
    data: List<PieChartData>,
    assets: List<Asset>,
    modifier: Modifier
) {
    val total = data.sumOf { it.value.toDouble() }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .padding(16.dp)
        ) {
            drawGradientPie(data, total)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ModernLegend(data, total)
    }
}

@Composable
private fun MinimalPieChart(
    data: List<PieChartData>,
    assets: List<Asset>,
    modifier: Modifier
) {
    val total = data.sumOf { it.value.toDouble() }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(160.dp)
            ) {
                drawMinimalPie(data, total)
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            data.take(4).forEach { pieData ->
                MinimalLegendItem(pieData, total)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun DrawScope.drawModernDonut(data: List<PieChartData>, total: Double) {
    var startAngle = -90f
    val strokeWidth = 28.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    data.forEach { pieData ->
        val sweepAngle = 360f * (pieData.value / total.toFloat())

        // 도넛 그리기 (그라데이션 효과)
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(
                    pieData.color,
                    pieData.color.copy(alpha = 0.8f)
                ),
                center = center,
                radius = radius
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            ),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        startAngle += sweepAngle
    }
}

private fun DrawScope.drawGradientPie(data: List<PieChartData>, total: Double) {
    var startAngle = -90f
    val radius = size.minDimension / 2
    val center = Offset(size.width / 2, size.height / 2)

    data.forEach { pieData ->
        val sweepAngle = 360f * (pieData.value / total.toFloat())

        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    pieData.color.copy(alpha = 0.7f),
                    pieData.color,
                    pieData.color.copy(alpha = 0.9f)
                )
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // 구분선
        drawArc(
            color = Color.White,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            style = Stroke(width = 3.dp.toPx()),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        startAngle += sweepAngle
    }
}

private fun DrawScope.drawMinimalPie(data: List<PieChartData>, total: Double) {
    var startAngle = -90f
    val radius = size.minDimension / 2
    val center = Offset(size.width / 2, size.height / 2)

    data.forEach { pieData ->
        val sweepAngle = 360f * (pieData.value / total.toFloat())

        drawArc(
            color = pieData.color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        startAngle += sweepAngle
    }
}

@Composable
private fun ModernLegend(data: List<PieChartData>, total: Double) {
    data.chunked(2).forEach { rowData ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            rowData.forEach { pieData ->
                ModernLegendItem(
                    pieData = pieData,
                    total = total,
                    modifier = Modifier.weight(1f)
                )
            }

            // 홀수 개일 때 공간 채우기
            if (rowData.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ModernLegendItem(
    pieData: PieChartData,
    total: Double,
    modifier: Modifier = Modifier
) {
    val proportion = (pieData.value / total.toFloat()) * 100

    Card(
        modifier = modifier
            .padding(4.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                pieData.color,
                                pieData.color.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pieData.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${proportion.toInt()}%",
                    fontSize = 12.sp,
                    color = pieData.color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MinimalLegendItem(pieData: PieChartData, total: Double) {
    val proportion = (pieData.value / total.toFloat()) * 100

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(pieData.color)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = pieData.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "${proportion.toInt()}%",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun ModernStockDetailsCard(data: List<PieChartData>, assets: List<Asset>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "종목 상세",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(assets) { asset ->
                    val index = assets.indexOf(asset)
                    val entryPrice = when (asset.type) {
                        AssetType.STOCK, AssetType.ETF, AssetType.CRYPTO -> {
                            asset.details["averagePrice"]?.toDoubleOrNull() ?: 0.0
                        }
                        else -> 0.0
                    }

                    ModernStockItem(
                        asset = asset,
                        color = data[index].color,
                        entryPrice = entryPrice
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernStockItem(
    asset: Asset,
    color: Color,
    entryPrice: Double
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFBFC)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = when(asset.type) {
                        AssetType.STOCK -> "주식"
                        AssetType.ETF -> "ETF"
                        AssetType.CRYPTO -> "암호화폐"
                        else -> "기타"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (entryPrice > 0) formatter.format(entryPrice) else "-",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "평단가",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}