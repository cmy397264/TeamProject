package com.bigPicture.businessreportgenerator.presentation.features.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Exchange(
    var ko: Double = 0.0
)

class NewsViewModel() : ViewModel() {
    private val _Exchange = MutableStateFlow(Exchange())
    val exchange: StateFlow<Exchange> = _Exchange

    fun fetchExchange() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val responseExchange = RetrofitClient.ExchangeService.getExchangeUs()
                if (responseExchange.isSuccessful) {
                    val koExData = responseExchange.body()
                    if (koExData != null) {
                        // 날짜가 최신인 값을 선택
                        val latest = koExData.data.maxByOrNull { it.ExchangeDate }
                        _Exchange.value.ko = latest?.ExchangeRate ?: 0.0
                    }
                } else {
                    Log.e("BigPicture", "불러온 환율 정보가 올바르지 않습니다.")
                }
            } catch (e: Exception) {
                Log.e("BigPicture", "환율 정보 불러오기 실패 : ${e.message}", e)
            }
        }
    }
}