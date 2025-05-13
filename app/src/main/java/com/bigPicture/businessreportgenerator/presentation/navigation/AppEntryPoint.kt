package com.bigPicture.businessreportgenerator.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.bigPicture.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.bigPicture.businessreportgenerator.presentation.onboarding.OnboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 앱 진입점 화면 - 온보딩 또는 메인 화면 표시
 */
@Composable
fun AppEntryPoint() {
    val analystViewmodel : AnalystViewmodel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
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
            onComplete = { ReportRequest ->
                // 온보딩 완료 시 메인 화면으로 전환
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        analystViewmodel.requestReport(ReportRequest)
                        showMainScreen = true
                    } catch (e : Exception) {
                        Log.e("BigPicture", "error : $e")
                    }
                }
            }
        )
    } else {
        // 메인 화면 표시
        MainScreen()
    }
}