package com.example.library.api

import com.example.library.model.BookList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {
    @GET("book/get_books.php")
    fun getBook(@Query("limit") limit: Int, @Query("page") page: Int): Call<BookList>
}