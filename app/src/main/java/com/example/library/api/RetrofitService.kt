package com.example.library.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    val gson: Gson
        get() = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

    //http://localhost/CT06/do_an/api/routes/book/get_books.php?limit=2&page=1

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.188.3/CT06/do_an/api/routes/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}