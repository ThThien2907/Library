package com.example.library.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import com.example.library.R
import com.example.library.api.AuthApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.ActivityLoginBinding
import com.example.library.model.Token
import com.example.library.utils.AuthDBHelper
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var authApi = RetrofitService.getInstance().create(AuthApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validOnTextChange()

        binding.btnLogin.setOnClickListener {
            if (validOnClickBtnLogin()) {
                binding.progressBar.visibility = View.VISIBLE
                val email = binding.edtEmail.text.toString().trim()
                val password = binding.edtPassword.text.toString().trim()

                val data = authApi.login(email, password)
                data.enqueue(object : Callback<Token> {
                    override fun onResponse(call: Call<Token>, response: Response<Token>) {
                        if (!response.isSuccessful) {
                            val errorBody = response.errorBody()?.string()
                            val errorResponse =
                                Gson().fromJson(errorBody, ErrorResponse::class.java)
                            binding.progressBar.visibility = View.GONE
                            Dialog.createDialog(this@LoginActivity) { dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                                dialog.setCancelable(true)

                                tvTitle.text = "Lỗi đăng nhập!"
                                tvContent.text = errorResponse.errors[0]
                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()
                            }
                        } else {
                            Handler(Looper.myLooper()!!).postDelayed({
                                val result = response.body()
                                if (result != null) {
                                    val token = Token(
                                        result.accessToken,
                                        result.refreshToken,
                                        result.role,
                                        result.expirationAccessTokenTime,
                                        result.expirationRefreshTokenTime
                                    )
                                    AuthToken.storeToken(this@LoginActivity, token)
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }, 1000)
                        }
                    }

                    override fun onFailure(call: Call<Token>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Có lỗi gì đó xảy ra!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                    }
                })
            }
        }

        binding.btnRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            Handler(Looper.myLooper()!!).postDelayed({
                val i = Intent(this, RegisterActivity::class.java)
                startActivity(i)
                finish()
            }, 500)
        }
    }

    private fun validOnClickBtnLogin(): Boolean {
        binding.layoutEdtEmail.error = validEmail()
        binding.layoutEdtPassword.error = validPassword()

        return binding.layoutEdtEmail.error == null && binding.layoutEdtPassword.error == null
    }

    private fun validOnTextChange() {
        binding.edtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtEmail.error = validEmail()
        }

        binding.edtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtPassword.error = validPassword()
        }
    }

    private fun validPassword(): String? {
        val password = binding.edtPassword.text.toString()

        if (password.isEmpty())
            return "Không được bỏ trống!"
        if (password.length < 6)
            return "Mật khẩu phải có ít nhất 6 kí tự"
        return null
    }

    private fun validEmail(): String? {
        val email = binding.edtEmail.text.toString()

        if (email.isEmpty())
            return "Không được bỏ trống!"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Email không hợp lệ!"
        return null
    }

}