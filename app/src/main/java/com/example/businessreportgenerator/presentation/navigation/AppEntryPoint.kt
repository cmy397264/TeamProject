package com.example.businessreportgenerator.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.businessreportgenerator.presentation.onboarding.OnboardingScreen
import com.example.businessreportgenerator.presentation.onboarding.OnboardingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 앱 진입점 화면 - 온보딩 또는 메인 화면 표시
 */
@Composable
fun AppEntryPoint() {
    val context = LocalContext.current

    // 온보딩 완료 여부 확인
    val isOnboardingCompleted = remember {
        context.getSharedPreferences("user_prefs", 0)
            .getBoolean("onboarding_completed", false)
    }

    // 현재 화면 상태
    var showMainScreen by remember { mutableStateOf(isOnboardingCompleted) }

    if (!showMainScreen) {
        // 온보딩 화면 표시
        OnboardingScreen(
            onOnboardingComplete = {
                // 온보딩 완료 시 메인 화면으로 전환
                showMainScreen = true
            }
        )
    } else {
        // 메인 화면 표시
        MainScreen()
    }
}