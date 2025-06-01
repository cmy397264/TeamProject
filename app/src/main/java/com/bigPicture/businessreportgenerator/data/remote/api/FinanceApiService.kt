package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.domain.StockHistoryResponse
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.ApiResponse
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.ExchangeItem
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.InterestItem
import com.bigPicture.businessreportgenerator.data.remote.domain.graph.StockItem
import retrofit2.http.GET
import retrofit2.http.Query

// 새로운 데이터 모델들
data class TickerValidationResponse(
    val status: String,
    val message: String,
    val data: Boolean,
    val code: Int
)

data class StockPriceData(
    val stockDate: String,
    val stockPrice: Double
)

data class StockPriceResponse(
    val status: String,
    val message: String,
    val data: StockPriceData,
    val code: Int
)

interface FinanceApiService {
    @GET("/api/v1/exchanges")
    suspend fun getExchanges(): ApiResponse<List<ExchangeItem>>

    @GET("/api/v1/stocks")
    suspend fun getStocks(@Query("stockName") stockName: String): ApiResponse<List<StockItem>>

    @GET("/api/v1/interests/us")
    suspend fun getUsInterests(): ApiResponse<List<InterestItem>>

    @GET("/api/v1/interests/korea")
    suspend fun getKrInterests(): ApiResponse<List<InterestItem>>

    // 티커 체크 및 가격 불러오는 API 엔드포인트
    @GET("/api/v1/stocks/check-ticker")
    suspend fun checkTicker(@Query("ticker") ticker: String): TickerValidationResponse

    @GET("/api/v1/stocks/today")
    suspend fun getTodayStockPrice(@Query("stockName") stockName: String): StockPriceResponse

    @GET("/api/v1/stocks/all")
    suspend fun getStockHistory(
        @Query("stockName") stockName: String
    ): StockHistoryResponse

}
