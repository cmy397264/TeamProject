package com.bigPicture.businessreportgenerator.data.remote.network

import CommentApiService
import com.bigPicture.businessreportgenerator.data.remote.api.BoardApiService
import com.bigPicture.businessreportgenerator.data.remote.api.InterestApiService
import com.bigPicture.businessreportgenerator.data.remote.api.ReportApiService
import com.bigPicture.businessreportgenerator.data.remote.api.ScheduleApiService
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

    val ScheduleService : ScheduleApiService by lazy{
        retrofit.create(ScheduleApiService::class.java)
    }

    val BoardService: BoardApiService by lazy {
        retrofit.create(BoardApiService::class.java)
    }

    val CommentService: CommentApiService by lazy {
        retrofit.create(CommentApiService::class.java)
    }

    val InterestService: InterestApiService by lazy {
        retrofit.create(InterestApiService::class.java)
    }
}