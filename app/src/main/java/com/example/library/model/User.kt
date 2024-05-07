package com.example.library.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id"    ) var id    : String? = null,
    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("email" ) var email : String? = null,
    @SerializedName("role"  ) var role  : String? = null
)

data class Users(
    @SerializedName("data"    ) var data    : User?   = User())