package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// import com.example.businessreportgenerator.R
import com.example.businessreportgenerator.domain.model.AssetType
import com.example.businessreportgenerator.presentation.common.AppTopBar
import com.example.businessreportgenerator.presentation.common.PieChart
import com.example.businessreportgenerator.presentation.common.PieChartData
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // 애플 스타일의 프리미엄 색상 팔레트
    val colors = listOf(
        Color(0xFF007AFF), // 애플 블루
        Color(0xFFFF9500), // 애플 오렌지
        Color(0xFF4CD964), // 애플 그린
        Color(0xFFFF2D55), // 애플 핑크
        Color(0xFF5856D6), // 애플 퍼플
        Color(0xFFFFCC00), // 애플 옐로우
        Color(0xFF34C759), // 라이트 그린
        Color(0xFFAF52DE)  // 라이트 퍼플
    )

    // 파이 차트 데이터 생성
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
        color = Color(0xFFF8F9FA) // 애플 스타일 배경색 (밝은 회색)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            AppTopBar(title = "My Portfolio")

            // 메인 콘텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 16.dp) // 하단 여백 추가
            ) {
                // 애니메이션 효과로 컨텐츠 로딩
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
                ) {
                    if (state.assets.isEmpty()) {
                        EmptyPortfolioContent(onClick = { viewModel.showAddAssetDialog() })
                    } else {
                        PopulatedPortfolioContent(
                            totalValue = state.totalPortfolioValue,
                            formatter = formatter,
                            pieChartData = pieChartData,
                            assets = state.assets,
                            onAddClick = { viewModel.showAddAssetDialog() }
                        )
                    }
                }
            }

            // 자산 추가 대화상자
            if (state.isAddAssetDialogVisible) {
                AddAssetDialog(
                    onDismiss = { viewModel.hideAddAssetDialog() },
                    onAddAsset = { viewModel.addAsset(it) }
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
        // 요약 카드 (그라데이션 배경)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2C7AFA),
                                Color(0xFF1A54D5)
                            )
                        ),
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatter.format(totalValue),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // 성장률 표시 (예시)
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
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "3.2%",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 퀵액션 버튼들
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(
                            icon = Icons.Rounded.Person,
                            label = "자산 분석",
                            onClick = {}
                        )

                        QuickActionButton(
                            icon = Icons.Rounded.ThumbUp,
                            label = "퍼포먼스",
                            onClick = {}
                        )

                        QuickActionButton(
                            icon = Icons.Filled.Add,
                            label = "종목 추가",
                            onClick = onAddClick
                        )
                    }
                }
            }
        }

        // 자산 구성 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "자산 구성",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 더보기 버튼 (옵션)
                    TextButton(onClick = {}) {
                        Text(
                            text = "상세보기",
                            fontSize = 14.sp,
                            color = Color(0xFF007AFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 파이 차트
                PieChart(
                    data = pieChartData,
                    assets = assets
                )
            }
        }

        // 종목 목록 카드
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "투자 종목",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 각 자산 항목 표시
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

                Spacer(modifier = Modifier.height(16.dp))

                // 종목 추가 버튼
                OutlinedButton(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.5.dp,
                        brush = SolidColor(Color(0xFF007AFF))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF007AFF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "추가",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "종목 추가하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp
            )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 색상 원
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 종목 정보
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = asset.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = when(asset.type) {
                    AssetType.REAL_ESTATE -> "부동산"
                    AssetType.STOCK -> "주식"
                    AssetType.ETF -> "ETF"
                    AssetType.BOND -> "채권"
                    AssetType.CRYPTO -> "코인"
                },
                fontSize = 14.sp,
                color = Color.Gray
            )
            // AssetListItem 완성 부분 (계속)
        }

        // 금액 및 비율
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatter.format(asset.purchasePrice),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${proportion.toInt()}%",
                fontSize = 14.sp,
                color = color
            )
        }
    }
}

@Composable
fun EmptyPortfolioContent(onClick: () -> Unit) {
    val viewModel: PortfolioViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 모던한 투자 팁 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "오늘의 투자 팁",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // 일일 업데이트 배지
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE1F5FE))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Daily",
                            color = Color(0xFF0288D1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 팁 카드들
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
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 380.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 예시 이미지
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F6FF))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "포트폴리오",
                        tint = Color(0xFF007AFF),
                        modifier = Modifier.size(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "포트폴리오 관리를 시작하세요",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "종목을 추가하여 자산을 효율적으로 관리하고 투자 성과를 분석해보세요.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 종목 추가 버튼
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(54.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(27.dp),
                            spotColor = Color(0xFF007AFF).copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "추가",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "종목 추가하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 샘플 포트폴리오 버튼
                OutlinedButton(
                    onClick = { viewModel.loadSamplePortfolio() },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.5.dp,
                        brush = SolidColor(Color(0xFF007AFF))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF007AFF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "샘플",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "샘플 포트폴리오 보기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// 투자 팁 데이터 클래스
data class InvestmentTip(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val backgroundColor: Color
)

// 투자 팁 목록
val investmentTips = listOf(
    InvestmentTip(
        icon = Icons.Rounded.Search,
        title = "분산 투자",
        description = "다양한 자산 클래스에 투자하여 리스크를 분산시키세요.",
        backgroundColor = Color(0xFFE8F5E9)
    ),
    InvestmentTip(
        icon = Icons.Rounded.DateRange,
        title = "장기 투자",
        description = "단기 변동성에 흔들리지 말고 장기적 관점으로 투자하세요.",
        backgroundColor = Color(0xFFE3F2FD)
    ),
    InvestmentTip(
        icon = Icons.Rounded.Refresh,
        title = "정기적 리밸런싱",
        description = "포트폴리오를 정기적으로 조정하여 목표 배분을 유지하세요.",
        backgroundColor = Color(0xFFFFF3E0)
    )
)

@Composable
fun InvestmentTipCard(tip: InvestmentTip) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = tip.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 아이콘
            Icon(
                imageVector = tip.icon,
                contentDescription = tip.title,
                tint = Color(0xFF006064),
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 제목
            Text(
                text = tip.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 설명
            Text(
                text = tip.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}