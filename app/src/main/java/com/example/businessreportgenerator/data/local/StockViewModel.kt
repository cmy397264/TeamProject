package com.example.businessreportgenerator.data.local

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.businessreportgenerator.data.local.entity.StockEntity
import com.example.businessreportgenerator.data.local.repository.StockRepository
import com.example.businessreportgenerator.data.remote.model.StockRequest
import com.example.businessreportgenerator.data.remote.model.StockResponse
import com.example.businessreportgenerator.data.remote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockViewModel(val stockRepository: StockRepository) : ViewModel() {

    fun registerStock(stockType : String, stockName : String) {
        val call = RetrofitClient.ReportService.registerStock(
            StockRequest(
                stockType = stockType,
                stockName = stockName
            )
        )
        Log.d("stockRequest", "${stockType}, ${stockName}")
        call.enqueue(object : Callback<StockResponse> {
            override fun onResponse(
                call: Call<StockResponse>,
                response: Response<StockResponse>
            ) {
                Log.d("BigPicture", "${response.body()}")
                val stockResponse = response.body()
                if (stockResponse != null) {
                    insertStock(stockResponse.toDomain())
                    Log.d("BigPicture", "success")
                } else
                    Log.d("BigPicture", "fail")
            }

            override fun onFailure(call: Call<StockResponse>, t: Throwable) {
                Log.d("BigPicture", "network error")
            }
        })
    }

    fun insertStock(stockEntity: StockEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            stockRepository.insertStock(stockEntity)
        }
    }

//    fun getAllStocks() = stockRepository.getAllStocks()

    fun getLatestStocksGroupByDate() = stockRepository.getLatestStocksGroupedByName()

    fun updateStockDate(stockName: String, newDate: String) =
        viewModelScope.launch(Dispatchers.IO) {
            stockRepository.updateStockDate(stockName, newDate)
        }
}