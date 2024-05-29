package com.example.library.utils

import android.app.Activity
import android.content.Context
import com.example.library.api.AuthApi
import com.example.library.api.RetrofitService
import com.example.library.model.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AuthToken {

    fun refreshToken(activity: Activity, onRefreshComplete:(token: Token) -> Unit){
        val token = getToken(activity)

        if (token.refreshToken != null){
            if (isAccessTokenExpired(token.expirationAccessTokenTime)){
                refresh(activity, token.refreshToken!!){
                        newToken -> onRefreshComplete(newToken)
                }
            }
            else {
                onRefreshComplete(token)
            }
        }
    }

    fun refresh(activity: Activity, refreshToken: String, onRefreshComplete:(token: Token) -> Unit){
        val authApi = RetrofitService.getInstance().create(AuthApi::class.java)
        val data = authApi.refreshToken("Bearer $refreshToken")
        data.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        val newToken = Token(
                            result.accessToken,
                            result.refreshToken,
                            result.role,
                            result.expirationAccessTokenTime,
                            result.expirationRefreshTokenTime
                        )
                        storeToken(activity, newToken)
                        onRefreshComplete(newToken)
                    }
                }
                else {
                    Dialog.createDialogLoginSessionExpired(activity)
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
            }
        })
    }

    private fun isAccessTokenExpired(expirationAccessTokenTime: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime >= (expirationAccessTokenTime * 1000) - (60 * 1000)
    }

    fun getToken(activity: Activity): Token {
        val sharedPreferences = activity.getSharedPreferences("token", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("access_token", null)
        val refreshToken = sharedPreferences.getString("refresh_token", null)
        val role = sharedPreferences.getString("role", null)
        val expirationAccessTokenTime = sharedPreferences.getLong("expirationAccessTokenTime", 0)
        val expirationRefreshTokenTime = sharedPreferences.getLong("expirationRefreshTokenTime", 0)

        return Token(accessToken, refreshToken, role, expirationAccessTokenTime, expirationRefreshTokenTime)
    }

    fun storeToken(activity: Activity, token: Token){
        val sharedPreferences = activity.getSharedPreferences("token", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("access_token", token.accessToken)
            putString("refresh_token", token.refreshToken)
            putString("role", token.role)
            putLong("expirationAccessTokenTime", token.expirationAccessTokenTime)
            putLong("expirationRefreshTokenTime", token.expirationRefreshTokenTime)
        }.apply()
    }
}