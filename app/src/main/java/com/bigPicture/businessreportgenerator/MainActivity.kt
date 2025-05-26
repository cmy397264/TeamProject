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
import com.bigPicture.businessreportgenerator.data.remote.dto.ReportRequest
import com.bigPicture.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.bigPicture.businessreportgenerator.presentation.navigation.AppEntryPoint
import com.bigPicture.businessreportgenerator.ui.theme.BusinessReportGeneratorTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
            val stockViewModel : StockViewModel = koinViewModel()
            val analystViewModel: AnalystViewmodel = koinViewModel()

            val apiState by apiViewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                val checkPing = true
                if (checkPing) {
                    Log.d("BigPicture", "app fetch : ping success")
                    Log.d("BigPicture", "app fetch : onboard is {$onboardingCompleted}")
                    if (onboardingCompleted) {
                        val today = LocalDate.now().toString()
                        stockViewModel.getLatestStocksGroupByDate().firstOrNull()?.let {stocks ->
                            val filteredStocks = stocks.filter { stock ->
                                stock.date != today
                            }
                            val deferredList = filteredStocks.map { stock ->
                                    val reportRequest = ReportRequest(
                                        reportType = "stock",
                                        stockName = stock.stockName,
                                        riskTolerance = riskTolerance.toString(),
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
                    apiViewModel.updateApiStatus(ApiStatus.DONE)
                }
            }

            BusinessReportGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    when (apiState.isLoading) {
                        ApiStatus.LOADING -> LoadingScreen()
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

@Composable
fun LoadingScreen(){
    Text("This is LoadingScreen")
}