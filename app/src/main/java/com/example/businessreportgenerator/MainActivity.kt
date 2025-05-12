package com.example.businessreportgenerator

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
import com.example.businessreportgenerator.data.local.StockViewModel
import com.example.businessreportgenerator.data.remote.ApiStatus
import com.example.businessreportgenerator.data.remote.ApiViewModel
import com.example.businessreportgenerator.data.remote.model.ReportRequest
import com.example.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import com.example.businessreportgenerator.presentation.navigation.AppEntryPoint
import com.example.businessreportgenerator.ui.theme.BusinessReportGeneratorTheme
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.koinViewModel

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
                if (apiViewModel.sendPing()) {
                    Log.d("BigPicture", "app fetch : ping success")
                    Log.d("BigPicture", "app fetch : onboard is {$onboardingCompleted}")
                    if (onboardingCompleted) {
                        stockViewModel.getAllStocks().firstOrNull()?.let {
                            it.forEach { stock ->
                                val reportRequest = ReportRequest(
                                    reportType = "stock",
                                    stockName = stock.stockName,
                                    riskTolerance = riskTolerance.toString(),
                                    reportDifficultyLevel = reportComplexity,
                                    interestAreas = interests ?: emptyList()
                                )
                                analystViewModel.requestReport(reportRequest)
                            }
                        }
                    }
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