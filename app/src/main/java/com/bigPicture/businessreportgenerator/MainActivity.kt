package com.bigPicture.businessreportgenerator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigPicture.businessreportgenerator.data.local.StockViewModel
import com.bigPicture.businessreportgenerator.data.remote.ApiStatus
import com.bigPicture.businessreportgenerator.data.remote.ApiViewModel
import com.bigPicture.businessreportgenerator.data.remote.model.ReportRequest
import com.bigPicture.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.bigPicture.businessreportgenerator.presentation.features.news.NewsViewModel
import com.bigPicture.businessreportgenerator.presentation.navigation.AppEntryPoint
import com.bigPicture.businessreportgenerator.presentation.navigation.LoadingScreen
import com.bigPicture.businessreportgenerator.ui.theme.BusinessReportGeneratorTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("user_prefs", 0)
        val onboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)
        val riskTolerance = sharedPrefs.getString("risk_tolerance", null).toString()
        val reportComplexity = sharedPrefs.getString("report_complexity", null).toString()
        val interests = sharedPrefs.getStringSet("interests", null)?.toList()

        setContent {
            val apiViewModel: ApiViewModel = viewModel()
            val newsViewModel : NewsViewModel = viewModel()
            val stockViewModel : StockViewModel = koinViewModel()
            val analystViewModel: AnalystViewmodel = koinViewModel()

            val apiState by apiViewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                apiViewModel.updateApiMessage("서버와의 연결을 확인하는 중...")
                if (apiViewModel.sendPing()) {
                    Log.d("BigPicture", "app fetch : ping success")
                    apiViewModel.updateApiMessage("사용자 정보를 확인하는 중...")
                    Log.d("BigPicture", "app fetch : onboard is {$onboardingCompleted}")
                    if (onboardingCompleted) {
                        apiViewModel.updateApiMessage("레포트 정보를 갱신하는 중...")
                        val today = LocalDate.now().toString()
                        stockViewModel.getLatestStocksGroupByDate().firstOrNull()?.let {stocks ->
                            val filteredStocks = stocks.filter { stock ->
                                stock.date != today
                            }
                            val deferredList = filteredStocks.map { stock ->
                                    val reportRequest = ReportRequest(
                                        reportType = "stock",
                                        stockName = stock.stockName,
                                        riskTolerance = riskTolerance,
                                        reportDifficultyLevel = reportComplexity,
                                        interestAreas = interests ?: emptyList()
                                    )
                                    async {
                                        analystViewModel.requestReport(reportRequest)
                                        stockViewModel.updateStockDate(stock.stockName, today)
                                    }
                            }
                            deferredList.awaitAll()
                        }
                    }
                    Log.d("BigPicture", "app fetch : report request success")
                    apiViewModel.updateApiMessage("환율 정보를 확인하는 중...")
                    newsViewModel.fetchInterests()
                    apiViewModel.updateApiMessage("로딩 완료!")
                    delay(1000)
                    apiViewModel.updateApiStatus(ApiStatus.DONE)
                } else {
                    apiViewModel.updateApiStatus(ApiStatus.ERROR)
                }
            }

            BusinessReportGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    when (apiState.isLoading) {
                        ApiStatus.LOADING -> LoadingScreen(
                            message = apiState.message
                        )
                        ApiStatus.ERROR -> ErrorScreen()
                        ApiStatus.DONE -> AppEntryPoint()
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(){
    Text("This is ErrorScreen")
}