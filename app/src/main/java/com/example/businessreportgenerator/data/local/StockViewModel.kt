package com.example.businessreportgenerator.data.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.businessreportgenerator.data.local.entity.StockEntity
import com.example.businessreportgenerator.data.local.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockViewModel(val stockRepository: StockRepository) : ViewModel() {
    fun insertStock(stockEntity: StockEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            stockRepository.insertStock(stockEntity)
        }
    }

    fun deleteStock(stockEntity: StockEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            stockRepository.deleteStock(stockEntity)
        }
    }

    fun getAllStocks() = stockRepository.getAllStocks()
}