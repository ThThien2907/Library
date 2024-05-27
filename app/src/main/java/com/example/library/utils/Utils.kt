package com.example.library.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.library.R
import com.example.library.activity.LoginActivity
import com.example.library.api.AuthApi
import com.example.library.api.RetrofitService
import com.example.library.model.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Utils {
    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}