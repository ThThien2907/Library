package com.example.library.api

import com.example.library.model.Users
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("user/get_my_profile.php")
    fun getMyProfile(@Header("Authorization") accessToken: String) : Call<Users>
}