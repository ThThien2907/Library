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
//    private lateinit var dialogBox: Utils.DialogBox
    private lateinit var db : AuthDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val sharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
//        val e2 = sharedPreferences.getString("access_token", null)
//        val e3 = sharedPreferences.getString("refresh_token", null)
//
//        Log.e("e2", e2.toString())
//        Log.e("e3", e3.toString())

//        binding.btnLogin.setOnClickListener {
//            Dialog.createDialogDatePicker(this){
//
//            }
////            val calendar = Calendar.getInstance()
//////            var selectedDate = Calendar.getInstance()
////            val datePickerDialog = DatePickerDialog(this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
////                val selectedDate = Calendar.getInstance()
////                selectedDate.set(year, monthOfYear, dayOfMonth)
////                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
////                val formatDate = dateFormat.format(selectedDate.time)
//////                btnSelectDate.text = formatDate
////                Log.e("date", selectedDate.time.toString())
////                Log.e("date", formatDate)
////            },
////                calendar.get(Calendar.YEAR),
////                calendar.get(Calendar.MONTH),
////                calendar.get(Calendar.DAY_OF_MONTH)
////            )
////            datePickerDialog.show()
//        }


        validOnTextChange()
//
//        db = AuthDBHelper(this@LoginActivity)
//
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
//                            Toast.makeText(this@LoginActivity, errorResponse.errors[0], Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                            Dialog.createDialog(this@LoginActivity){
                                dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                                dialog.setCancelable(true)

                                tvTitle.text = "Lỗi đăng nhập"
                                tvContent.text = errorResponse.errors[0]
                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                }

                                dialog.show()
                            }

//                            dialogBox = Utils.DialogBox(this@LoginActivity)
//                            dialogBox.createDialog()
//                            dialogBox.dialog.setCancelable(true)
//                            dialogBox.tvTitle.text = "Lỗi đăng nhập"
//                            dialogBox.tvContent.text = errorResponse.errors[0]
//
//                            dialogBox.btnAccept.setOnClickListener {
//                                dialogBox.dialog.dismiss()
//                            }
//
//                            dialogBox.dialog.show()
                        }
                        else {
                            Handler(Looper.myLooper()!!).postDelayed( {
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
                                    Log.e("login", AuthToken.getToken(this@LoginActivity).toString())
//                                    val rs = db.addNewToken(token)
//                                    if (rs >= 1){
//                                    sharedPreferences.edit().apply {
//                                        putString("access_token", result.accessToken)
//                                        putString("refresh_token", result.refreshToken)
//                                        putString("role", result.role)
//                                        putLong("expirationAccessTokenTime", result.expirationAccessTokenTime!!)
//                                        putLong("expirationRefreshTokenTime", result.expirationRefreshTokenTime!!)
//                                    }.apply()
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
//                                    }
                                }
                            },1000)
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
            Handler(Looper.myLooper()!!).postDelayed( {
                val i = Intent(this, RegisterActivity::class.java)
                startActivity(i)
                finish()
            },500)
        }
    }

    private fun validOnClickBtnLogin(): Boolean {
        binding.layoutEdtEmail.error = validEmail()
        binding.layoutEdtPassword.error = validPassword()

        return binding.layoutEdtEmail.error == null && binding.layoutEdtPassword.error == null
    }
    private fun validOnTextChange(){
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