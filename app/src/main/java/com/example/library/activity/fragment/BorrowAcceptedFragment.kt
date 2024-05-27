package com.example.library.activity.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.library.R
import com.example.library.adapter.BorrowAcceptedAdapter
import com.example.library.api.BorrowReturnBookApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentBorrowAcceptedBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowAcceptedFragment(private val listener: OnReloadListener) :
    Fragment(),
    OnReturnBookClickListener,
    OnReadBookClickListener,
    OnBorrowAgainClickListener {
    private lateinit var binding: FragmentBorrowAcceptedBinding
    private lateinit var adapter: BorrowAcceptedAdapter
    private lateinit var listBorrowAccepted: ArrayList<BorrowReturnBook>
    private lateinit var db: AuthDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBorrowAcceptedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        db = AuthDBHelper(requireContext())

        initContent()
    }

    private fun initContent(){
        adapter = BorrowAcceptedAdapter(requireContext(),this, this, this)
        binding.rcvBorrowAccepted.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        binding.rcvBorrowAccepted.layoutManager = layoutManager
        listBorrowAccepted = ArrayList()
        adapter.setData(listBorrowAccepted)

        AuthToken.refreshToken(requireActivity()){
            token ->
//            val token = db.getToken()
            val borrowReturnBookApi = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
            val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer ${token.accessToken}")
//        val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer " +
//                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEwIiwibmFtZSI6InRoaWVuIiwicm9sZSI6IlVSIiwiYmFuU3RhdHVzIjoiMCIsImV4cCI6MTcxNjM3NTUwOSwiaWF0IjoxNzE2Mjg5MTA5fQ.LXMH6s4tu74DFld6dibxnQ-yBmq2K63CbZGn6W-vfa0")
            data.enqueue(object : Callback<BorrowReturnBooks>{
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            for (i in result.data){
                                when(i.status){
                                    "1" -> listBorrowAccepted.add(i)
                                    "3" -> listBorrowAccepted.add(i)
                                    "4" -> listBorrowAccepted.add(i)
                                }
                            }
                            adapter.setData(listBorrowAccepted)
                            if (listBorrowAccepted.size > 0)
                                binding.tvNotification.visibility = View.GONE
                            else
                                binding.tvNotification.visibility = View.VISIBLE
                        }
                    }
                    else {
                        Dialog.createDialogLoginSessionExpired(requireActivity())
                    }
                }

                override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                }
            })
        }
    }

    override fun onReturnBookClick(borrowReturnBook: BorrowReturnBook) {
        AuthToken.refreshToken(requireActivity()){
            token ->
//            val token = db.getToken()
            val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
            val data = brb.returnBook("Bearer ${token.accessToken}", borrowReturnBook.id!!.toInt())
//            val data = brb.returnBook("Bearer " +
//                    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU3Nzk5ODAsImlhdCI6MTcxNTY4OTk4MH0.uY5oBcLPYOMLDCrnlL77b-6JApNn2h16n3YT2y-TSgc"
//                , borrowReturnBook.id!!.toInt())
            data.enqueue(object : Callback<BorrowReturnBooks>{
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    if (response.isSuccessful){
                        Dialog.createDialog(requireActivity()){
                            dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                            dialog.setCancelable(false)
                            tvTitle.text = "Thông báo"
                            tvContent.text = "Trả sách thành công."
                            btnAccept.setOnClickListener {
                                dialog.dismiss()
                                listener.onReload()
                            }
                            dialog.show()
                        }
                    }
                    else if (response.code() == 409){
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Dialog.createDialog(requireActivity()){
                            dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                            dialog.setCancelable(true)
                            tvTitle.text = "Thông báo"
                            tvContent.text = errorResponse.errors[0]
                            btnAccept.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                    }
                    else
                        Dialog.createDialogLoginSessionExpired(requireActivity())
                }

                override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                }
            })
        }
    }

    override fun onReadBookClick(bookID: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(resources.getString(R.string.read_book))
        startActivity(intent)
    }

    override fun onBorrowAgainClick(bookID: String) {
        Dialog.createDialogDatePicker(requireContext()){
            expirationTimestamp ->
            AuthToken.refreshToken(requireActivity()){
                    token ->
                val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)

//            val token = db.getToken()
                val data = brb.borrowBook("Bearer ${token.accessToken}", bookID.toInt(),0)
//            val data = brb.borrowBook("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU3Nzk5ODAsImlhdCI6MTcxNTY4OTk4MH0.uY5oBcLPYOMLDCrnlL77b-6JApNn2h16n3YT2y-TSgc",
//                bookID.toInt(), 0)
                data.enqueue(object : Callback<BorrowReturnBooks>{
                    override fun onResponse(
                        call: Call<BorrowReturnBooks>,
                        response: Response<BorrowReturnBooks>,
                    ) {
                        if (response.isSuccessful){
                            Dialog.createDialog(requireActivity()){
                                    dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                                dialog.setCancelable(false)
                                tvTitle.text = "Thông báo"
                                tvContent.text = "Mượn sách thành công."
                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                    listener.onReload()
                                }
                                dialog.show()
                            }
                        }
                        else if (response.code() == 409){
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            Dialog.createDialog(requireActivity()){
                                    dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                                dialog.setCancelable(true)
                                tvTitle.text = "Thông báo"
                                tvContent.text = errorResponse.errors[0]
                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()
                            }
                        }
                        else
                            Dialog.createDialogLoginSessionExpired(requireActivity())
                    }

                    override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                    }
                })
            }
        }
    }
}