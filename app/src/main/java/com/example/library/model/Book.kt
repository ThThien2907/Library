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
