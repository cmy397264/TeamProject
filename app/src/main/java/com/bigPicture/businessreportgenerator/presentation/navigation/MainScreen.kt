package com.bigPicture.businessreportgenerator.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigPicture.businessreportgenerator.presentation.features.analyst.AnalystScreen
import com.bigPicture.businessreportgenerator.presentation.features.news.NewsScreen
import com.bigPicture.businessreportgenerator.presentation.features.portfolio.PortfolioScreen
import com.example.app.features.board.BoardScreen
import com.example.app.features.board.BoardViewModel

@Composable
fun MainScreen() {
    val navigationViewModel : NavigationViewModel = viewModel()
    val selectedTab by navigationViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { navigationViewModel.navigateTo(it) }
            )
        }
    ) { paddingValues -> // 선택된 탭에 따라 화면 표시
        when (selectedTab) {
            NavigationItem.Portfolio.route -> PortfolioScreen(modifier = Modifier.padding(paddingValues))
            NavigationItem.Analyst.route -> AnalystScreen(modifier = Modifier.padding(paddingValues))
            NavigationItem.News.route -> NewsScreen(modifier = Modifier.padding(paddingValues))
            NavigationItem.Board.route -> {
                // 임시 ViewModel 생성(로컬에서)
                val boardViewModel: BoardViewModel = viewModel()
                val uiState by boardViewModel.uiState.collectAsState()

                BoardScreen(
                    uiState = uiState,
                    onClickItem = { post -> /* TODO: 상세 이동 등 */ },
                    onAddPost = { title, content -> boardViewModel.addPost(title, content) },
                    modifier = Modifier.padding(paddingValues)
                )
            }

        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
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