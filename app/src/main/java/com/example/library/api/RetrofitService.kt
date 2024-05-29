package com.example.library.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val gson: Gson
        get() = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
                //truyền vô địa chỉ ip của máy chủ
            .baseUrl("http://10.0.68.231/CT06/do_an/api/routes/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}