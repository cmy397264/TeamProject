package com.bigPicture.businessreportgenerator.data.remote.network

import CommentApiService
import com.bigPicture.businessreportgenerator.data.remote.api.BoardApiService
import com.bigPicture.businessreportgenerator.data.remote.api.FcmApiService
import com.bigPicture.businessreportgenerator.data.remote.api.ExchangeApiService
import com.bigPicture.businessreportgenerator.data.remote.api.FinanceApiService
import com.bigPicture.businessreportgenerator.data.remote.api.ReportApiService
import com.bigPicture.businessreportgenerator.data.remote.api.PingApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://dockerel.o-r.kr/api/v1/"

    val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ReportService : ReportApiService by lazy{
        retrofit.create(ReportApiService::class.java)
    }

    val PingService : PingApiService by lazy{
        retrofit.create(PingApiService::class.java)
    }

    val BoardService: BoardApiService by lazy {
        retrofit.create(BoardApiService::class.java)
    }

    val CommentService: CommentApiService by lazy {
        retrofit.create(CommentApiService::class.java)
    }

    val ExchangeService: ExchangeApiService by lazy {
        retrofit.create(ExchangeApiService::class.java)
    }

    val FinanceService: FinanceApiService by lazy {
        retrofit.create(FinanceApiService::class.java)
    }

    val FcmService: FcmApiService by lazy {
        retrofit.create(FcmApiService::class.java)
    }
}