package com.bigPicture.businessreportgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bigPicture.businessreportgenerator.presentation.navigation.AppEntryPoint
import com.bigPicture.businessreportgenerator.ui.theme.BusinessReportGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BusinessReportGeneratorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    AppEntryPoint()
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(){
    Text("This is ErrorScreen")
}