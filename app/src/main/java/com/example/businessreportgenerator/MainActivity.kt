package com.example.businessreportgenerator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.businessreportgenerator.data.remote.Schedule
import com.example.businessreportgenerator.data.remote.RetrofitClient
import com.example.businessreportgenerator.presentation.navigation.AppEntryPoint
import com.example.businessreportgenerator.ui.theme.BusinessReportGeneratorTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 서버로부터 정보를 읽어와 Log를 사용하여 연결상태를 확인함
        RetrofitClient.apiService.getSchedule().enqueue(object : Callback<Schedule> {
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful) {
                    val state = response.body()
                    Log.d("API State", "Status: ${state?.status}")
                } else {
                    Log.e("API Error", "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<Schedule?>,
                t: Throwable
            ) {
                Log.e("API Error", "Network request failed: ${t.message}")
            }
        })

        setContent {
            BusinessReportGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // 온보딩을 처리하는 진입점
                    AppEntryPoint()
                }
            }
        }
    }
}