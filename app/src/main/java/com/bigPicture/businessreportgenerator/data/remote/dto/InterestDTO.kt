package com.bigPicture.businessreportgenerator.data.remote.model

import com.google.gson.annotations.SerializedName

data class InterestData(
    val interestDate : String,
    val interestRate : Double
)

data class InterestResponse(
    @SerializedName("code") val code : Int,
    @SerializedName("status") val status : String,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : List<InterestData>
)