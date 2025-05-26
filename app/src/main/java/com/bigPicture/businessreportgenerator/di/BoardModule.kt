package com.bigPicture.businessreportgenerator.di

import BoardViewModel
import CommentApiService
import com.bigPicture.businessreportgenerator.data.remote.api.BoardApiService
import com.bigPicture.businessreportgenerator.data.remote.network.RetrofitClient
import com.bigPicture.businessreportgenerator.data.remote.repository.BoardRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val boardModule = module {
    // Retrofit 서비스
    single<BoardApiService> { RetrofitClient.BoardService }
    single<CommentApiService> { RetrofitClient.CommentService }

    // 리포지토리
    single { BoardRepository(get(), get()) }

    // ViewModel
    viewModel { BoardViewModel(get()) }
}
