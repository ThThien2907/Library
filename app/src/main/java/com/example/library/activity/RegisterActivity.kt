package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.library.api.AuthApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.ActivityRegisterBinding
import com.example.library.model.Token
import com.example.library.utils.Dialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var authApi = RetrofitService.getInstance().create(AuthApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

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

                //gọi api
                val data = authApi.register(name, email, password, confirmPassword)
                data.enqueue(object : Callback<Token> {
                    override fun onResponse(call: Call<Token>, response: Response<Token>) {
                        //gọi không thành công
                        if (!response.isSuccessful) {
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            binding.progressBar.visibility = View.GONE
                            //tạo dialog và in ra lỗi
                            Dialog.createDialog(this@RegisterActivity){
                                dialog, tvTitle, tvContent, btnAccept, _ ->
                                dialog.setCancelable(true)
                                tvTitle.text = "Lỗi đăng ký!"
                                tvContent.text = errorResponse.errors[0]

                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()
                            }
                        }
                        //gọi thành công
                        else {
                            Handler(Looper.myLooper()!!).postDelayed( {
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                //tạo dialog thông báo đăng ký thành công và chuyển màn hình qua đăng nhập
                                Dialog.createDialog(this@RegisterActivity){
                                    dialog, tvTitle, tvContent, btnAccept, _ ->
                                    tvTitle.text = "Thành công!"
                                    tvContent.text = "Đăng ký tài khoản thành công. Mời bạn đăng nhập."

                                    btnAccept.setOnClickListener {
                                        startActivity(intent)
                                        finish()
                                    }
                                    dialog.show()

                                }

                            },500)
                        }
                    }

                    override fun onFailure(call: Call<Token>, t: Throwable) {
                        Dialog.createDialogConnectionError(this@RegisterActivity)
                        binding.progressBar.visibility = View.GONE

                    }
                })
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            Handler(Looper.myLooper()!!).postDelayed( {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }, 500)
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
        binding.edtName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtName.error = validName()
        }

        binding.edtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtEmail.error = validEmail()
        }

        binding.edtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                binding.layoutEdtPassword.error = validPassword()
        }

        binding.edtConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
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


