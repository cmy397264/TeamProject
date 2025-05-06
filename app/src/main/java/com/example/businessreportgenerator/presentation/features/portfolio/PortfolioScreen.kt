package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.businessreportgenerator.domain.model.Asset
import com.example.businessreportgenerator.domain.model.AssetType
import com.example.businessreportgenerator.presentation.common.AppTopBar
import com.example.businessreportgenerator.presentation.common.PieChart
import com.example.businessreportgenerator.presentation.common.PieChartData
import java.text.NumberFormat
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: PortfolioViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    val colors = listOf(
        Color(0xFF007AFF),
        Color(0xFFFF9500),
        Color(0xFF4CD964),
        Color(0xFFFF2D55),
        Color(0xFF5856D6),
        Color(0xFFFFCC00),
        Color(0xFF34C759),
        Color(0xFFAF52DE)
    )

    val pieChartData = state.assets.mapIndexed { index, asset ->
        PieChartData(
            value = asset.purchasePrice.toFloat(),
            color = colors[index % colors.size],
            label = asset.name
        )
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(title = "My Portfolio")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 16.dp)
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
                ) {
                    if (state.assets.isEmpty()) {
                        EmptyPortfolioContent(
                            onAddClick       = { viewModel.showAddAssetDialog() },
                            onShowSampleClick = { viewModel.showSamplePortfolioDialog() }
                        )
                    } else {
                        PopulatedPortfolioContent(
                            totalValue  = state.totalPortfolioValue,
                            formatter   = formatter,
                            pieChartData = pieChartData,
                            assets      = state.assets,
                            onAddClick  = { viewModel.showAddAssetDialog() }
                        )
                    }
                }
            }

            if (state.isAddAssetDialogVisible) {
                AddAssetDialog(
                    onDismiss   = { viewModel.hideAddAssetDialog() },
                    onAddAsset  = { viewModel.addAsset(it) }
                )
            }

            if (state.isSamplePortfolioDialogVisible) {
                SamplePortfolioDialog(
                    sampleAssets = state.sampleAssets,
                    onDismiss    = { viewModel.hideSamplePortfolioDialog()},
                    onLoad = {
                        viewModel.loadSamplePortfolio()
                        viewModel.hideSamplePortfolioDialog()
                    }
                )
            }
            if (state.isSamplePortfolioDialogVisible) {
                SamplePortfolioDialog(
                    sampleAssets = state.sampleAssets,
                    onDismiss    = { viewModel.hideSamplePortfolioDialog() },
                    onLoad       = {
                        viewModel.loadSamplePortfolio()
                        viewModel.hideSamplePortfolioDialog()
                        }
                )
            }
        }
    }
}

@Composable
fun PopulatedPortfolioContent(
    totalValue: Double,
    formatter: NumberFormat,
    pieChartData: List<PieChartData>,
    assets: List<com.example.businessreportgenerator.domain.model.Asset>,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 요약 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFF2C7AFA), Color(0xFF1A54D5))),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "총 자산 가치",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatter.format(totalValue),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowUp,
                                contentDescription = "상승",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "3.2%",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(icon = Icons.Rounded.Person, label = "자산 분석", onClick = {})
                        QuickActionButton(icon = Icons.Rounded.ThumbUp, label = "퍼포먼스", onClick = {})
                        QuickActionButton(icon = Icons.Filled.Add, label = "종목 추가", onClick = onAddClick)
                    }
                }
            }
        }

        // 자산 구성 카드
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "자산 구성", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = {}) {
                        Text(text = "상세보기", fontSize = 14.sp, color = Color(0xFF007AFF))
                    }
                }
                Spacer(Modifier.height(16.dp))
                PieChart(data = pieChartData, assets = assets)
            }
        }

        // 투자 종목 목록
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(text = "투자 종목", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                assets.forEachIndexed { index, asset ->
                    AssetListItem(
                        asset = asset,
                        totalValue = totalValue,
                        color = pieChartData[index].color
                    )
                    if (index < assets.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.5.dp,
                        brush = SolidColor(Color(0xFF007AFF))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF007AFF))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "추가", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "종목 추가하기", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun EmptyPortfolioContent(
    onAddClick: () -> Unit,
    onShowSampleClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 오늘의 투자 팁
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "오늘의 투자 팁", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE1F5FE))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Daily", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0288D1))
                    }
                }
                Spacer(Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(investmentTips) { tip ->
                        InvestmentTipCard(tip)
                    }
                }
            }
        }

        // 시작하기 카드
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(min = 380.dp).padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(160.dp).clip(CircleShape).background(Color(0xFFF0F6FF)).padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "포트폴리오",
                        tint = Color(0xFF007AFF),
                        modifier = Modifier.size(70.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(text = "포트폴리오 관리를 시작하세요", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "종목을 추가하여 자산을 효율적으로 관리하고 투자 성과를 분석해보세요.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onAddClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(54.dp)
                        .shadow(4.dp, RoundedCornerShape(27.dp), spotColor = Color(0xFF007AFF).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "추가", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "종목 추가하기", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onShowSampleClick,
                    modifier = Modifier.fillMaxWidth(0.8f).height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp, brush = SolidColor(Color(0xFF007AFF))),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF007AFF))
                ) {
                    Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = "샘플", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "샘플 포트폴리오 보기", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp)
        }
    }
}

@Composable
fun AssetListItem(
    asset: com.example.businessreportgenerator.domain.model.Asset,
    totalValue: Double,
    color: Color
) {
    val proportion = if (totalValue > 0) (asset.purchasePrice / totalValue) * 100 else 0.0
    val formatter = NumberFormat.getCurrencyInstance(Locale.KOREA)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(color).padding(8.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = asset.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = when(asset.type) {
                    AssetType.REAL_ESTATE -> "부동산"
                    AssetType.STOCK       -> "주식"
                    AssetType.ETF         -> "ETF"
                    AssetType.BOND        -> "채권"
                    AssetType.CRYPTO      -> "코인"
                },
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = formatter.format(asset.purchasePrice), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "${proportion.toInt()}%", fontSize = 14.sp, color = color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamplePortfolioDialog(
    sampleAssets: List<Asset>,
    onDismiss: () -> Unit,
    onLoad: () -> Unit
) {
    // 도넛형 차트용 데이터 생성
    val colors = listOf(
        Color(0xFF007AFF),
        Color(0xFFFF9500),
        Color(0xFF4CD964),
        Color(0xFFFF2D55),
        Color(0xFF5856D6),
        Color(0xFFFFCC00),
        Color(0xFF34C759),
        Color(0xFFAF52DE)
    )
    val pieData = sampleAssets.mapIndexed { idx, asset ->
        PieChartData(
            value = asset.purchasePrice.toFloat(),
            color = colors[idx % colors.size],
            label = asset.name
        )
    }

    // 총합 계산
    val totalSampleValue = sampleAssets.sumOf { it.purchasePrice }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
            ) {
                // — 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "샘플 포트폴리오 예시",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider()

                // — 도넛형 PieChart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)    // 차트 크기 조절
                        .padding(16.dp)
                ) {
                    PieChart(
                        data = pieData,
                        assets = sampleAssets,
                        // 만약 도넛 비율 조절 옵션이 있다면 추가
                    )
                }

                Divider()

                // — 샘플 항목 리스트
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp) // 필요에 따라 조정
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(sampleAssets) { asset ->
                        AssetListItem(
                            asset      = asset,
                            totalValue = totalSampleValue,
                            color      = colors[sampleAssets.indexOf(asset) % colors.size]
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // — 하단 액션 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onLoad) {
                        Text("샘플 적용")
                    }
                }
            }
        }
    }
}
