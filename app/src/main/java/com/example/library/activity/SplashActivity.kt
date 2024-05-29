package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import com.example.library.R
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.Utils

@SuppressLint("CustomSplashScreen, SetTextI18n")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val token = AuthToken.getToken(this)

        //kiểm tra kết nối mạng
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
            Handler(Looper.myLooper()!!).postDelayed( {
                //nếu như đã tồn tại token thì refresh lại token đó
                if (token.refreshToken != null) {
                    AuthToken.refresh(this, token.refreshToken!!){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }
    }
}