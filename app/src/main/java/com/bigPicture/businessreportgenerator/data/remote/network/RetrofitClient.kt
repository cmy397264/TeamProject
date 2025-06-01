package com.bigPicture.businessreportgenerator.data.remote.network

import CommentApiService
import com.bigPicture.businessreportgenerator.data.remote.api.BoardApiService
import com.bigPicture.businessreportgenerator.data.remote.api.ExchangeApiService
import com.bigPicture.businessreportgenerator.data.remote.api.FcmApiService
import com.bigPicture.businessreportgenerator.data.remote.api.FinanceApiService
import com.bigPicture.businessreportgenerator.data.remote.api.PingApiService
import com.bigPicture.businessreportgenerator.data.remote.api.ReportApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://dockerel.o-r.kr/api/v1/"

    /* timeout 설정을 위한 okHttpClient
    connectTimeout = 서버 연결을 시도할 때까지 기다리는 시간,
    readTimeout = 서버에 요청을 보낸 후 응답 데이터를 기다리는 시간
    writeTimeout = 서버로 요청 데이터를 전송하는 시간
     */
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
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