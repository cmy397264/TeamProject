package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType
import com.bigPicture.businessreportgenerator.data.local.StockViewModel
import com.bigPicture.businessreportgenerator.presentation.common.PieChart
import com.bigPicture.businessreportgenerator.presentation.common.PieChartData
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

// InvestmentTip 정의 (임시로 이 파일에 추가)
data class InvestmentTip(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val gradient: Brush
)

// InvestmentTips 정의 (임시로 이 파일에 추가)
val InvestmentTips = listOf(
    InvestmentTip(
        title = "분산 투자의 힘",
        description = "여러 종목에 분산 투자하여 리스크를 줄이고 안정적인 수익을 추구하세요.",
        icon = Icons.Rounded.Star,
        iconColor = Color.White,
        gradient = Brush.linearGradient(
            listOf(
                Color(0xFF667eea),
                Color(0xFF764ba2)
            )
        )
    ),
    InvestmentTip(
        title = "장기 투자 전략",
        description = "시간의 힘을 활용한 장기 투자로 복리 효과를 누려보세요.",
        icon = Icons.Rounded.Edit,
        iconColor = Color.White,
        gradient = Brush.linearGradient(
            listOf(
                Color(0xFF10B981),
                Color(0xFF059669)
            )
        )
    ),
    InvestmentTip(
        title = "정기적인 모니터링",
        description = "포트폴리오를 정기적으로 점검하고 필요시 리밸런싱하세요.",
        icon = Icons.Rounded.Done,
        iconColor = Color.White,
        gradient = Brush.linearGradient(
            listOf(
                Color(0xFF8B5CF6),
                Color(0xFF7C3AED)
            )
        )
    )
)

@Composable
fun PortfolioScreen(
    modifier: Modifier = Modifier
) {
    val portfolioViewModel: PortfolioViewModel = koinViewModel()
    val portfolioState by portfolioViewModel.state.collectAsState()
    val stockViewModel: StockViewModel = koinViewModel()
    val scrollState = rememberScrollState()

    // 현대적인 컬러 팔레트
    val modernColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Purple
        Color(0xFF06B6D4), // Cyan
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFFEF4444), // Red
        Color(0xFFEC4899), // Pink
        Color(0xFF84CC16)  // Lime
    )

    val pieChartData = portfolioState.assets.mapIndexed { index, asset ->
        PieChartData(
            value = (asset.getCurrentValue() ?: asset.purchasePrice).toFloat(),
            color = modernColors[index % modernColors.size],
            label = asset.name
        )
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 커스텀 헤더
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Portfolio",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    if (portfolioState.assets.any { it.type == AssetType.STOCK }) {
                        IconButton(
                            onClick = {
                                // refreshStockPrices 메서드가 정의되어 있다면 주석 해제
                                // portfolioViewModel.refreshStockPrices()
                            },
                            enabled = !portfolioState.isLoadingPrices
                        ) {
                            if (portfolioState.isLoadingPrices) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF6366F1)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "가격 새로고침",
                                    tint = Color(0xFF6366F1)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(600, easing = FastOutSlowInEasing)) +
                            slideInVertically(tween(600, easing = LinearOutSlowInEasing)) { it / 3 }
                ) {
                    if (portfolioState.assets.isEmpty()) {
                        ModernEmptyPortfolioContent(
                            onAddClick = { portfolioViewModel.showAddAssetDialog() },
                            onShowSampleClick = { portfolioViewModel.showSamplePortfolioDialog() }
                        )
                    } else {
                        PopulatedPortfolioContent(
                            portfolioState = portfolioState,
                            formatter = formatter,
                            pieChartData = pieChartData,
                            onAddClick = { portfolioViewModel.showAddAssetDialog() }
                        )
                    }
                }
            }

            if (portfolioState.isAddAssetDialogVisible) {
                AddAssetDialog(
                    onDismiss = { portfolioViewModel.hideAddAssetDialog() },
                    onAddAsset = {
                        portfolioViewModel.addAsset(it)
                        if (it.type == AssetType.STOCK) {
                            val market = it.details["market"]
                            val stockType = if (market == "코스피" || market == "코스닥") "korea" else "us"
                            stockViewModel.registerStock(stockType, it.name)
                        }
                    }
                )
            }

            if (portfolioState.isSamplePortfolioDialogVisible) {
                SamplePortfolioDialog(
                    sampleAssets = portfolioState.sampleAssets,
                    onDismiss = { portfolioViewModel.hideSamplePortfolioDialog() },
                    onLoad = {
                        portfolioViewModel.loadSamplePortfolio()
                        portfolioViewModel.hideSamplePortfolioDialog()
                    }
                )
            }
        }
    }
}

