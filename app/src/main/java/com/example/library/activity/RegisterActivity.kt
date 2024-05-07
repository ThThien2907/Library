package com.example.library.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.library.api.AuthApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.ActivityRegisterBinding
import com.example.library.model.Token
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var authApi = RetrofitService.getInstance().create(AuthApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validOnTextChange()

        binding.btnRegister.setOnClickListener {
            if (validOnClickBtnRegister()) {
                binding.progressBar.visibility = View.VISIBLE
                val name = binding.edtName.text.toString().trim()
                val email = binding.edtEmail.text.toString().trim()
                val password = binding.edtPassword.text.toString().trim()
                val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

                val data = authApi.register(name, email, password, confirmPassword)
                data.enqueue(object : Callback<Token> {
                    override fun onResponse(call: Call<Token>, response: Response<Token>) {
                        if (!response.isSuccessful) {
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            Log.e("aaa", errorResponse.toString())
                            Toast.makeText(this@RegisterActivity, errorResponse.errors[0], Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }
                        else {
                            Handler().postDelayed(Runnable {
                                val result = response.body()
                                if (result != null) {
                                    val intent =
                                        Intent(this@RegisterActivity, LoginActivity::class.java)
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Đăng ký thành công",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(intent)
                                    finish()
                                }
                            },2000)
                        }
                    }

                    override fun onFailure(call: Call<Token>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Có lỗi gì đó xảy ra. Vui lòng thử lại!",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.progressBar.visibility = View.GONE

                    }
                })
            }
        }


        binding.tvLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            Handler().postDelayed(Runnable {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }, 1000)
        }
    }

    private fun validOnClickBtnRegister(): Boolean {
        binding.layoutEdtName.error = validName()
        binding.layoutEdtEmail.error = validEmail()
        binding.layoutEdtPassword.error = validPassword()
        binding.layoutEdtConfirmPassword.error = validConFirmPassword()

        return binding.layoutEdtName.error == null
                && binding.layoutEdtEmail.error == null
                && binding.layoutEdtPassword.error == null
                && binding.layoutEdtConfirmPassword.error == null
    }
    private fun validOnTextChange(){
        binding.edtName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtName.error = validName()
        }

        binding.edtEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtEmail.error = validEmail()
        }

        binding.edtPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtPassword.error = validPassword()
        }

        binding.edtConfirmPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtConfirmPassword.error = validConFirmPassword()
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

    private fun validConFirmPassword(): String? {
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        if (confirmPassword.isEmpty())
            return "Không được bỏ trống!"
        if (password != confirmPassword)
            return "Mật khẩu không trùng khớp!"
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

    private fun validName(): String? {
        val name = binding.edtName.text.toString()

        if (name.isEmpty())
            return "Không được bỏ trống!"
        return null
    }
}


