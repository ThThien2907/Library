package com.example.library.api

import com.example.library.model.BorrowReturnBooks
import retrofit2.Call
import retrofit2.http.*

interface BorrowReturnBookApi {
    @GET("borrow_return_books/get_my_borrow_return_books.php")
    fun getMyBorrowReturnBook(@Header("Authorization") authorization: String) : Call<BorrowReturnBooks>

    @FormUrlEncoded
    @POST("borrow_return_books/create_return_book.php")
    fun returnBook(@Header("Authorization") authorization: String, @Field("borrow_id") borrow_id: Int): Call<BorrowReturnBooks>

    @FormUrlEncoded
    @POST("borrow_return_books/create_borrow_book.php")
    fun borrowBook(@Header("Authorization") authorization: String, @Field("book_id") book_id: Int): Call<BorrowReturnBooks>
}