@Composable
fun PopulatedPortfolioContent(
    portfolioState: ModernPortfolioState,
    formatter: NumberFormat,
    pieChartData: List<PieChartData>,
    onAddClick: () -> Unit
) {
    val animatedTotalValue by animateFloatAsState(
        targetValue = portfolioState.totalPortfolioValue.toFloat(),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "totalValue"
    )

    val animatedReturnValue by animateFloatAsState(
        targetValue = portfolioState.totalReturnValue.toFloat(),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "returnValue"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 향상된 총 자산 카드 (수익률 포함)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (portfolioState.totalReturnValue >= 0) {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF059669)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFFDC2626)
                                )
                            )
                        },
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(28.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "총 자산",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = formatter.format(animatedTotalValue.toDouble()),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(Modifier.height(12.dp))

                            // 수익률 표시
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (portfolioState.totalReturnValue >= 0) {
                                        Icons.Outlined.KeyboardArrowUp
                                    } else {
                                        Icons.Outlined.KeyboardArrowDown
                                    },
                                    contentDescription = "수익률",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${if (portfolioState.totalReturnValue >= 0) "+" else ""}${formatter.format(animatedReturnValue.toDouble())}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${if (portfolioState.totalReturnPercentage >= 0) "+" else ""}${String.format("%.2f", portfolioState.totalReturnPercentage)}%",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // 성과 아이콘
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (portfolioState.totalReturnValue >= 0) {
                                    Icons.Rounded.Done
                                } else {
                                    Icons.Rounded.Star
                                },
                                contentDescription = "수익률",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // 현대적인 퀵 액션 버튼들
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ModernQuickActionButton(
                            icon = Icons.Rounded.Edit,
                            label = "분석",
                            onClick = {}
                        )
                        ModernQuickActionButton(
                            icon = Icons.Rounded.Star,
                            label = "성과",
                            onClick = {}
                        )
                        ModernQuickActionButton(
                            icon = Icons.Filled.Add,
                            label = "추가",
                            onClick = onAddClick
                        )
                    }
                }
            }
        }

        // 자산 구성 카드 - 현재 시장가치 기준
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "자산 구성 (현재 시장가치)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    TextButton(onClick = {}) {
                        Text(
                            text = "상세보기",
                            fontSize = 15.sp,
                            color = Color(0xFF6366F1),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))

                // 향상된 파이 차트
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    PieChart(data = pieChartData, assets = portfolioState.assets)
                }
            }
        }

        // 투자 종목 목록 - 수익률 포함
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "보유 종목",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    if (portfolioState.isLoadingPrices) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF6366F1)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "가격 업데이트 중...",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))

                portfolioState.assets.forEachIndexed { index, asset ->
                    EnhancedAssetListItem(
                        asset = asset,
                        totalValue = portfolioState.totalPortfolioValue,
                        color = pieChartData[index].color
                    )
                    if (index < portfolioState.assets.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color(0xFFE2E8F0),
                            thickness = 1.dp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // 현대적인 추가 버튼
                Button(
                    onClick = onAddClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "추가",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "종목 추가하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedAssetListItem(
    asset: Asset,
    totalValue: Double,
    color: Color
) {
    val currentValue = asset.getCurrentValue() ?: asset.purchasePrice
    val proportion = if (totalValue > 0) (currentValue / totalValue) * 100 else 0.0
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)
    val returnPercentage = asset.getReturnPercentage()
    val returnAmount = asset.getReturnAmount()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 현대적인 아이콘 디자인
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

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = asset.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

                        // 티커 표시 (주식인 경우)
                        asset.ticker?.let { ticker ->
                            Text(
                                text = " • $ticker",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatter.format(currentValue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    // 수익률 표시 (주식이고 현재가가 있는 경우)
                    if (asset.type == AssetType.STOCK && returnPercentage != null && returnAmount != null) {
                        val isProfit = returnAmount >= 0
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                imageVector = if (isProfit) {
                                    Icons.Outlined.KeyboardArrowUp
                                } else {
                                    Icons.Outlined.KeyboardArrowDown
                                },
                                contentDescription = "수익률",
                                tint = if (isProfit) Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = "${if (isProfit) "+" else ""}${String.format("%.2f", returnPercentage)}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isProfit) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${proportion.toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = color
                        )
                    }
                }
            }

            // 주식인 경우 매입가 vs 현재가 정보 표시
            if (asset.type == AssetType.STOCK && asset.currentPrice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val avgPrice = asset.details["averagePrice"]?.toDoubleOrNull() ?: asset.purchasePrice

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "평단가: ${formatter.format(avgPrice)}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "현재가: ${formatter.format(asset.currentPrice)}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // 마지막 업데이트 시간
                asset.lastUpdated?.let { lastUpdated ->
                    val timeDiff = System.currentTimeMillis() - lastUpdated
                    val minutesAgo = (timeDiff / (1000 * 60)).toInt()
                    if (minutesAgo < 60) {
                        Text(
                            text = "${minutesAgo}분 전 업데이트",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernEmptyPortfolioContent(
    onAddClick: () -> Unit,
    onShowSampleClick: () -> Unit
) {
    // 랜덤 투자 팁 선택
    var currentTip by remember { mutableStateOf(InvestmentTips.random()) }

    LaunchedEffect(Unit) {
        // 앱 시작시 랜덤 팁 선택
        currentTip = InvestmentTips[Random.nextInt(InvestmentTips.size)]
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 현대적인 투자 팁 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = currentTip.gradient,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentTip.icon,
                            contentDescription = currentTip.title,
                            tint = currentTip.iconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentTip.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTip.description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // 현대적인 시작하기 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 현대적인 아이콘 디자인
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.1f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Face,
                        contentDescription = "포트폴리오",
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "투자를 시작해보세요",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "종목을 추가하여 포트폴리오를 구성하고\n실시간 수익률을 확인해보세요",
                    fontSize = 16.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                // 현대적인 CTA 버튼
                Button(
                    onClick = onAddClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "추가",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "첫 종목 추가하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onShowSampleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6366F1)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "샘플",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "샘플 포트폴리오 보기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamplePortfolioDialog(
    sampleAssets: List<Asset>,
    onDismiss: () -> Unit,
    onLoad: () -> Unit
) {
    val modernColors = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFFEF4444),
        Color(0xFFEC4899),
        Color(0xFF84CC16)
    )

    val pieData = sampleAssets.mapIndexed { idx, asset ->
        PieChartData(
            value = asset.purchasePrice.toFloat(),
            color = modernColors[idx % modernColors.size],
            label = asset.name
        )
    }

    val totalSampleValue = sampleAssets.sumOf { it.purchasePrice }
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(animationSpec = tween(400)) { it / 3 },
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .heightIn(max = 680.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        // 현대적인 헤더
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFF667eea),
                                            Color(0xFF764ba2)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Star,
                                            contentDescription = "샘플",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "샘플 포트폴리오",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "테크 중심 포트폴리오",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "닫기",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            // 총 자산 정보
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8FAFC)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "총 투자금액",
                                            fontSize = 14.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatter.format(totalSampleValue),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFF6366F1),
                                                        Color(0xFF8B5CF6)
                                                    )
                                                )
                                            )
                                            .padding(14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit,
                                            contentDescription = "분석",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 파이 차트
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        text = "자산 구성",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    ) {
                                        PieChart(
                                            data = pieData,
                                            assets = sampleAssets
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 종목 리스트
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        text = "보유 종목",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 200.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(sampleAssets) { asset ->
                                            ModernAssetListItem(
                                                asset = asset,
                                                totalValue = totalSampleValue,
                                                color = modernColors[sampleAssets.indexOf(asset) % modernColors.size]
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 설명 카드
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF0F9FF)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = "정보",
                                        tint = Color(0xFF0EA5E9),
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "글로벌 테크 기업 중심의 성장형 포트폴리오입니다.\n실시간 가격 업데이트와 수익률 확인이 가능합니다.",
                                        fontSize = 14.sp,
                                        color = Color(0xFF0369A1),
                                        lineHeight = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // 액션 버튼들
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF64748B)
                                    )
                                ) {
                                    Text(
                                        text = "취소",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = onLoad,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .shadow(
                                            elevation = 6.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6366F1),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = "포트폴리오 적용",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernQuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernAssetListItem(
    asset: Asset,
    totalValue: Double,
    color: Color
) {
    val proportion = if (totalValue > 0) (asset.purchasePrice / totalValue) * 100 else 0.0
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 현대적인 아이콘 디자인
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

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
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
                text = formatter.format(asset.purchasePrice),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${proportion.toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = color
                )
            }
        }
    }
}