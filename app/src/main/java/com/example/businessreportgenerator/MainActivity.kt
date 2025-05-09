package com.example.businessreportgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.businessreportgenerator.data.remote.ApiStatus
import com.example.businessreportgenerator.data.remote.ApiViewModel
import com.example.businessreportgenerator.data.remote.repository.ApiRepository
import com.example.businessreportgenerator.presentation.navigation.AppEntryPoint
import com.example.businessreportgenerator.ui.theme.BusinessReportGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ApiViewModel(apiRepository = ApiRepository)
        setContent {
            BusinessReportGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val state = viewModel.state.collectAsState()
                    when (state.value.isLoading)
                    {
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