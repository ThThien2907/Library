package com.example.library.activity.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.library.R
import com.example.library.activity.LoginActivity
import com.example.library.api.AuthApi
import com.example.library.api.RetrofitService
import com.example.library.api.UserApi
import com.example.library.databinding.FragmentAccountBinding
import com.example.library.model.Token
import com.example.library.model.User
import com.example.library.model.Users
import com.example.library.utils.AuthDBHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var db : AuthDBHelper
    private var userApi = RetrofitService.getInstance().create(UserApi::class.java)
    private var authApi = RetrofitService.getInstance().create(AuthApi::class.java)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AuthDBHelper(requireContext())
        val token = db.getToken()

        initProfile(token)

        binding.btnAccount.setOnClickListener {
            Toast.makeText(requireContext(),"Đang phát triển thêm", Toast.LENGTH_SHORT).show()
        }

        binding.btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + resources.getString(R.string.hotline))
            startActivity(intent)
        }

        binding.btnWebsite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(resources.getString(R.string.website))
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            logOut(token)
        }
    }

    private fun logOut(token: Token){
        val data = authApi.logout("Bearer " + token.accessToken, "Bearer " + token.refreshToken)
        data.enqueue(object : Callback<Token>{
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        val rs = db.deleteToken(token.refreshToken!!)
                        if (rs >= 1){
                            val intent = Intent(this@AccountFragment.activity, LoginActivity::class.java)
                            startActivity(intent)
                            this@AccountFragment.activity?.finish()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
            }
        })
    }
    private fun initProfile(token: Token) {
        val data = userApi.getMyProfile("Bearer " + token.accessToken)
        data.enqueue(object : Callback<Users>{
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        val user : User? = result.data
                        binding.tvName.text = user!!.name
                    }
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
            }
        })
    }
}