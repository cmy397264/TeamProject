// presentation/features/portfolio/PortfolioScreen.kt
package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.businessreportgenerator.domain.model.Asset
import com.example.businessreportgenerator.domain.model.AssetType
import com.example.businessreportgenerator.presentation.common.PieChart
import com.example.businessreportgenerator.presentation.common.PieChartData
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // 파이 차트 데이터 생성
    val pieChartData = state.assets.map { asset ->
        PieChartData(
            value = asset.purchasePrice.toFloat(),
            color = when (asset.type) {
                AssetType.REAL_ESTATE -> Color(0xFF4285F4)
                AssetType.STOCK -> Color(0xFF34A853)
                AssetType.ETF -> Color(0xFFFBBC05)
                AssetType.BOND -> Color(0xFFEA4335)
                AssetType.CRYPTO -> Color(0xFF9C27B0)
            },
            label = asset.name
        )
    }

    val formatter = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance("KRW")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Portfolio") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 총 포트폴리오 가치
            Text(
                text = "총 자산 가치",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = formatter.format(state.totalPortfolioValue),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 파이 차트
            if (pieChartData.isNotEmpty()) {
                PieChart(
                    data = pieChartData,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "자산을 추가하여 포트폴리오를 구성해보세요",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 종목 추가 버튼
            Button(
                onClick = { viewModel.showAddAssetDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("종목 추가하기")
            }

            // 자산 목록 표시
            if (state.assets.isNotEmpty()) {
                Text(
                    "포트폴리오 구성",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(vertical = 8.dp)
                )

                state.assets.forEach { asset ->
                    AssetListItem(
                        asset = asset,
                        totalValue = state.totalPortfolioValue
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

@Composable
fun AssetListItem(
    asset: Asset,
    totalValue: Double
) {
    val proportion = if (totalValue > 0) (asset.purchasePrice / totalValue) * 100 else 0.0
    val formatter = NumberFormat.getPercentInstance()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        when (asset.type) {
                            AssetType.REAL_ESTATE -> Color(0xFF4285F4)
                            AssetType.STOCK -> Color(0xFF34A853)
                            AssetType.ETF -> Color(0xFFFBBC05)
                            AssetType.BOND -> Color(0xFFEA4335)
                            AssetType.CRYPTO -> Color(0xFF9C27B0)
                        },
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when(asset.type) {
                        AssetType.REAL_ESTATE -> "부동산"
                        AssetType.STOCK -> "주식"
                        AssetType.ETF -> "ETF"
                        AssetType.BOND -> "채권"
                        AssetType.CRYPTO -> "코인"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "${proportion.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}