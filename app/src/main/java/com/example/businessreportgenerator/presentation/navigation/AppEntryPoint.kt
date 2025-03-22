package com.example.businessreportgenerator.presentation.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.businessreportgenerator.presentation.onboarding.OnboardingScreen
import com.example.businessreportgenerator.presentation.onboarding.UserData

/**
 * 앱의 진입점 컴포저블. 사용자가 온보딩을 완료했는지 확인하고
 * 적절한 화면(온보딩 또는 메인 화면)을 표시합니다.
 */
@Composable
fun AppEntryPoint() {
    val context = LocalContext.current
    var isOnboardingCompleted by remember { mutableStateOf(isOnboardingCompleted(context)) }
    var userData by remember { mutableStateOf<UserData?>(null) }

    if (!isOnboardingCompleted) {
        OnboardingScreen(
            onOnboardingComplete = { newUserData ->
                // 사용자 데이터 저장
                saveUserData(context, newUserData)

                // 온보딩 완료 상태 저장
                markOnboardingCompleted(context)

                // 상태 업데이트
                userData = newUserData
                isOnboardingCompleted = true
            }
        )
    } else {
        // 메인 화면 표시
        MainScreen()

        // 사용자 데이터 로드 (필요한 경우)
        LaunchedEffect(Unit) {
            if (userData == null) {
                userData = loadUserData(context)
            }
        }
    }
}

/**
 * 온보딩 완료 상태를 저장합니다.
 */
private fun markOnboardingCompleted(context: Context) {
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
}

/**
 * 사용자가 온보딩을 완료했는지 확인합니다.
 */
private fun isOnboardingCompleted(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("onboarding_completed", false)
}

/**
 * 사용자 데이터를 SharedPreferences에 저장합니다.
 */
private fun saveUserData(context: Context, userData: UserData) {
    val sharedPrefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    with(sharedPrefs.edit()) {
        putString("name", userData.name)
        putInt("age", userData.age)
        putStringSet("interests", userData.interests.toSet())
        putString("risk_tolerance", userData.riskTolerance)
        putString("report_complexity", userData.reportComplexity)
        putString("report_days", userData.reportDays.joinToString(","))
        apply()
    }
}

/**
 * SharedPreferences에서 사용자 데이터를 로드합니다.
 */
private fun loadUserData(context: Context): UserData {
    val sharedPrefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    return UserData(
        name = sharedPrefs.getString("name", "") ?: "",
        age = sharedPrefs.getInt("age", 0),
        interests = sharedPrefs.getStringSet("interests", emptySet())?.toList() ?: emptyList(),
        riskTolerance = sharedPrefs.getString("risk_tolerance", "") ?: "",
        reportComplexity = sharedPrefs.getString("report_complexity", "") ?: "",
        reportDays = sharedPrefs.getString("report_days", "")
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?.map { it.toInt() } ?: emptyList()
    )
}