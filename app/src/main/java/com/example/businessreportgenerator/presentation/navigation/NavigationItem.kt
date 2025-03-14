package com.example.businessreportgenerator.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.businessreportgenerator.presentation.features.analyst.AnalystScreen
import com.example.businessreportgenerator.presentation.features.feed.FeedScreen
import com.example.businessreportgenerator.presentation.features.news.NewsScreen
import com.example.businessreportgenerator.presentation.features.portfolio.PortfolioScreen
import com.google.android.libraries.intelligence.acceleration.Analytics

// 네비게이션 항목
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Portfolio : NavigationItem("portfolio", Icons.Filled.AccountCircle, "포트폴리오")
    object Analyst : NavigationItem("analyst", Icons.Filled.Search, "AI 애널리스트")
    object News : NavigationItem("news", Icons.Filled.Notifications, "주요 뉴스")
    object Feed : NavigationItem("feed", Icons.Filled.Face, "피드")
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(NavigationItem.Portfolio.route) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        // 선택된 탭에 따라 화면 표시
        when (selectedTab) {
            NavigationItem.Portfolio.route -> PortfolioScreen(modifier = Modifier.padding(paddingValues))
            NavigationItem.Analyst.route -> AnalystScreen(modifier = Modifier.padding(paddingValues))
            NavigationItem.News.route -> NewsScreen(modifier = Modifier.padding(paddingValues))
            NavigationItem.Feed.route -> FeedScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        NavigationItem.Portfolio,
        NavigationItem.Analyst,
        NavigationItem.News,
        NavigationItem.Feed
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF007AFF),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = selectedTab == item.route,
                onClick = { onTabSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF007AFF),
                    selectedTextColor = Color(0xFF007AFF),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}