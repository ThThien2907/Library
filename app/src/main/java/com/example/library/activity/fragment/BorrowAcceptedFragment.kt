package com.example.library.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.library.R
import com.example.library.activity.BookDetailActivity
import com.example.library.adapter.BorrowAcceptedAdapter
import com.example.library.api.BookApi
import com.example.library.api.BorrowReturnBookApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentBorrowAcceptedBinding
import com.example.library.model.Book
import com.example.library.model.BookByID
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("SetTextI18n")
class BorrowAcceptedFragment(private val listener: OnReloadListener) :
    Fragment(),
    OnReturnBookClickListener,
    OnReadBookClickListener,
    OnBorrowAgainClickListener,
    OnItemClickListener{

    private lateinit var binding: FragmentBorrowAcceptedBinding
    private lateinit var adapter: BorrowAcceptedAdapter
    private lateinit var listBorrowAccepted: ArrayList<BorrowReturnBook>

    private val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)


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

        if (Utils.isLogin(requireActivity()))
            initContent()
    }

    private fun initContent() {
        //khởi tạo adapter và set adapter cho rcv
        adapter = BorrowAcceptedAdapter(requireContext(), this, this, this, this)
        binding.rcvBorrowAccepted.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcvBorrowAccepted.layoutManager = layoutManager
        listBorrowAccepted = ArrayList()
        adapter.setData(listBorrowAccepted)

        //Kiểm tra token, refresh khi sắp hết hạn
        AuthToken.refreshToken(requireActivity()) { token ->
            //gọi api
            val data = brb.getMyBorrowReturnBook("Bearer ${token.accessToken}")
            data.enqueue(object : Callback<BorrowReturnBooks> {
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    //gọi thành công
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            //fragment này chỉ hiện thị các phiếu đang mượn, đã trả và quá hạn
                            //status = "1" là "đang mượn"
                            //status = "3" là "đã trả"
                            //status = "4" là "quá hạn"
                            for (i in result.data) {
                                when (i.status) {
                                    "1" -> listBorrowAccepted.add(i)
                                    "3" -> listBorrowAccepted.add(i)
                                    "4" -> listBorrowAccepted.add(i)
                                }
                            }
                            //sort lại list, sách quá hạn sẽ hiện lên đầu tiền của rcv
                            val customOrder = listOf("4", "1", "3")
                            listBorrowAccepted.sortBy { customOrder.indexOf(it.status) }

                            adapter.setData(listBorrowAccepted)
                            binding.progressBar.visibility = View.GONE

                            if (listBorrowAccepted.size > 0) {
                                binding.tvNotification.visibility = View.GONE
                            } else {
                                binding.tvNotification.visibility = View.VISIBLE
                            }
                        }
                    }
                    //không có phiếu mượn nào
                    else if (response.code() == 404){
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

    override fun onReturnBookClick(borrowReturnBook: BorrowReturnBook) {
        //Kiểm tra token, refresh khi sắp hết hạn
        AuthToken.refreshToken(requireActivity()) { token ->
            //gọi api
            val data = brb.returnBook("Bearer ${token.accessToken}", borrowReturnBook.id!!.toInt())
            data.enqueue(object : Callback<BorrowReturnBooks> {
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    //gọi thành công
                    if (response.isSuccessful) {
                        //tạo dialog thông báo trả thành công và reload lại rcv
                        Dialog.createDialog(requireActivity()) { dialog, tvTitle, tvContent, btnAccept, _ ->
                            dialog.setCancelable(false)
                            tvTitle.text = "Thông báo"
                            tvContent.text = "Trả sách thành công."
                            btnAccept.setOnClickListener {
                                dialog.dismiss()
                                onReload()
                            }
                            dialog.show()
                        }
                    }
                    //nếu response code == 409 là đang bị ban
                    else if (response.code() == 409) {
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        //tạo dialog thông báo đang bị ban kh thể thực hiện
                        Dialog.createDialog(requireActivity()) { dialog, tvTitle, tvContent, btnAccept, _ ->
                            dialog.setCancelable(true)
                            tvTitle.text = "Thông báo"
                            tvContent.text = errorResponse.errors[0]
                            btnAccept.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                    } else
                        Dialog.createDialogLoginSessionExpired(requireActivity())
                }

                override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                    Dialog.createDialogConnectionError(requireActivity())
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
        //tạo dialog chọn ngày giờ hẹn trả
        Dialog.createDialogDatePicker(requireContext()) {
                expirationTimestamp ->

            //Kiểm tra token, refresh khi sắp hết hạn
            AuthToken.refreshToken(requireActivity()) { token ->
                //gọi api
                val data = brb.borrowBook(
                    "Bearer ${token.accessToken}",
                    bookID.toInt(),
                    expirationTimestamp
                )
                data.enqueue(object : Callback<BorrowReturnBooks> {
                    override fun onResponse(
                        call: Call<BorrowReturnBooks>,
                        response: Response<BorrowReturnBooks>,
                    ) {
                        //gọi thành công
                        if (response.isSuccessful) {
                            //tạo dialog thông báo mượn thành công và reload lại rcv
                            Dialog.createDialog(requireActivity()) { dialog, tvTitle, tvContent, btnAccept, _ ->
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
                        //nếu response code == 409 là đang bị ban
                        else if (response.code() == 409) {
                            val errorBody = response.errorBody()?.string()
                            val errorResponse =
                                Gson().fromJson(errorBody, ErrorResponse::class.java)
                            //tạo dialog thông báo đang bị ban kh thể thực hiện
                            Dialog.createDialog(requireActivity()) {
                                    dialog, tvTitle, tvContent, btnAccept, _ ->
                                dialog.setCancelable(true)
                                tvTitle.text = "Thông báo"
                                tvContent.text = errorResponse.errors[0]
                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()
                            }
                        } else
                            Dialog.createDialogLoginSessionExpired(requireActivity())
                    }

                    override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                        Dialog.createDialogConnectionError(requireActivity())
                    }
                })
            }
        }
    }

    fun onReload(){
        parentFragmentManager.beginTransaction().detach(this).commitNow()
        parentFragmentManager.beginTransaction().attach(this).commitNow()
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