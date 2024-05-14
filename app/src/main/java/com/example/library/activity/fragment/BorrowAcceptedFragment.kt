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
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentBorrowAcceptedBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.*
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

        db = AuthDBHelper(requireContext())

        initContent()
    }

    private fun initContent(){
        val token = db.getToken()

        adapter = BorrowAcceptedAdapter(requireContext(),this, this, this)
        binding.rcvBorrowAccepted.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        binding.rcvBorrowAccepted.layoutManager = layoutManager
        listBorrowAccepted = ArrayList()
        adapter.setData(listBorrowAccepted)

        val borrowReturnBookApi = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
//        val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer " + token.accessToken)
        val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer " +
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU2NzYwNDIsImlhdCI6MTcxNTU4NjA0M30.prrpsNdzJvtdMdTKYDTZi_8Eg10aR6vVcjtycJg8SUA")
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
                            }
                        }
                        adapter.setData(listBorrowAccepted)
                    }
                }
            }

            override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
            }
        })
    }

    override fun onReturnBookClick(borrowReturnBook: BorrowReturnBook) {
        val token = db.getToken()
        val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
//        val data = brb.returnBook("Bearer " + token.accessToken, borrowReturnBook.id!!)
        val data = brb.returnBook("Bearer " +
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU1ODYwNDEsImlhdCI6MTcxNTQ5NjA0MX0.sus44Q5_Pi0Z_Y4We7hKxsXN6ti9YCoEZx_S7vHlcjs"
            , borrowReturnBook.id!!.toInt())
        data.enqueue(object : Callback<BorrowReturnBooks>{
            override fun onResponse(
                call: Call<BorrowReturnBooks>,
                response: Response<BorrowReturnBooks>,
            ) {
                if (response.isSuccessful){
                    Toast.makeText(context, "Tra sach thanh cong", Toast.LENGTH_SHORT).show()
                    listener.onReload()
                }
                else
                    Toast.makeText(context, "Tra sach that bai", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
            }
        })
    }

    override fun onReadBookClick(bookID: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(resources.getString(R.string.read_book))
        startActivity(intent)
    }

    override fun onBorrowAgainClick(bookID: String) {
        val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)

        val token = db.getToken()
//            val data = brb.borrowBook("Bearer " + token.accessToken, bookID)
        val data = brb.borrowBook("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU2NzYwNDIsImlhdCI6MTcxNTU4NjA0M30.prrpsNdzJvtdMdTKYDTZi_8Eg10aR6vVcjtycJg8SUA",
            bookID.toInt())
        data.enqueue(object : Callback<BorrowReturnBooks>{
            override fun onResponse(
                call: Call<BorrowReturnBooks>,
                response: Response<BorrowReturnBooks>,
            ) {
                if (response.isSuccessful){
                    Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    listener.onReload()
                }
                else
                    Toast.makeText(context, "Co loi ", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
            }
        })
    }
}