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
    private lateinit var db: AuthDBHelper
//    private lateinit var dialogBox: Utils.DialogBox
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        AuthToken.storeToken(this, Token())
        val token = AuthToken.getToken(this)
        Log.e("spl", token.toString())

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
//                    val authApi = RetrofitService.getInstance().create(AuthApi::class.java)
//                    val data = authApi.refreshToken("Bearer ${token.refreshToken}")
//                    data.enqueue(object : Callback<Token> {
//                        override fun onResponse(call: Call<Token>, response: Response<Token>) {
//                            if (response.isSuccessful) {
//                                val result = response.body()
//                                if (result != null) {
//                                    val newToken = Token(
//                                        result.accessToken,
//                                        result.refreshToken,
//                                        result.role,
//                                        result.expirationAccessTokenTime,
//                                        result.expirationRefreshTokenTime
//                                    )
//                                    AuthToken.storeToken(this@SplashActivity, newToken)
//
////                                    sharedPreferences.edit().apply {
////                                        putString("access_token", result.accessToken)
////                                        putString("refresh_token", result.refreshToken)
////                                        putString("role", result.role)
////                                        putLong("expirationAccessTokenTime", result.expirationAccessTokenTime!!)
////                                        putLong("expirationRefreshTokenTime", result.expirationRefreshTokenTime!!)
////                                    }.apply()
////                                    val rs = db.updateToken(newToken, token.refreshToken!!)
////                                    if (rs > 0) {
//                                        val intent =
//                                            Intent(this@SplashActivity, MainActivity::class.java)
//                                        startActivity(intent)
//                                        finish()
////                                    }
//                                }
//                            }
//                            else {
//                                val errorBody = response.errorBody()?.string()
//                                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
////
////                                if (errorResponse.errors[0] == "refresh_token này không tồn tại trong db (bạn chưa đăng nhập)"){
////                                    dialogBox.tvTitle.text = "Thông báo"
////                                    dialogBox.tvContent.text = "Hết phiên đăng nhập. Vui lòng đăng nhập lại."
////
////                                    dialogBox.btnAccept.setOnClickListener {
////                                        dialogBox.dialog.dismiss()
//////                                        val rs = db.deleteAll()
//////                                        if (rs > 0){
////                                            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
////                                            startActivity(intent)
////                                            finish()
//////                                        }
////                                    }
////                                    dialogBox.dialog.show()
////                                }
//                            }
//                        }
//
//                        override fun onFailure(call: Call<Token>, t: Throwable) {
//                        }
//                    })
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }
    }
}