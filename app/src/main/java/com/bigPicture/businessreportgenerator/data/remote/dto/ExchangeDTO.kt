package com.bigPicture.businessreportgenerator.data.remote.model

import com.google.gson.annotations.SerializedName

data class ExchangeData(
    @SerializedName("exchangeDate") val ExchangeDate: String,
    @SerializedName("exchangeRate") val ExchangeRate: Double
)


data class ExchangeResponse(
    @SerializedName("code") val code : Int,
    @SerializedName("status") val status : String,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : List<ExchangeData>
)