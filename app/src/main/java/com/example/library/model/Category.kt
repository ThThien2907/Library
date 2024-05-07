package com.example.library.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("code"  ) var code  : String? = null,
    @SerializedName("value" ) var value : String? = null
    )
data class Categories(
    @SerializedName("categories" ) var categories : ArrayList<Category> = arrayListOf()
)
