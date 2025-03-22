package com.example.businessreportgenerator.presentation.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnboardingState(
    var currentStep: Int = 0,
    var userData: UserData = UserData(),
)

class OnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    //currentStep을 증가시키는 함수
    fun increaseCurrentStep() {
        _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1)
    }

    //currentStep을 감소시키는 함수
    fun decreaseCurrentStep() {
        _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1)
    }

    fun getCurrentStep() : Int {
        return _state.value.currentStep
    }

    fun setUserDataName(name : String) {
        _state.value = _state.value.copy(userData = _state.value.userData.copy(name = name))
    }

    fun setUserDataAge(age : Int) {
        _state.value = _state.value.copy(userData = _state.value.userData.copy(age = age))
    }

    fun setUserDataInterests(interests : List<String>) {
        _state.value = _state.value.copy(userData = _state.value.userData.copy(interests = interests))
    }

    fun setUserDataRiskTolerance(riskTolerance : String) {
        _state.value = _state.value.copy(userData = _state.value.userData.copy(riskTolerance = riskTolerance))
    }

    fun setUserDataReportComplexity(reportComplexity : String) {
        _state.value = _state.value.copy(userData = _state.value.userData.copy(reportComplexity = reportComplexity))
    }

    fun setUserDataReportDays(reportDays : List<Int>) {
        _state.value = _state.value.copy(userData = _state.value.userData.copy(reportDays = reportDays))
    }

    //currentStep과 UserData를 비교하여 NextButton의 isEnabled를 결정하는 함수
    fun isStepVaild() : Boolean {
        val currentStep = _state.value.currentStep
        val userData = _state.value.userData
        return when (currentStep) {
            0 -> userData.name.isNotBlank()
            1 -> userData.age > 0
            2 -> userData.interests.isNotEmpty()
            3 -> userData.riskTolerance.isNotBlank()
            4 -> userData.reportComplexity.isNotBlank()
            5 -> userData.reportDays.isNotEmpty()
            else -> true
        }
    }
}