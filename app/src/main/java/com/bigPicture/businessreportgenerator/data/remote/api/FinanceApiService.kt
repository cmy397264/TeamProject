package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.domain.graph.ApiResponse
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.ExchangeItem
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.InterestItem
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.StockItem
import retrofit2.http.GET
import retrofit2.http.Query

interface FinanceApiService {
    @GET("/api/v1/exchanges")
    suspend fun getExchanges(): ApiResponse<List<ExchangeItem>>

    @GET("/api/v1/stocks")
    suspend fun getStocks(@Query("stockName") stockName: String): ApiResponse<List<StockItem>>

    @GET("/api/v1/interests/us")
    suspend fun getUsInterests(): ApiResponse<List<InterestItem>>

    @GET("/api/v1/interests/korea")
    suspend fun getKrInterests(): ApiResponse<List<InterestItem>>
}
