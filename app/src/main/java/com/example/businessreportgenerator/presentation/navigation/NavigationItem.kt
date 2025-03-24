package com.example.businessreportgenerator.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

// 네비게이션 항목
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Portfolio : NavigationItem("portfolio", Icons.Filled.AccountCircle, "포트폴리오")
    object Analyst : NavigationItem("analyst", Icons.Filled.Search, "AI 애널리스트")
    object News : NavigationItem("news", Icons.Filled.Notifications, "주요 뉴스")
    object Feed : NavigationItem("feed", Icons.Filled.Face, "피드")
}

val items = listOf(
    NavigationItem.Portfolio,
    NavigationItem.Analyst,
    NavigationItem.News,
    NavigationItem.Feed
)
