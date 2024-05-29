package com.example.library.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

object Utils {
    @Suppress("DEPRECATION")
    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun isLogin(activity: Activity):Boolean{
        val token = AuthToken.getToken(activity)
        return token.refreshToken != null
    }
}