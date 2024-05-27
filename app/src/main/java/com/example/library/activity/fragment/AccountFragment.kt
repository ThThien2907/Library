package com.example.library.activity.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
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
//        db = AuthDBHelper(requireContext())
//        val token = db.getToken()

//        val sharedPreferences = activity?.getSharedPreferences("token", Context.MODE_PRIVATE)
//        val accessToken = sharedPreferences?.getString("access_token", null)
//        val refreshToken = sharedPreferences?.getString("refresh_token", null)
        Log.e("acc", AuthToken.getToken(requireActivity()).toString())


        initProfile()
        binding.swipeRefreshLayout.setOnRefreshListener(this)

        binding.btnAccount.setOnClickListener {
//            Toast.makeText(requireContext(),"Đang phát triển thêm", Toast.LENGTH_SHORT).show()
            AuthToken.refreshToken(requireActivity()){
                token ->
                Log.e("token", token.toString())

            }
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
            logout()
        }
    }

    private fun logout(){
        AuthToken.refreshToken(requireActivity()){
            token ->
            val data = authApi.logout("Bearer ${token.accessToken}", "Bearer ${token.refreshToken}")
            data.enqueue(object : Callback<Token>{
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            AuthToken.storeToken(requireActivity(), Token())
                            val intent = Intent(this@AccountFragment.activity, LoginActivity::class.java)
                            startActivity(intent)
                            this@AccountFragment.activity?.finish()
                        }
                    }
                    else {
                        Dialog.createDialogLoginSessionExpired(requireActivity())
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                }
            })
        }
    }

    private fun initProfile() {
        AuthToken.refreshToken(requireActivity()){
            token ->
            val data = userApi.getMyProfile("Bearer ${token.accessToken}")
            data.enqueue(object : Callback<Users>{
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            val user : User? = result.data
                            binding.tvName.text = user!!.name
                            if (user.isBanned == "1")
                                binding.tvBanned.visibility = View.VISIBLE
                            else
                                binding.tvBanned.visibility = View.GONE
                        }
                    }
                    else{
                        Dialog.createDialogLoginSessionExpired(requireActivity())
                    }
                }

                override fun onFailure(call: Call<Users>, t: Throwable) {
                }
            })
        }
    }

    override fun onRefresh() {
        Handler().postDelayed({
            parentFragmentManager.beginTransaction().detach(this).commitNow()
            parentFragmentManager.beginTransaction().attach(this).commitNow()
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000)

    }
}