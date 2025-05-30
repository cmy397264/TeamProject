package com.bigPicture.businessreportgenerator.data.remote.domain

data class PingInfo(
    val status: String,
    val message : String,
    val data : String,
    val code : Int
)