package com.bigPicture.businessreportgenerator.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * 개발 중 편의를 위한 온보딩 초기화 버튼
 * 출시 전에 제거하거나 개발자 모드에서만 표시하도록 해야 함
 */
@Composable
fun ResetOnboardingButton() {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                resetOnboardingState(context)
                Toast.makeText(context, "온보딩 상태가 초기화되었습니다. 앱을 다시 시작하세요.", Toast.LENGTH_LONG).show()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red.copy(alpha = 0.7f),
                contentColor = Color.White
            )
        ) {
            Text("온보딩 초기화 (개발용)")
        }
    }
}

/**
 * 온보딩 상태를 초기화합니다. (개발용)
 */
fun resetOnboardingState(context: Context) {
    // 온보딩 완료 상태 초기화
    val appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    appPrefs.edit().putBoolean("onboarding_completed", false).apply()

    // 사용자 데이터 초기화
    val userDataPrefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    userDataPrefs.edit().clear().apply()
}