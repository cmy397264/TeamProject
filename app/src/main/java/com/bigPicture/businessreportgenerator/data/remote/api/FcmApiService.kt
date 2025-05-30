package com.bigPicture.businessreportgenerator.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class FCMToken(
    @SerializedName("uuid") val uuid : String,
    @SerializedName("fcmToken") val fcmToken : String
)

interface FcmApiService {
    @POST("alert/register-fcm-token")
    fun registerFcmToken(@Body token : FCMToken) : Call<FCMToken>
}