package com.bigPicture.businessreportgenerator.presentation.features.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Interests(
    var ko: Double = 0.0,
    var us: Double = 0.0
)

class NewsViewModel() : ViewModel() {
    private val _interests = MutableStateFlow(Interests())
    val interests: StateFlow<Interests> = _interests

    fun fetchInterests() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val responseKo = RetrofitClient.InterestService.getInterestKo()
                val responseUs = RetrofitClient.InterestService.getInterestUs()

                if (responseKo.isSuccessful && responseUs.isSuccessful) {
                    val koData = responseKo.body()
                    val usData = responseUs.body()
                    if (koData != null)
                        _interests.value.ko = koData.data.firstOrNull()?.interestRate ?: 0.0
                    if (usData != null)
                        _interests.value.us = usData.data.firstOrNull()?.interestRate ?: 0.0
                } else {
                    Log.e("BigPicture", "불러온 환율 정보가 올바르지 않습니다.")
                }

            } catch (e: Exception) {
                Log.e("BigPicture", "환율 정보 불러오기 실패 : ${e.message}", e)
            }
        }
    }

}