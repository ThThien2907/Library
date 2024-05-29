package com.example.library.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.library.R
import com.example.library.activity.LoginActivity
import com.example.library.activity.MainActivity
import com.example.library.api.AuthApi
import com.example.library.api.RetrofitService
import com.example.library.api.UserApi
import com.example.library.databinding.FragmentAccountBinding
import com.example.library.model.Token
import com.example.library.model.User
import com.example.library.model.Users
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("SetTextI18n")
class AccountFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentAccountBinding
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

        //Kiểm tra đăng nhập
        if (Utils.isLogin(requireActivity())){
            initProfile()
            binding.btnLogin.visibility = View.GONE
        }
        else {
            binding.btnLogout.visibility = View.GONE
            binding.tvRequireLogin.visibility =View.VISIBLE
        }

        binding.swipeRefreshLayout.setOnRefreshListener(this)

        binding.btnAccount.setOnClickListener {

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
            //Tạo dialog confirm logout
            Dialog.createDialog(requireContext()){
                dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                btnCancel.visibility = View.VISIBLE

                dialog.setCancelable(true)
                tvTitle.text = "Đăng xuất"
                tvContent.text = "Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng?"

                btnAccept.setOnClickListener {
                    logout()
                }

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout(){
        //Kiểm tra token, refresh khi sắp hết hạn
        AuthToken.refreshToken(requireActivity()){
            token ->
            //gọi api logout
            val data = authApi.logout("Bearer ${token.accessToken}", "Bearer ${token.refreshToken}")
            data.enqueue(object : Callback<Token>{
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    //logout thành công
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            //lưu lại token = null
                            AuthToken.storeToken(requireActivity(), Token())

                            //chuyển màn hình đến main activity
                            val intent = Intent(this@AccountFragment.activity, MainActivity::class.java)
                            startActivity(intent)
                            this@AccountFragment.activity?.finish()
                        }
                    }
                    //lỗi kh logout được
                    else {
                        //tạo dialog hết phiên đăng nhập
                        Dialog.createDialogLoginSessionExpired(requireActivity())
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Dialog.createDialogConnectionError(requireActivity())
                }
            })
        }
    }

    private fun initProfile() {
        binding.imgAvt.visibility = View.VISIBLE
        //Kiểm tra token, refresh khi sắp hết hạn
        AuthToken.refreshToken(requireActivity()){
            token ->
            //gọi api
            val data = userApi.getMyProfile("Bearer ${token.accessToken}")
            data.enqueue(object : Callback<Users>{
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    //gọi thành công
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            //set data cho các view
                            val user : User? = result.data
                            binding.tvName.visibility = View.VISIBLE
                            binding.tvName.text = user!!.name

                            //nếu user bị ban thì hiện thông báo bị ban
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
        Handler(Looper.myLooper()!!).postDelayed({
            parentFragmentManager.beginTransaction().detach(this).commitNow()
            parentFragmentManager.beginTransaction().attach(this).commitNow()
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000)

    }
}