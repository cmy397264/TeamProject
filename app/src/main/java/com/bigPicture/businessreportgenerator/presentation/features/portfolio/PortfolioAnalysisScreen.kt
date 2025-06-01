// PortfolioAnalysisScreen.kt - 완전한 파일

package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType
import com.bigPicture.businessreportgenerator.data.domain.PortfolioAnalysisData
import com.bigPicture.businessreportgenerator.data.domain.StockHistoryItem
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale


@Composable
fun PortfolioAnalysisScreen(
    assets: List<Asset>,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val portfolioViewModel: PortfolioViewModel = koinViewModel()
    var analysisData by remember { mutableStateOf<List<PortfolioAnalysisData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var totalPortfolioHistory by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 주식 타입 자산만 필터링
    val stockAssets = remember(assets) {
        assets.filter { it.type == AssetType.STOCK && !it.ticker.isNullOrEmpty() }
    }

    Log.d("PortfolioAnalysis", "분석할 주식 수: ${stockAssets.size}")

    // 데이터 로딩
    LaunchedEffect(stockAssets) {
        if (stockAssets.isEmpty()) {
            isLoading = false
            errorMessage = "분석할 주식이 없습니다. 주식을 추가해주세요."
            return@LaunchedEffect
        }

        isLoading = true
        errorMessage = null

        try {
            val analysisResults = mutableListOf<PortfolioAnalysisData>()
            val portfolioValueByDate = mutableMapOf<String, Double>()

            for (asset in stockAssets) {
                try {
                    Log.d("PortfolioAnalysis", "주식 히스토리 조회 중: ${asset.ticker}")
                    val history = portfolioViewModel.getStockHistory(asset.ticker!!)

                    if (history.isNotEmpty()) {
                        val currentPrice = history.lastOrNull()?.stockPrice ?: 0.0
                        val purchasePricePerShare = asset.purchasePricePerShare ?: 0.0
                        val shares = asset.shares ?: 0.0

                        val currentValue = currentPrice * shares
                        val purchaseValue = purchasePricePerShare * shares
                        val returnAmount = currentValue - purchaseValue
                        val returnPercentage = if (purchaseValue > 0) (returnAmount / purchaseValue) * 100 else 0.0

                        analysisResults.add(
                            PortfolioAnalysisData(
                                asset = asset,
                                priceHistory = history,
                                currentValue = currentValue,
                                purchaseValue = purchaseValue,
                                returnPercentage = returnPercentage,
                                returnAmount = returnAmount
                            )
                        )

                        // 포트폴리오 전체 가치 계산
                        history.forEach { historyItem ->
                            val dateValue = historyItem.stockPrice * shares
                            portfolioValueByDate[historyItem.stockDate] =
                                portfolioValueByDate.getOrDefault(historyItem.stockDate, 0.0) + dateValue
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PortfolioAnalysis", "${asset.ticker} 분석 실패: ${e.message}")
                }
            }

            analysisData = analysisResults
            totalPortfolioHistory = portfolioValueByDate.toList().sortedBy { it.first }

        } catch (e: Exception) {
            Log.e("PortfolioAnalysis", "전체 분석 실패: ${e.message}")
            errorMessage = "분석 중 오류가 발생했습니다: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFF))
    ) {
        when {
            isLoading -> {
                LoadingScreen(stockAssets.size)
            }

            errorMessage != null -> {
                ErrorScreen(errorMessage!!, onBackPressed)
            }

            analysisData.isEmpty() -> {
                EmptyDataScreen(onBackPressed)
            }

            else -> {
                AnalysisContent(
                    analysisData = analysisData,
                    totalPortfolioHistory = totalPortfolioHistory
                )
            }
        }

        // 플로팅 백 버튼
        FloatingActionButton(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
                .size(48.dp),
            containerColor = Color.White.copy(alpha = 0.9f),
            contentColor = Color(0xFF1A1A2E),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LoadingScreen(stockCount: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF667eea),
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "포트폴리오 분석 중...",
                fontSize = 16.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${stockCount}개 주식 데이터 수집",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ErrorScreen(errorMessage: String, onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "에러",
                tint = Color(0xFFFF3B30),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                fontSize = 16.sp,
                color = Color(0xFF1A1A2E),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                )
            ) {
                Text("돌아가기", color = Color.White)
            }
        }
    }
}

@Composable
private fun EmptyDataScreen(onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Face,
                contentDescription = "데이터 없음",
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "분석할 주식 데이터가 없습니다",
                fontSize = 18.sp,
                color = Color(0xFF1A1A2E),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "주식을 추가하고 다시 시도해주세요",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                )
            ) {
                Text("돌아가기", color = Color.White)
            }
        }
    }
}

