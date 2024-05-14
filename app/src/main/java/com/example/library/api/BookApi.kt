package com.example.library.api

import com.example.library.model.BookByID
import com.example.library.model.Books
import com.example.library.model.Categories
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {
    @GET("book/get_books.php")
    fun getBook(@Query("limit") limit: Int, @Query("page") page: Int): Call<Books>

    @GET("book/get_all_categories.php")
    fun getAllCategory(): Call<Categories>

    @GET("book/get_books.php")
    fun getBookByID(@Query("id") id: Int): Call<BookByID>

    @GET("book/get_books.php")
    fun getBookByCategory(@Query("limit") limit: Int, @Query("page") page: Int, @Query("category_code") category_code: String): Call<Books>
}