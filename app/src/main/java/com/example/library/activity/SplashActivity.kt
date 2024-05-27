package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.example.library.R
import com.example.library.api.AuthApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.model.Token
import com.example.library.utils.AuthDBHelper
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen, SetTextI18n")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val token = AuthToken.getToken(this)

        if (!Utils.isOnline(this)){
            Dialog.createDialog(this){
                dialog, tvTitle, tvContent, btnAccept, _ ->
                tvTitle.text = "Lỗi kết nối mạng!"
                tvContent.text = "Thiết bị của bạn chưa kết nối mạng. Vui lòng kiểm tra lại kết nối."
                dialog.setCancelable(false)
                btnAccept.setOnClickListener {
                    dialog.dismiss()
                    finish()
                }

                dialog.show()
            }
        }
        else {
            Handler(Looper.getMainLooper()).postDelayed( {
                if (token.refreshToken != null) {
                    AuthToken.refresh(this, token.refreshToken!!){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }
    }
}