@Composable
private fun AnalysisContent(
    analysisData: List<PortfolioAnalysisData>,
    totalPortfolioHistory: List<Pair<String, Double>>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 헤더
        item {
            AnalysisHeaderSection(
                totalAssets = analysisData.size,
                totalValue = analysisData.sumOf { it.currentValue },
                totalReturn = analysisData.sumOf { it.returnAmount },
                totalReturnPercentage = run {
                    val totalPurchase = analysisData.sumOf { it.purchaseValue }
                    val totalCurrent = analysisData.sumOf { it.currentValue }
                    if (totalPurchase > 0) ((totalCurrent - totalPurchase) / totalPurchase) * 100 else 0.0
                }
            )
        }

        // 전체 포트폴리오 차트
        if (totalPortfolioHistory.isNotEmpty()) {
            item {
                PortfolioTotalValueChart(
                    portfolioHistory = totalPortfolioHistory
                )
            }
        }

        // 개별 자산 분석
        items(analysisData) { data ->
            IndividualAssetAnalysisCard(
                analysisData = data
            )
        }
    }
}

@Composable
fun AnalysisHeaderSection(
    totalAssets: Int,
    totalValue: Double,
    totalReturn: Double,
    totalReturnPercentage: Double
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)
    val isProfit = totalReturn >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (isProfit) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFf093fb),
                                Color(0xFFf5576c)
                            )
                        )
                    },
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "포트폴리오 분석",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.6).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "3개월 주가 변화 분석",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "총 자산가치",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatter.format(totalValue),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "총 수익률",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${if (isProfit) "+" else ""}${String.format("%.2f", totalReturnPercentage)}%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "분석 대상: ${totalAssets}개 종목",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "${if (isProfit) "+" else ""}${formatter.format(totalReturn)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioTotalValueChart(
    portfolioHistory: List<Pair<String, Double>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "포트폴리오 가치 변화",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Text(
                text = "최근 3개월 총 자산 가치 추이",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Color(0xFFF8FAFF),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                PortfolioLineChart(
                    data = portfolioHistory.associate { it.first to it.second.toFloat() }
                )
            }
        }
    }
}

@Composable
fun IndividualAssetAnalysisCard(
    analysisData: PortfolioAnalysisData
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)
    val isProfit = analysisData.returnAmount >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = analysisData.asset.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    analysisData.asset.ticker?.let { ticker ->
                        Text(
                            text = ticker,
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isProfit) "+" else ""}${String.format("%.2f", analysisData.returnPercentage)}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isProfit) Color(0xFF34C759) else Color(0xFFFF3B30)
                    )
                    Text(
                        text = "${if (isProfit) "+" else ""}${formatter.format(analysisData.returnAmount)}",
                        fontSize = 14.sp,
                        color = if (isProfit) Color(0xFF34C759) else Color(0xFFFF3B30),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 가치 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "현재 가치",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )
                    Text(
                        text = formatter.format(analysisData.currentValue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "매입 가치",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )
                    Text(
                        text = formatter.format(analysisData.purchaseValue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 개별 주가 차트
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Color(0xFFF8FAFF),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                AssetPriceChart(
                    priceHistory = analysisData.priceHistory
                )
            }
        }
    }
}

@Composable
fun PortfolioLineChart(
    data: Map<String, Float>
) {
    if (data.isEmpty() || data.size < 2) return

    val values = data.values.toList()
    val max = values.maxOrNull() ?: 0f
    val min = values.minOrNull() ?: 0f
    val range = (max - min).takeIf { it != 0f } ?: 1f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width - 40.dp.toPx()
        val height = size.height - 40.dp.toPx()
        val startX = 20.dp.toPx()
        val startY = 20.dp.toPx()

        val stepX = width / (values.size - 1)

        // 그리드 라인
        for (i in 0..4) {
            val y = startY + (height / 4) * i
            drawLine(
                color = Color(0xFFE5E7EB),
                start = Offset(startX, y),
                end = Offset(startX + width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // 데이터 포인트 계산
        val points = values.mapIndexed { index, value ->
            val x = startX + index * stepX
            val y = startY + height - ((value - min) / range) * height
            Offset(x, y)
        }

        // 그라데이션 배경
        val gradientPath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, startY + height)
                points.forEach { point ->
                    lineTo(point.x, point.y)
                }
                lineTo(points.last().x, startY + height)
                close()
            }
        }

        drawPath(
            path = gradientPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF667eea).copy(alpha = 0.3f),
                    Color(0xFF667eea).copy(alpha = 0.05f)
                )
            )
        )

        // 메인 라인
        val linePath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }
        }

        drawPath(
            path = linePath,
            color = Color(0xFF667eea),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 데이터 포인트
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = point,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF667eea),
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun AssetPriceChart(
    priceHistory: List<StockHistoryItem>
) {
    val data = priceHistory.associate { it.stockDate to it.stockPrice.toFloat() }
    PortfolioLineChart(data = data)
}