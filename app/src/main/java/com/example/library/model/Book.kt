package com.example.library.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Book (
    @SerializedName("id"             ) var id            : String? = null,
    @SerializedName("title"          ) var title         : String? = null,
    @SerializedName("available"      ) var available     : String? = null,
    @SerializedName("image"          ) var image         : String? = null,
    @SerializedName("description"    ) var description   : String? = null,
    @SerializedName("category_code"  ) var categoryCode  : String? = null,
    @SerializedName("author"         ) var author        : String? = null,
    @SerializedName("category_value" ) var categoryValue : String? = null

): Serializable
data class Books(
    @SerializedName("message"    ) var message: String? = null,
    @SerializedName("data"       ) var data: ArrayList<Book> = arrayListOf(),
    @SerializedName("total_page" ) var totalPage: Int? = null
)

data class BookByID(
    @SerializedName("message"    ) var message: String? = null,
    @SerializedName("data"       ) var data: Book? = Book()
)

data class BorrowReturnBook(
    @SerializedName("id"           ) var id          : String? = null,
    @SerializedName("user_id"      ) var userId      : String? = null,
    @SerializedName("user_name"    ) var userName    : String? = null,
    @SerializedName("book_id"      ) var bookId      : String? = null,
    @SerializedName("book_title"   ) var bookTitle   : String? = null,
    @SerializedName("status"       ) var status      : String? = null,
    @SerializedName("borrowed_day" ) var borrowedDay : String? = null,
    @SerializedName("returned_day" ) var returnedDay : String? = null
)

data class BorrowReturnBooks(
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : ArrayList<BorrowReturnBook> = arrayListOf()
)