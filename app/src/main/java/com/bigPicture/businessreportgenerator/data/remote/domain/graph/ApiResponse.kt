package com.bigPicture.businessreportgenerator.data.remote.domain.graph

data class ApiResponse<T>(val code: Int, val message: String, val data: T)
