package com.example.library.activity.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.library.activity.BookDetailActivity
import com.example.library.adapter.BorrowPendingAdapter
import com.example.library.api.BookApi
import com.example.library.api.BorrowReturnBookApi
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentBorrowPendingBinding
import com.example.library.model.BookByID
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.OnItemClickListener
import com.example.library.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowPendingFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentBorrowPendingBinding
    private lateinit var adapter: BorrowPendingAdapter
    private lateinit var listBorrowPending: ArrayList<BorrowReturnBook>

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

        if (Utils.isLogin(requireActivity()))
            initContent()
    }

    private fun initContent(){
        //khởi tạo adapter và set adapter cho rcv
        adapter = BorrowPendingAdapter(this)
        binding.rcvBorrowPending.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        binding.rcvBorrowPending.layoutManager = layoutManager
        listBorrowPending = ArrayList()
        adapter.setData(listBorrowPending)

        //Kiểm tra token, refresh khi sắp hết hạn
        AuthToken.refreshToken(requireActivity()){
            token ->
            //gọi api
            val borrowReturnBookApi = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
            val data = borrowReturnBookApi.getMyBorrowReturnBook("Bearer ${token.accessToken}")
            data.enqueue(object : Callback<BorrowReturnBooks> {
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    //gọi thành công
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            //fragment này chỉ hiện thị các phiếu đang chờ duyệt
                            //status = "0" là "đang chờ duyệt"
                            for (i in result.data){
                                if (i.status == "0")
                                    listBorrowPending.add(i)
                            }
                            adapter.setData(listBorrowPending)
                            binding.progressBar.visibility = View.GONE

                            if (listBorrowPending.size > 0){
                                binding.tvNotification.visibility = View.GONE
                            }
                            else{
                                binding.tvNotification.visibility = View.VISIBLE
                            }
                        }
                    }
                    //không có phiếu mượn nào
                    else if (response.code() == 404){
                        binding.progressBar.visibility = View.GONE
                        binding.tvNotification.visibility = View.VISIBLE
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

    override fun onItemClick(bookID: String) {
        val bookApi = RetrofitService.getInstance().create(BookApi::class.java)
        val data = bookApi.getBookByID(bookID.toInt())
        data.enqueue(object : Callback<BookByID>{
            override fun onResponse(call: Call<BookByID>, response: Response<BookByID>) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        if (result.message == "Successfully"){
                            val book = result.data
                            val intent = Intent(activity, BookDetailActivity::class.java)
                            intent.putExtra("book", book)
                            startActivity(intent)
                        }
                    }
                }

            }

            override fun onFailure(call: Call<BookByID>, t: Throwable) {
                Dialog.createDialogConnectionError(requireActivity())
            }
        })
    }
}