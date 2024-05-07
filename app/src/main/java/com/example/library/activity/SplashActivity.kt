package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import com.example.library.R
import com.example.library.api.AuthApi
import com.example.library.api.RetrofitService
import com.example.library.model.Token
import com.example.library.utils.AuthDBHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var db: AuthDBHelper
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        db = AuthDBHelper(this)
        val token = db.getToken()
//
//        Log.e("b", token.toString())
//
//        val btn = findViewById<Button>(R.id.btnnnn)
//        btn.setOnClickListener {
//            db.deleteToken2("UR")
//            Log.e("b", token.toString())
//        }
        Handler().postDelayed(Runnable {
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
                                if (rs >= 1) {
                                    val intent =
                                        Intent(this@SplashActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
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