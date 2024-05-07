package com.example.library.api

import com.example.library.model.Token
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @FormUrlEncoded
    @POST("auth/login.php")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<Token>

    @FormUrlEncoded
    @POST("auth/logout.php")
    fun logout(@Header("Authorization") authorization: String, @Field("refresh_token") refresh_token: String): Call<Token>

    @FormUrlEncoded
    @POST("auth/register.php")
    fun register(@Field("name") name: String,
                 @Field("email") email: String,
                 @Field("password") password: String,
                 @Field("confirm_password") confirm_password: String): Call<Token>

    @FormUrlEncoded
    @POST("auth/refresh_token.php")
    fun refreshToken(@Field("refresh_token") refresh_token: String): Call<Token>
}