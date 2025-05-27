package com.bigPicture.businessreportgenerator.presentation.features.analyst

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigPicture.businessreportgenerator.data.domain.AnalystReport
import com.bigPicture.businessreportgenerator.data.domain.GraphData
import com.bigPicture.businessreportgenerator.data.domain.ReportSentiment
import com.bigPicture.businessreportgenerator.data.domain.getColor
import com.bigPicture.businessreportgenerator.data.domain.getDisplayName
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

// 컬러 팔레트 정의
object FinancialColors {
    val Primary = Color(0xFF0066FF)
    val Secondary = Color(0xFF00D4AA)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val SurfaceVariant = Color(0xFFF1F5F9)
    val OnSurface = Color(0xFF1E293B)
    val OnSurfaceVariant = Color(0xFF64748B)
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Neutral = Color(0xFF6B7280)

    // 그래프 색상
    val GraphPrimary = Color(0xFF3B82F6)
    val GraphSecondary = Color(0xFF8B5CF6)
    val GraphTertiary = Color(0xFF06B6D4)
    val GraphAccent = Color(0xFFF59E0B)
}

/**
 * 메인 애널리스트 화면 - 현대적 디자인
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnalystScreen(modifier: Modifier = Modifier) {
    var isFilterOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: AnalystViewmodel = viewModel(
        factory = remember { AnalystViewModelFactory(context.applicationContext) }
    )

    val filter by viewModel.filterState.collectAsState()
    val selectedCategory = filter.category
    val selectedSentiment = filter.sentiment
    val selectedReport by viewModel.selectedReport.collectAsState()
    val reports by viewModel.filteredReports.collectAsState()
    val categories by viewModel.categoriesFlow.collectAsState()
    val sentiments = viewModel.sentiments

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FinancialColors.Background)
    ) {
        if (selectedReport == null) {
            // 메인 리스트 화면
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // 헤더
                stickyHeader {
                    ModernTopBar()
                }

                // 필터 섹션
                stickyHeader {
                    ModernFilterSection(
                        isOpen = isFilterOpen,
                        onToggle = { isFilterOpen = !isFilterOpen },
                        categories = categories,
                        sentiments = sentiments,
                        selectedCategory = selectedCategory,
                        selectedSentiment = selectedSentiment,
                        onCategorySelected = { viewModel.setSelectedCategory(it) },
                        onSentimentSelected = { viewModel.setSelectedSentiment(it) }
                    )
                }

                // 보고서 리스트
                items(reports, key = { it.id }) { report ->
                    ModernReportCard(
                        report = report,
                        onClick = { viewModel.setSelectedReport(report) }
                    )
                }
            }
        } else {
            // 상세 화면
            ModernReportDetailScreen(
                report = selectedReport!!,
                onBackPressed = { viewModel.setSelectedReport(null) }
            )
        }
    }
}

@Composable
fun ModernTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FinancialColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI 투자 리포트",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinancialColors.OnSurface
                    )
                    Text(
                        text = "실시간 시장 분석 및 전망",
                        fontSize = 14.sp,
                        color = FinancialColors.OnSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            FinancialColors.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = FinancialColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernFilterSection(
    isOpen: Boolean,
    onToggle: () -> Unit,
    categories: List<String>,
    sentiments: List<ReportSentiment>,
    selectedCategory: String?,
    selectedSentiment: ReportSentiment?,
    onCategorySelected: (String?) -> Unit,
    onSentimentSelected: (ReportSentiment?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 필터 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccountBox,
                        contentDescription = null,
                        tint = FinancialColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "필터",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FinancialColors.OnSurface
                    )
                }

                Icon(
                    imageVector = if (isOpen) Icons.Default.AccountBox else Icons.Default.Notifications,
                    contentDescription = null,
                    tint = FinancialColors.OnSurfaceVariant
                )
            }

            if (isOpen) {
                Spacer(modifier = Modifier.height(16.dp))

                // 카테고리 필터
                ModernFilterChipGroup(
                    title = "카테고리",
                    items = categories,
                    selectedItem = selectedCategory,
                    onItemSelected = onCategorySelected,
                    itemToString = { it },
                    getItemColor = { FinancialColors.Primary }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 감정 필터
                ModernFilterChipGroup(
                    title = "시장 전망",
                    items = sentiments,
                    selectedItem = selectedSentiment,
                    onItemSelected = onSentimentSelected,
                    itemToString = { it.getDisplayName() },
                    getItemColor = { it.getColor() }
                )
            }
        }
    }
}

@Composable
fun <T> ModernFilterChipGroup(
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    itemToString: (T) -> String,
    getItemColor: (T) -> Color
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = FinancialColors.OnSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 전체 선택 칩
            FilterChip(
                selected = selectedItem == null,
                onClick = { onItemSelected(null) },
                label = {
                    Text(
                        "전체",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FinancialColors.Primary,
                    selectedLabelColor = Color.White,
                    containerColor = FinancialColors.SurfaceVariant,
                    labelColor = FinancialColors.OnSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedItem == null,
                    borderColor = if (selectedItem == null) FinancialColors.Primary else Color.Transparent
                )
            )

            // 개별 아이템 칩들
            items.forEach { item ->
                FilterChip(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    label = {
                        Text(
                            itemToString(item),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getItemColor(item),
                        selectedLabelColor = Color.White,
                        containerColor = FinancialColors.SurfaceVariant,
                        labelColor = FinancialColors.OnSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedItem == item,
                        borderColor = if (selectedItem == item) getItemColor(item) else Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun ModernReportCard(
    report: AnalystReport,
    onClick: (AnalystReport) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick(report) },
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 헤더 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        val dateFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
                        Text(
                            text = dateFormat.format(report.date),
                            fontSize = 12.sp,
                            color = FinancialColors.OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    FinancialColors.Primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = report.category,
                                fontSize = 11.sp,
                                color = FinancialColors.Primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Text(
                        text = report.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinancialColors.OnSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 24.sp
                    )
                }

                ModernSentimentTag(sentiment = report.sentiment)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 그래프 미리보기
            if (report.graphData.isNotEmpty()) {
                val firstGraph = report.graphData.first()
                ModernGraphPreview(
                    graphData = firstGraph,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinancialColors.SurfaceVariant)
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 요약
            Text(
                text = report.summary,
                fontSize = 14.sp,
                color = FinancialColors.OnSurfaceVariant,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 더보기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "자세히 보기",
                    fontSize = 14.sp,
                    color = FinancialColors.Primary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = FinancialColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ModernSentimentTag(sentiment: ReportSentiment) {
    val backgroundColor = sentiment.getColor().copy(alpha = 0.12f)
    val contentColor = sentiment.getColor()

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, contentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = sentiment.getDisplayName(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Composable
fun ModernGraphPreview(
    graphData: GraphData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (graphData.type) {
            "LINE_CHART" -> ModernLineChart(data = graphData.data)
            "BAR_CHART" -> ModernBarChart(data = graphData.data)
            "PIE_CHART" -> ModernPieChart(data = graphData.data)
            else -> {
                // 기본 플레이스홀더
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "차트 데이터 없음",
                        color = FinancialColors.OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 그래프 제목
        if (graphData.title.isNotEmpty()) {
            Text(
                text = graphData.title,
                fontSize = 11.sp,
                color = FinancialColors.OnSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(
                        FinancialColors.Surface.copy(alpha = 0.9f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun ModernLineChart(data: Map<String, Float>) {
    if (data.isEmpty() || data.size < 2) return

    val values = data.values.toList()
    val keys = data.keys.toList()
    val max = values.maxOrNull() ?: 0f
    val min = values.minOrNull() ?: 0f
    val range = (max - min).takeIf { it != 0f } ?: 1f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width - 40.dp.toPx()
        val height = size.height - 40.dp.toPx()
        val startX = 20.dp.toPx()
        val startY = 20.dp.toPx()

        val stepX = width / (values.size - 1)

        // 그리드 라인 그리기
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = startY + (height / gridLines) * i
            drawLine(
                color = FinancialColors.OnSurfaceVariant.copy(alpha = 0.1f),
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

        // 그라데이션 배경 영역
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
                    FinancialColors.GraphPrimary.copy(alpha = 0.3f),
                    FinancialColors.GraphPrimary.copy(alpha = 0.05f)
                )
            )
        )

        // 메인 라인 그리기
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
            color = FinancialColors.GraphPrimary,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 데이터 포인트 원 그리기
        points.forEach { point ->
            drawCircle(
                color = FinancialColors.Surface,
                radius = 6.dp.toPx(),
                center = point,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = FinancialColors.GraphPrimary,
                radius = 4.dp.toPx(),
                center = point
            )
        }

        // Y축 레이블
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#64748B")
            textSize = 24f
            isAntiAlias = true
        }

        for (i in 0..2) {
            val value = min + (max - min) * (2 - i) / 2
            val y = startY + (height / 2) * i
            drawContext.canvas.nativeCanvas.drawText(
                "%.0f".format(value),
                8f,
                y + 8f,
                labelPaint
            )
        }
    }
}

@Composable
fun ModernBarChart(data: Map<String, Float>) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val max = values.maxOrNull() ?: 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width - 40.dp.toPx()
        val height = size.height - 40.dp.toPx()
        val startX = 20.dp.toPx()
        val startY = 20.dp.toPx()

        val barWidth = (width / values.size) * 0.6f
        val barSpacing = (width / values.size) * 0.4f

        values.forEachIndexed { index, value ->
            val barHeight = (value / max) * height
            val x = startX + index * (barWidth + barSpacing) + barSpacing / 2
            val y = startY + height - barHeight

            // 그라데이션 막대
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FinancialColors.GraphPrimary,
                        FinancialColors.GraphPrimary.copy(alpha = 0.7f)
                    )
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
fun ModernPieChart(data: Map<String, Float>) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val total = values.sum()
    val colors = listOf(
        FinancialColors.GraphPrimary,
        FinancialColors.GraphSecondary,
        FinancialColors.GraphTertiary,
        FinancialColors.GraphAccent,
        FinancialColors.Success,
        FinancialColors.Warning
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) * 0.8f
        val innerRadius = radius * 0.5f

        var startAngle = -90f

        values.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f
            val color = colors[index % colors.size]

            // 외부 원호
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = (radius - innerRadius))
            )

            startAngle += sweepAngle
        }

        // 중앙 원
        drawCircle(
            color = FinancialColors.Surface,
            radius = innerRadius,
            center = Offset(centerX, centerY)
        )
    }
}

/**
 * 현대적인 보고서 상세 화면 - 애플/토스 스타일
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernReportDetailScreen(
    report: AnalystReport,
    onBackPressed: () -> Unit
) {
    var isHeaderCollapsed by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // 스크롤에 따른 헤더 상태 변경
    LaunchedEffect(scrollState.value) {
        isHeaderCollapsed = scrollState.value > 200
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FinancialColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 히어로 헤더 섹션
            item {
                ModernHeroHeader(
                    report = report,
                    isCollapsed = isHeaderCollapsed
                )
            }

            // Executive Summary
            item {
                ModernSummaryCard(
                    title = "Executive Summary",
                    content = report.summary
                )
            }

            // 주요 지표 카드들
            item {
                ModernMetricsSection(report = report)
            }

            // 차트 섹션들
            report.graphData.forEach { graphData ->
                item {
                    ModernChartSection(
                        graphData = graphData,
                        report = report
                    )
                }
            }

            // 상세 분석 내용
            item {
                ModernAnalysisCard(
                    title = "상세 분석",
                    content = report.detailedContent
                )
            }

            // 투자 포인트
            item {
                ModernInvestmentPoints(report = report)
            }

            // 리스크 분석
            item {
                ModernRiskAnalysis(report = report)
            }
        }

        // 플로팅 백 버튼
        FloatingBackButton(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // 플로팅 액션 버튼들
        FloatingActionButtons(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}

@Composable
fun ModernHeroHeader(
    report: AnalystReport,
    isCollapsed: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCollapsed) 200.dp else 320.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FinancialColors.Primary,
                        FinancialColors.Primary.copy(alpha = 0.8f),
                        FinancialColors.Secondary
                    )
                )
            )
    ) {
        // 백그라운드 패턴
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pattern = listOf(
                Offset(size.width * 0.1f, size.height * 0.2f),
                Offset(size.width * 0.9f, size.height * 0.1f),
                Offset(size.width * 0.2f, size.height * 0.8f),
                Offset(size.width * 0.8f, size.height * 0.9f)
            )

            pattern.forEach { offset ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 60.dp.toPx(),
                    center = offset
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // 메타 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = report.category,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                Text(
                    text = dateFormat.format(report.date),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                ModernSentimentBadge(
                    sentiment = report.sentiment,
                    isDark = true
                )
            }

            // 제목
            Text(
                text = report.title,
                color = Color.White,
                fontSize = if (isCollapsed) 20.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = if (isCollapsed) 28.sp else 36.sp,
                maxLines = if (isCollapsed) 2 else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (!isCollapsed) {
                Spacer(modifier = Modifier.height(16.dp))

                // 주요 수치 미리보기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernStatItem(
                        label = "목표가",
                        value = "₩65,000",
                        change = "+8.3%",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    ModernStatItem(
                        label = "현재가",
                        value = "₩60,100",
                        change = "-2.1%",
                        isPositive = false,
                        modifier = Modifier.weight(1f)
                    )
                    ModernStatItem(
                        label = "예상수익률",
                        value = "8.2%",
                        change = "3개월",
                        isPositive = null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernStatItem(
    label: String,
    value: String,
    change: String,
    isPositive: Boolean?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = change,
            color = when (isPositive) {
                true -> FinancialColors.Success
                false -> FinancialColors.Error
                null -> Color.White.copy(alpha = 0.8f)
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ModernSentimentBadge(
    sentiment: ReportSentiment,
    isDark: Boolean = false
) {
    val backgroundColor = if (isDark) {
        Color.White.copy(alpha = 0.2f)
    } else {
        sentiment.getColor().copy(alpha = 0.15f)
    }

    val contentColor = if (isDark) {
        Color.White
    } else {
        sentiment.getColor()
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = when (sentiment) {
                    ReportSentiment.POSITIVE -> Icons.Default.AccountBox
                    ReportSentiment.NEGATIVE -> Icons.Default.AccountBox
                    else -> Icons.Default.Warning
                },
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = sentiment.getDisplayName(),
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ModernSummaryCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            FinancialColors.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = FinancialColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinancialColors.OnSurface
                )
            }

            Text(
                text = content,
                fontSize = 16.sp,
                color = FinancialColors.OnSurfaceVariant,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ModernMetricsSection(report: AnalystReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "주요 지표",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FinancialColors.OnSurface,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 지표 그리드
            val metrics = listOf(
                Triple("PER", "12.5x", FinancialColors.GraphPrimary),
                Triple("PBR", "1.8x", FinancialColors.GraphSecondary),
                Triple("ROE", "15.2%", FinancialColors.Success),
                Triple("부채비율", "45.3%", FinancialColors.Warning)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(160.dp)
            ) {
                items(metrics) { (label, value, color) ->
                    ModernMetricCard(
                        label = label,
                        value = value,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun ModernMetricCard(
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color.copy(alpha = 0.08f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = FinancialColors.OnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModernChartSection(
    graphData: GraphData,
    report: AnalystReport
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // 차트 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = graphData.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinancialColors.OnSurface
                    )

                    Text(
                        text = "최근 데이터 기준",
                        fontSize = 12.sp,
                        color = FinancialColors.OnSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // 차트 타입 아이콘
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            FinancialColors.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (graphData.type) {
                            "LINE_CHART" -> Icons.Default.AccountBox
                            "BAR_CHART" -> Icons.Default.AccountBox
                            "PIE_CHART" -> Icons.Default.DateRange
                            else -> Icons.Default.DateRange
                        },
                        contentDescription = null,
                        tint = FinancialColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 향상된 차트
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        FinancialColors.SurfaceVariant,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                when (graphData.type) {
                    "LINE_CHART" -> EnhancedLineChart(
                        data = graphData.data,
                        title = graphData.title
                    )
                    "BAR_CHART" -> EnhancedBarChart(
                        data = graphData.data,
                        title = graphData.title
                    )
                    "PIE_CHART" -> EnhancedPieChart(
                        data = graphData.data,
                        title = graphData.title
                    )
                }
            }

            // 차트 인사이트
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        FinancialColors.Primary.copy(alpha = 0.05f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = FinancialColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "핵심 인사이트",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FinancialColors.Primary
                        )

                        Text(
                            text = generateChartInsight(graphData),
                            fontSize = 14.sp,
                            color = FinancialColors.OnSurfaceVariant,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedLineChart(
    data: Map<String, Float>,
    title: String
) {
    if (data.isEmpty() || data.size < 2) return

    val values = data.values.toList()
    val keys = data.keys.toList()
    val max = values.maxOrNull() ?: 0f
    val min = values.minOrNull() ?: 0f
    val range = (max - min).takeIf { it != 0f } ?: 1f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width - 60.dp.toPx()
        val height = size.height - 60.dp.toPx()
        val startX = 40.dp.toPx()
        val startY = 20.dp.toPx()

        val stepX = width / (values.size - 1)

        // 배경 그리드
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = startY + (height / gridLines) * i
            drawLine(
                color = FinancialColors.OnSurfaceVariant.copy(alpha = 0.1f),
                start = Offset(startX, y),
                end = Offset(startX + width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            )
        }

        // Y축 레이블
        for (i in 0..4) {
            val value = min + (max - min) * (4 - i) / 4
            val y = startY + (height / 4) * i

            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", value),
                10f,
                y + 8f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#64748B")
                    textSize = 28f
                    isAntiAlias = true
                }
            )
        }

        // 데이터 포인트 계산
        val points = values.mapIndexed { index, value ->
            val x = startX + index * stepX
            val y = startY + height - ((value - min) / range) * height
            Offset(x, y)
        }

        // 그라데이션 영역
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
                    FinancialColors.GraphPrimary.copy(alpha = 0.4f),
                    FinancialColors.GraphPrimary.copy(alpha = 0.1f),
                    Color.Transparent
                )
            )
        )

        // 메인 라인
        val linePath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val cp1x = points[i-1].x + (points[i].x - points[i-1].x) * 0.5f
                    val cp1y = points[i-1].y
                    val cp2x = points[i-1].x + (points[i].x - points[i-1].x) * 0.5f
                    val cp2y = points[i].y
                    cubicTo(cp1x, cp1y, cp2x, cp2y, points[i].x, points[i].y)
                }
            }
        }

        drawPath(
            path = linePath,
            color = FinancialColors.GraphPrimary,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 데이터 포인트
        points.forEachIndexed { index, point ->
            // 외곽 원
            drawCircle(
                color = FinancialColors.Surface,
                radius = 8.dp.toPx(),
                center = point
            )
            // 내부 원
            drawCircle(
                color = FinancialColors.GraphPrimary,
                radius = 5.dp.toPx(),
                center = point
            )
        }

        // X축 레이블 (처음, 중간, 끝)
        val labelIndices = listOf(0, keys.size / 2, keys.size - 1)
        labelIndices.forEach { index ->
            val x = startX + index * stepX
            val label = keys[index].takeLast(5) // 마지막 5글자만

            drawContext.canvas.nativeCanvas.drawText(
                label,
                x - 30f,
                startY + height + 35f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#64748B")
                    textSize = 26f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
fun EnhancedBarChart(
    data: Map<String, Float>,
    title: String
) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val keys = data.keys.toList()
    val max = values.maxOrNull() ?: 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width - 60.dp.toPx()
        val height = size.height - 60.dp.toPx()
        val startX = 40.dp.toPx()
        val startY = 20.dp.toPx()

        val barWidth = (width / values.size) * 0.6f
        val barSpacing = (width / values.size) * 0.4f

        // 배경 그리드
        for (i in 0..4) {
            val y = startY + (height / 4) * i
            drawLine(
                color = FinancialColors.OnSurfaceVariant.copy(alpha = 0.1f),
                start = Offset(startX, y),
                end = Offset(startX + width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            )
        }

        values.forEachIndexed { index, value ->
            val barHeight = (value / max) * height
            val x = startX + index * (barWidth + barSpacing) + barSpacing / 2
            val y = startY + height - barHeight

            // 막대 그라데이션
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FinancialColors.GraphPrimary,
                        FinancialColors.GraphPrimary.copy(alpha = 0.8f)
                    )
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
            )

            // 값 표시
            if (barHeight > 40.dp.toPx()) {
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.0f", value),
                    x + barWidth / 2,
                    y + 30f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 24f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }

            // X축 레이블
            drawContext.canvas.nativeCanvas.drawText(
                keys[index].takeLast(3),
                x + barWidth / 2,
                startY + height + 35f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#64748B")
                    textSize = 24f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
fun EnhancedPieChart(
    data: Map<String, Float>,
    title: String
) {
    if (data.isEmpty()) return

    val values = data.values.toList()
    val keys = data.keys.toList()
    val total = values.sum()
    val colors = listOf(
        FinancialColors.GraphPrimary,
        FinancialColors.GraphSecondary,
        FinancialColors.GraphTertiary,
        FinancialColors.GraphAccent,
        FinancialColors.Success,
        FinancialColors.Warning
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) * 0.7f
        val innerRadius = radius * 0.6f

        var startAngle = -90f

        values.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f
            var color = colors[index % colors.size]

            // 외부 원호 (두꺼운 도넛)
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = radius - innerRadius)
            )

            // 레이블 표시
            val labelAngle = startAngle + sweepAngle / 2
            val labelRadius = (radius + innerRadius) / 2
            val labelX = centerX + cos(Math.toRadians(labelAngle.toDouble())).toFloat() * labelRadius
            val labelY = centerY + sin(Math.toRadians(labelAngle.toDouble())).toFloat() * labelRadius

            val percentage = (value / total * 100).toInt()
            if (percentage > 5) { // 5% 이상만 레이블 표시
                drawContext.canvas.nativeCanvas.drawText(
                    "$percentage%",
                    labelX,
                    labelY + 8f,
                    android.graphics.Paint().apply {
                        color = Color(1,1,1,1)
                        textSize = 28f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                )
            }

            startAngle += sweepAngle
        }

        // 중앙 원
        drawCircle(
            color = FinancialColors.Surface,
            radius = innerRadius,
            center = Offset(centerX, centerY)
        )

        // 중앙 텍스트
        drawContext.canvas.nativeCanvas.drawText(
            "총합",
            centerX,
            centerY - 10f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#64748B")
                textSize = 32f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            String.format("%.0f", total),
            centerX,
            centerY + 25f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#1E293B")
                textSize = 36f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
        )
    }
}

// 누락된 부분들을 완성합니다

@Composable
fun ModernAnalysisCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            FinancialColors.Secondary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = FinancialColors.Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinancialColors.OnSurface
                )
            }

            Text(
                text = content,
                fontSize = 16.sp,
                color = FinancialColors.OnSurfaceVariant,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ModernInvestmentPoints(report: AnalystReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            FinancialColors.Success.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = FinancialColors.Success,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "투자 포인트",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinancialColors.OnSurface
                )
            }

            // 투자 포인트 리스트
            val investmentPoints = listOf(
                "강력한 시장 지배력과 지속적인 성장 모멘텀",
                "디지털 전환을 통한 효율성 개선",
                "ESG 경영으로 인한 지속가능한 가치 창출",
                "글로벌 확장을 통한 수익 다변화"
            )

            investmentPoints.forEachIndexed { index, point ->
                ModernBulletPoint(
                    text = point,
                    index = index + 1,
                    color = FinancialColors.Success
                )

                if (index < investmentPoints.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ModernRiskAnalysis(report: AnalystReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FinancialColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            FinancialColors.Warning.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = FinancialColors.Warning,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "리스크 분석",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinancialColors.OnSurface
                )
            }

            // 리스크 레벨 표시
            ModernRiskLevel(
                level = "중간",
                percentage = 0.6f,
                color = FinancialColors.Warning
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 리스크 요소들
            val riskFactors = listOf(
                "시장 변동성에 따른 주가 하락 위험",
                "경쟁 심화로 인한 마진 압박 가능성",
                "규제 변화에 따른 사업 영향",
                "환율 변동에 따른 수익성 변화"
            )

            riskFactors.forEachIndexed { index, risk ->
                ModernBulletPoint(
                    text = risk,
                    index = index + 1,
                    color = FinancialColors.Warning
                )

                if (index < riskFactors.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ModernBulletPoint(
    text: String,
    index: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Text(
            text = text,
            fontSize = 15.sp,
            color = FinancialColors.OnSurfaceVariant,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ModernRiskLevel(
    level: String,
    percentage: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "리스크 레벨: $level",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = FinancialColors.OnSurface
            )

            Text(
                text = "${(percentage * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 프로그레스 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color.copy(alpha = 0.1f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(
                        color,
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun FloatingBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        containerColor = FinancialColors.Surface.copy(alpha = 0.9f),
        contentColor = FinancialColors.OnSurface,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun FloatingActionButtons(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 공유 버튼
        FloatingActionButton(
            onClick = { /* 공유 기능 */ },
            modifier = Modifier.size(48.dp),
            containerColor = FinancialColors.Primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "공유",
                modifier = Modifier.size(20.dp)
            )
        }

        // 즐겨찾기 버튼
        FloatingActionButton(
            onClick = { /* 즐겨찾기 기능 */ },
            modifier = Modifier.size(48.dp),
            containerColor = FinancialColors.Secondary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "북마크",
                modifier = Modifier.size(20.dp)
            )
        }

        // 알림 설정 버튼
        FloatingActionButton(
            onClick = { /* 알림 설정 */ },
            modifier = Modifier.size(48.dp),
            containerColor = FinancialColors.Warning,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "알림",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// 차트 인사이트 생성 함수
fun generateChartInsight(graphData: GraphData): String {
    return when (graphData.type) {
        "LINE_CHART" -> {
            val values = graphData.data.values.toList()
            if (values.size >= 2) {
                val trend = if (values.last() > values.first()) "상승" else "하락"
                val change = ((values.last() - values.first()) / values.first() * 100).let {
                    if (it.isFinite()) "%.1f".format(kotlin.math.abs(it)) else "0.0"
                }
                "$trend 추세를 보이며, 전체 기간 대비 ${change}% 변화를 기록했습니다."
            } else {
                "데이터가 충분하지 않아 추세 분석이 어렵습니다."
            }
        }
        "BAR_CHART" -> {
            val maxEntry = graphData.data.maxByOrNull { it.value }
            val minEntry = graphData.data.minByOrNull { it.value }
            if (maxEntry != null && minEntry != null) {
                "최고값은 ${maxEntry.key}(%.1f)이며, 최저값은 ${minEntry.key}(%.1f)입니다."
                    .format(maxEntry.value, minEntry.value)
            } else {
                "데이터 분석 결과를 표시할 수 없습니다."
            }
        }
        "PIE_CHART" -> {
            val total = graphData.data.values.sum()
            val largest = graphData.data.maxByOrNull { it.value }
            val percentage = largest?.let { (it.value / total * 100).toInt() } ?: 0
            "전체에서 ${largest?.key ?: "알 수 없음"}이 ${percentage}%로 가장 큰 비중을 차지합니다."
        }
        else -> "차트 데이터를 분석 중입니다."
    }
}

// 추가 확장 함수들
fun ReportSentiment.getColor(): Color = when (this) {
    ReportSentiment.POSITIVE -> FinancialColors.Success
    ReportSentiment.NEGATIVE -> FinancialColors.Error
    ReportSentiment.NEUTRAL -> FinancialColors.Neutral
    ReportSentiment.CAUTION -> FinancialColors.Neutral
}

fun ReportSentiment.getDisplayName(): String = when (this) {
    ReportSentiment.POSITIVE -> "긍정적"
    ReportSentiment.NEGATIVE -> "부정적"
    ReportSentiment.NEUTRAL -> "중립적"
    ReportSentiment.CAUTION -> "주의"
}