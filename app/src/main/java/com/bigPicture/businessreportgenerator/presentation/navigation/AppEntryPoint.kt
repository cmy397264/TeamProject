package com.bigPicture.businessreportgenerator.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigPicture.businessreportgenerator.ErrorScreen
import com.bigPicture.businessreportgenerator.data.local.StockViewModel
import com.bigPicture.businessreportgenerator.data.remote.ApiStatus
import com.bigPicture.businessreportgenerator.data.remote.ApiViewModel
import com.bigPicture.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.bigPicture.businessreportgenerator.presentation.features.news.NewsViewModel
import com.bigPicture.businessreportgenerator.presentation.onboarding.OnboardingScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import androidx.core.content.edit
import com.bigPicture.businessreportgenerator.data.remote.dto.ReportRequest
import kotlinx.coroutines.delay

/**
 * 앱 진입점 화면 - 온보딩 또는 메인 화면 표시
 */
@Composable
fun AppEntryPoint() {
    val context = LocalContext.current
    val apiViewModel: ApiViewModel = viewModel()
    val newsViewModel: NewsViewModel = viewModel()
    val stockViewModel: StockViewModel = koinViewModel()
    val analystViewModel: AnalystViewmodel = koinViewModel()
    val apiState by apiViewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var onboardingCompleted by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("user_prefs", 0)
        onboardingCompleted = prefs.getBoolean("onboarding_completed", false)

        apiViewModel.updateApiMessage("서버와의 연결을 확인하는 중...")
        // val connected = apiViewModel.sendPing()
        val connected = true // 테스트용
        if (connected) {
            apiViewModel.updateApiMessage("사용자 정보를 확인하는 중...")
            if (onboardingCompleted == true) {
                val today = LocalDate.now().toString()
                val riskTolerance = prefs.getString("risk_tolerance", null).toString()
                val reportComplexity = prefs.getString("report_complexity", null).toString()
                val interests = prefs.getStringSet("interests", null)?.toList() ?: emptyList()

                apiViewModel.updateApiMessage("레포트 정보를 갱신하는 중...")
                stockViewModel.getLatestStocksGroupByDate().firstOrNull()?.let { stocks ->
                    val filtered = stocks.filter { it.date != today }
                    val deferred = filtered.map { stock ->
                        val request = ReportRequest(
                            reportType = "stock",
                            stockName = stock.stockName,
                            riskTolerance = riskTolerance,
                            reportDifficultyLevel = reportComplexity,
                            interestAreas = interests
                        )
                        coroutineScope.async {
                            analystViewModel.requestReport(request)
                            stockViewModel.updateStockDate(stock.stockName, today)
                        }
                    }
                    deferred.awaitAll()
                }
            }
            apiViewModel.updateApiMessage("환율 불러오는 중...")
            newsViewModel.fetchInterests()
            apiViewModel.updateApiMessage("로딩 완료")
            delay(500)
            apiViewModel.updateApiStatus(ApiStatus.DONE)
        } else {
            apiViewModel.updateApiStatus(ApiStatus.ERROR)
        }
    }

    when (apiState.isLoading) {
        ApiStatus.LOADING -> AnimatedVisibility(visible = true) {
            LoadingScreen(message = apiState.message)
        }
        ApiStatus.ERROR -> ErrorScreen()
        ApiStatus.DONE -> {
            if (onboardingCompleted == false) {
                OnboardingScreen(
                    onComplete = { reportRequest ->
                        coroutineScope.launch {
                            analystViewModel.requestReport(reportRequest)
                            context.getSharedPreferences("user_prefs", 0)
                                .edit() {
                                    putBoolean("onboarding_completed", true)
                                }
                            onboardingCompleted = true
                        }
                    }
                )
            } else {
                MainScreen()
            }
        }
    }
}
