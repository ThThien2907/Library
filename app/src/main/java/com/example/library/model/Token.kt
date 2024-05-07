package com.example.library.model

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("access_token"  ) var accessToken  : String? = null,
    @SerializedName("refresh_token" ) var refreshToken : String? = null,
    @SerializedName("role"          ) var role         : String? = null
)