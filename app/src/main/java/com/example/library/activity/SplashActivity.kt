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
import com.example.library.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen, SetTextI18n")
class SplashActivity : AppCompatActivity() {
    private lateinit var db: AuthDBHelper
    private lateinit var dialogBox: Utils.DialogBox
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        db = AuthDBHelper(this)
        val token = db.getToken()

        dialogBox = Utils.DialogBox(this)
        dialogBox.createDialog()
        dialogBox.dialog.setCancelable(false)

        if (!Utils.isOnline(this)){
            dialogBox.tvTitle.text = "Lỗi kết nối mạng!"
            dialogBox.tvContent.text = "Thiết bị của bạn chưa kết nối mạng. Vui lòng kiểm tra lại kết nối."

            dialogBox.btnAccept.setOnClickListener {
                dialogBox.dialog.dismiss()
                finish()
            }
            dialogBox.dialog.show()

        } else {
            Handler(Looper.getMainLooper()).postDelayed( {
                if (token.refreshToken != null) {
                    val authApi = RetrofitService.getInstance().create(AuthApi::class.java)
                    val data = authApi.refreshToken("Bearer " + token.refreshToken)
                    data.enqueue(object : Callback<Token> {
                        override fun onResponse(call: Call<Token>, response: Response<Token>) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result != null) {
                                    val newToken =
                                        Token(result.accessToken, result.refreshToken, result.role)

                                    val rs = db.updateToken(newToken, token.refreshToken!!)
                                    if (rs > 0) {
                                        val intent =
                                            Intent(this@SplashActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                            else {
                                val errorBody = response.errorBody()?.string()
                                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

                                if (errorResponse.errors[0] == "refresh_token này không tồn tại trong db (bạn chưa đăng nhập)"){
                                    dialogBox.tvTitle.text = "Thông báo"
                                    dialogBox.tvContent.text = "Tài khoản của bạn đã hết phiên đăng nhập. Vui lòng đăng nhập lại."

                                    dialogBox.btnAccept.setOnClickListener {
                                        dialogBox.dialog.dismiss()
                                        val rs = db.deleteAll()
                                        if (rs > 0){
                                            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                    dialogBox.dialog.show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Token>, t: Throwable) {
                        }
                    })
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }
    }
}