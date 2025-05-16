package com.bigPicture.businessreportgenerator.data.remote

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?
)