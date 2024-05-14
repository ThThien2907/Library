package com.example.library.activity.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.library.adapter.BorrowPendingAdapter
import com.example.library.api.BorrowReturnBookApi
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentBorrowPendingBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.AuthDBHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowPendingFragment : Fragment() {
    private lateinit var binding: FragmentBorrowPendingBinding
    private lateinit var adapter: BorrowPendingAdapter
    private lateinit var listBorrowPending: ArrayList<BorrowReturnBook>
    private lateinit var db: AuthDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentBorrowPendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AuthDBHelper(requireContext())

        initContent()
    }

    private fun initContent(){
        val token = db.getToken()

        adapter = BorrowPendingAdapter()
        binding.rcvBorrowPending.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        binding.rcvBorrowPending.layoutManager = layoutManager
        listBorrowPending = ArrayList()
        adapter.setData(listBorrowPending)

        val borrowReturnBookApi = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
//        val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer " + token.accessToken)
        val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer " +
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU2NzYwNDIsImlhdCI6MTcxNTU4NjA0M30.prrpsNdzJvtdMdTKYDTZi_8Eg10aR6vVcjtycJg8SUA")
        data.enqueue(object : Callback<BorrowReturnBooks> {
            override fun onResponse(
                call: Call<BorrowReturnBooks>,
                response: Response<BorrowReturnBooks>,
            ) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        for (i in result.data){
                            if (i.status == "0")
                                listBorrowPending.add(i)
                        }
                        adapter.setData(listBorrowPending)
                    }
                }
            }

            override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
            }
        })
    }
}