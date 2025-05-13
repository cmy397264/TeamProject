package com.bigPicture.businessreportgenerator.presentation.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 간소화된 온보딩 상태
 */
data class OnboardingState(
    val currentStep: Int = 0,
    val userData: UserData = UserData()
)

/**
 * 사용자 데이터 모델
 */
data class UserData(
    val name: String = "",
    val age: Int = 0,
    val riskTolerance: String = "",
    val reportComplexity: String = "",
    val interests: List<String> = emptyList(),
    val reportDays: List<Int> = emptyList()
)

/**
 * 간소화된 온보딩 ViewModel
 */
class OnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    // 다음 단계로 이동
    fun nextStep() {
        _state.update { it.copy(currentStep = it.currentStep + 1) }
    }

    // 이전 단계로 이동
    fun prevStep() {
        _state.update { it.copy(currentStep = it.currentStep - 1) }
    }

    // 기본 정보 설정 (Step 1)
    fun setBasicInfo(name: String, age: Int, riskTolerance: String, reportComplexity: String) {
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    name = name,
                    age = age,
                    riskTolerance = riskTolerance,
                    reportComplexity = reportComplexity
                )
            )
        }
    }

    // 관심 분야 및 수령 요일 설정 (Step 2)
    fun setInterestsAndDays(interests: List<String>, reportDays: List<Int>) {
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    interests = interests,
                    reportDays = reportDays
                )
            )
        }
    }
}