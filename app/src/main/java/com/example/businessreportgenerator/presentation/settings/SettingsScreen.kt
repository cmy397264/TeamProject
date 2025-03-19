package com.example.businessreportgenerator.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 더보기 메뉴 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onInterestsClick: () -> Unit,
    onRiskToleranceClick: () -> Unit,
    onReportComplexityClick: () -> Unit,
    onReportDaysClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "설정",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 사용자 설정 섹션
                Text(
                    text = "사용자 설정",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )

                // 관심 분야 설정
                SettingsItem(
                    icon = Icons.Default.List,
                    title = "관심 분야",
                    subtitle = "레포트에 포함될 분야를 선택합니다",
                    onClick = onInterestsClick
                )

                Divider()

                // 위험 수용 성향 설정
                SettingsItem(
                    icon = Icons.Default.Warning,
                    title = "위험 수용 성향",
                    subtitle = "투자 스타일에 맞는 정보를 제공합니다",
                    onClick = onRiskToleranceClick
                )

                Divider()

                // 레포트 난이도 설정
                SettingsItem(
                    icon = Icons.Default.Edit,
                    title = "레포트 난이도",
                    subtitle = "원하시는 설명의 수준을 선택합니다",
                    onClick = onReportComplexityClick
                )

                Divider()

                // 레포트 수령 요일 설정
                SettingsItem(
                    icon = Icons.Default.DateRange,
                    title = "레포트 수령 요일",
                    subtitle = "레포트를 받고 싶은 요일을 선택합니다",
                    onClick = onReportDaysClick
                )
            }
        }
    }
}

/**
 * 설정 항목 컴포넌트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF007AFF)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.ListItemDefaults.colors(
            containerColor = Color.White
        ),
    )
}