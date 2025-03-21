package com.example.businessreportgenerator.presentation.features.analyst

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * AI 애널리스트 메인 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalystScreen(modifier: Modifier = Modifier) {
    var selectedReport by remember { mutableStateOf<AnalystReport?>(null) }
    val reports = DummyReportData.reports

    // 필터 상태
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSentiment by remember { mutableStateOf<ReportSentiment?>(null) }

    // 필터링된 보고서
    val filteredReports = when {
        selectedCategory != null && selectedSentiment != null -> {
            reports.filter { it.category == selectedCategory && it.sentiment == selectedSentiment }
        }
        selectedCategory != null -> {
            reports.filter { it.category == selectedCategory }
        }
        selectedSentiment != null -> {
            reports.filter { it.sentiment == selectedSentiment }
        }
        else -> reports
    }

    // 카테고리 목록
    val categories = reports.map { it.category }.distinct()

    // 감정 목록
    val sentiments = ReportSentiment.values().toList()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        if (selectedReport == null) {
            // 보고서 리스트 화면
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "AI 애널리스트",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = Color.Black
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // 필터 영역
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // 필터 제목
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(

                                    imageVector = Icons.Default.List,
                                    contentDescription = null,
                                    tint = Color(0xFF007AFF),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "필터",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 카테고리 필터
                            Text(
                                text = "카테고리",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 전체 옵션
                                FilterChip(
                                    selected = selectedCategory == null,
                                    onClick = { selectedCategory = null },
                                    label = { Text("전체") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF007AFF),
                                        selectedLabelColor = Color.White
                                    )
                                )

                                // 각 카테고리 옵션
                                categories.forEach { category ->
                                    FilterChip(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category },
                                        label = { Text(category) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF007AFF),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 감정 필터
                            Text(
                                text = "분위기",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 전체 옵션
                                FilterChip(
                                    selected = selectedSentiment == null,
                                    onClick = { selectedSentiment = null },
                                    label = { Text("전체") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF007AFF),
                                        selectedLabelColor = Color.White
                                    )
                                )

                                // 각 감정 옵션
                                sentiments.forEach { sentiment ->
                                    FilterChip(
                                        selected = selectedSentiment == sentiment,
                                        onClick = { selectedSentiment = sentiment },
                                        label = { Text(sentiment.getDisplayName()) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = sentiment.getColor(),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 보고서 리스트
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredReports) { report ->
                            ReportCard(
                                report = report,
                                onClick = { selectedReport = it }
                            )
                        }

                        // 하단 여백
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        } else {
            // 보고서 상세 화면
            ReportDetailScreen(
                report = selectedReport!!,
                onBackPressed = { selectedReport = null }
            )
        }
    }
}

/**
 * 보고서 카드 아이템
 */
@Composable
fun ReportCard(
    report: AnalystReport,
    onClick: (AnalystReport) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(report) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 상단 정보 (날짜, 분류, 감정)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 날짜와 카테고리
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 날짜 포맷
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Text(
                        text = dateFormat.format(report.date),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = report.category,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // 감정 태그
                SentimentTag(sentiment = report.sentiment)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 그래프 미리보기 (첫 번째 그래프만)
            if (report.graphData.isNotEmpty()) {
                val firstGraph = report.graphData.first()
                GraphPreview(
                    graphData = firstGraph,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F9FA))
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 보고서 제목
            Text(
                text = report.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 보고서 요약
            Text(
                text = report.summary,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 더보기 버튼
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "자세히 보기",
                    fontSize = 14.sp,
                    color = Color(0xFF007AFF)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 감정 태그 표시
 */
@Composable
fun SentimentTag(sentiment: ReportSentiment) {
    Box(
        modifier = Modifier
            .background(
                color = sentiment.getColor().copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = sentiment.getDisplayName(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = sentiment.getColor()
        )
    }
}

/**
 * 그래프 미리보기
 */
@Composable
fun GraphPreview(
    graphData: GraphData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (graphData.type) {
            GraphType.LINE_CHART -> LineChartPreview(data = graphData.data)
            GraphType.BAR_CHART -> BarChartPreview(data = graphData.data)
            GraphType.PIE_CHART -> PieChartPreview(data = graphData.data)
        }

        // 그래프 제목
        Text(
            text = graphData.title,
            fontSize = 12.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

/**
 * 선 그래프 미리보기
 */
@Composable
fun LineChartPreview(data: Map<String, Float>) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val max = values.maxOrNull() ?: 0f
    val min = values.minOrNull() ?: 0f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)

        // Y축 범위 계산 (최소값이 0에 가깝지 않으면 여백 추가)
        val yRange = if (min > max * 0.7f) max - min else max

        // 경로 생성
        val path = Path()

        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - min) / yRange) * height

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // 곡선 그리기
        drawPath(
            path = path,
            color = Color(0xFF007AFF),
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // 데이터 포인트 그리기
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - min) / yRange) * height

            drawCircle(
                color = Color.White,
                radius = 6f,
                center = Offset(x, y)
            )

            drawCircle(
                color = Color(0xFF007AFF),
                radius = 4f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * 막대 그래프 미리보기
 */
@Composable
fun BarChartPreview(data: Map<String, Float>) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val max = values.maxOrNull() ?: 0f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = (width / data.size) * 0.7f
        val spacing = (width / data.size) * 0.3f

        // 막대 그래프 그리기
        values.forEachIndexed { index, value ->
            val x = index * (barWidth + spacing) + spacing / 2
            val barHeight = (value / max) * height
            val y = height - barHeight

            drawRect(
                color = when {
                    value > max * 0.8f -> Color(0xFF4CD964) // 높은 값 (초록)
                    value > max * 0.5f -> Color(0xFF007AFF) // 중간 값 (파랑)
                    value > max * 0.3f -> Color(0xFFFF9500) // 낮은 값 (주황)
                    else -> Color(0xFFFF3B30) // 매우 낮은 값 (빨강)
                },
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

/**
 * 파이 차트 미리보기
 */
@Composable
fun PieChartPreview(data: Map<String, Float>) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val total = values.sum()

    // 파이 차트 색상
    val colors = listOf(
        Color(0xFF007AFF), // 파랑
        Color(0xFF4CD964), // 초록
        Color(0xFFFF9500), // 주황
        Color(0xFF5856D6), // 보라
        Color(0xFFFF2D55), // 빨강
        Color(0xFFFFCC00), // 노랑
        Color(0xFF34C759), // 라임 그린
        Color(0xFFAF52DE)  // 분홍
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val radius = minOf(width, height) / 2
        val center = Offset(width / 2, height / 2)

        var startAngle = 0f

        // 파이 차트 세그먼트 그리기
        values.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }
    }
}

/**
 * 보고서 상세 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    report: AnalystReport,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "보고서 상세",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 보고서 헤더
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // 날짜, 카테고리, 감정
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 날짜
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                Text(
                                    text = dateFormat.format(report.date),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )

                                // 구분점
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Color.LightGray, CircleShape)
                                )

                                // 카테고리
                                Text(
                                    text = report.category,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            // 감정 태그
                            SentimentTag(sentiment = report.sentiment)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 보고서 제목
                        Text(
                            text = report.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 보고서 요약
                        Text(
                            text = report.summary,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // 그래프 섹션
            item {
                Text(
                    text = "주요 데이터",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // 각 그래프 표시
            items(report.graphData) { graphData ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // 그래프 제목 및 아이콘
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (graphData.type) {
                                    GraphType.LINE_CHART -> Icons.Default.Info
                                    GraphType.BAR_CHART -> Icons.Default.Info
                                    GraphType.PIE_CHART -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = graphData.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 그래프 설명
                        Text(
                            text = graphData.description,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 그래프 표시
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8F9FA))
                                .padding(16.dp)
                        ) {
                            when (graphData.type) {
                                GraphType.LINE_CHART -> LineChartPreview(data = graphData.data)
                                GraphType.BAR_CHART -> BarChartPreview(data = graphData.data)
                                GraphType.PIE_CHART -> PieChartPreview(data = graphData.data)
                            }
                        }

                        // 데이터 출처
                        Text(
                            text = "출처: AI 애널리스트 데이터 분석",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            // 상세 내용 섹션
            item {
                Text(
                    text = "상세 내용",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    // 마크다운 포맷으로 된 내용을 간단하게 파싱하여 표시
                    val lines = report.detailedContent.lines()

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        lines.forEach { line ->
                            when {
                                line.startsWith("# ") -> {
                                    // 제목 1
                                    Text(
                                        text = line.substring(2),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                line.startsWith("## ") -> {
                                    // 제목 2
                                    Text(
                                        text = line.substring(3),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                                line.startsWith("- ") -> {
                                    // 목록
                                    Row {
                                        Text(
                                            text = "•",
                                            fontSize = 16.sp,
                                            modifier = Modifier.width(16.dp)
                                        )
                                        Text(
                                            text = line.substring(2),
                                            fontSize = 16.sp,
                                            lineHeight = 24.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                line.isEmpty() -> {
                                    // 빈 줄
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                else -> {
                                    // 일반 텍스트
                                    Text(
                                        text = line,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                // 하단 여백
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}