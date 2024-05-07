package com.example.library.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.library.api.AuthApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.ActivityLoginBinding
import com.example.library.model.Token
import com.example.library.utils.AuthDBHelper
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var authApi = RetrofitService.getInstance().create(AuthApi::class.java)
    private lateinit var db : AuthDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validOnTextChange()

        db = AuthDBHelper(this@LoginActivity)
//        binding.btnLogin.setOnClickListener {
//            db.deleteToken2("UR")
//            Log.e("delete", db.getToken().toString())
//        }
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
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            Toast.makeText(this@LoginActivity, errorResponse.errors[0], Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }
                        else {
                            Handler().postDelayed(Runnable {
                                val result = response.body()
                                if (result != null) {
                                    val token = Token(result.accessToken, result.refreshToken, result.role)

                                    val rs = db.addNewToken(token)
                                    if (rs >= 1){
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            },2000)
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

        binding.tvRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            Handler().postDelayed(Runnable {
                val i = Intent(this, RegisterActivity::class.java)
                startActivity(i)
                finish()
            },1000)
        }
    }
    private fun validOnClickBtnLogin(): Boolean {
        binding.layoutEdtEmail.error = validEmail()
        binding.layoutEdtPassword.error = validPassword()

        return binding.layoutEdtEmail.error == null && binding.layoutEdtPassword.error == null
    }
    private fun validOnTextChange(){
        binding.edtEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtEmail.error = validEmail()
        }

        binding.edtPassword.setOnFocusChangeListener { v, hasFocus ->
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