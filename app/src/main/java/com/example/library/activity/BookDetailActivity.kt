package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.adapter.BookAdapterRcv
import com.example.library.api.BookApi
import com.example.library.api.BorrowReturnBookApi
import com.example.library.api.ErrorResponse
import com.example.library.api.RetrofitService
import com.example.library.databinding.ActivityBookDetailBinding
import com.example.library.model.Book
import com.example.library.model.Books
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.AuthDBHelper
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.OnItemBookClickListener
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class BookDetailActivity : AppCompatActivity(), OnItemBookClickListener {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var adapter: BookAdapterRcv
    private lateinit var listBook: ArrayList<Book>
    private lateinit var db: AuthDBHelper
    private var bookState : Int = 2
    private val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
    private val bookApi = RetrofitService.getInstance().create(BookApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AuthDBHelper(this)

        var bookSelected = Book()
        val intent = intent
        if (intent != null){
            bookSelected = intent.getSerializableExtra("book") as Book
            initBookDetail(bookSelected)
            checkBorrowState(bookSelected.id!!)
            initBookByCategory(bookSelected.categoryCode!!)
        }

        binding.btnBorrowBook.setOnClickListener {
            when (bookState) {
                1 -> readBook()
                2 -> borrowBook(bookSelected.id!!)
                4 -> returnBook(bookSelected.id!!)
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initBookDetail(bookSelected: Book){
        Glide.with(this).load(bookSelected.image).into(binding.imgBook)
        binding.tvTitle.text = bookSelected.title
        binding.tvAuthor.text = bookSelected.author
        binding.tvDescription.text = bookSelected.description
    }

    private fun initBookByCategory(categoryCode: String) {
        Handler().postDelayed({
            listBook = ArrayList()
            adapter = BookAdapterRcv(this, this)
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rcvBookByCategory.layoutManager = layoutManager
            binding.rcvBookByCategory.adapter = adapter
            adapter.setData(listBook)

            val data = bookApi.getBookByCategory(12, 1, categoryCode)
            data.enqueue(object : Callback<Books>{
                override fun onResponse(call: Call<Books>, response: Response<Books>) {
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            listBook.addAll(result.data)
                            adapter.setData(listBook)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<Books>, t: Throwable) {
                }
            })
        }, 1000)
    }

    private fun readBook(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(resources.getString(R.string.read_book))
        startActivity(intent)
    }
    private fun borrowBook(bookID: String){
//        val token = db.getToken()
        Dialog.createDialogDatePicker(this){
            expirationTimestamp ->
            AuthToken.refreshToken(this){
                    token ->
                val data = brb.borrowBook("Bearer ${token.accessToken}", bookID.toInt(), expirationTimestamp)
                data.enqueue(object : Callback<BorrowReturnBooks>{
                    override fun onResponse(
                        call: Call<BorrowReturnBooks>,
                        response: Response<BorrowReturnBooks>,
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(this@BookDetailActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            binding.btnBorrowBook.text = "Chờ duyệt"
                            binding.btnBorrowBook.isEnabled = false
                        }
                        else
                            Toast.makeText(this@BookDetailActivity, "Co loi ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                    }
                })
            }
        }

    }

    private fun returnBook(bookID: String) {
        AuthToken.refreshToken(this){
                token ->
//            val token = db.getToken()
            val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
            val data = brb.returnBook("Bearer ${token.accessToken}", bookID.toInt())
//            val data = brb.returnBook("Bearer " +
//                    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU3Nzk5ODAsImlhdCI6MTcxNTY4OTk4MH0.uY5oBcLPYOMLDCrnlL77b-6JApNn2h16n3YT2y-TSgc"
//                , borrowReturnBook.id!!.toInt())
            data.enqueue(object : Callback<BorrowReturnBooks>{
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    if (response.isSuccessful){
                        Dialog.createDialog(this@BookDetailActivity){
                                dialog, tvTitle, tvContent, btnAccept, btnCancel ->
                            dialog.setCancelable(false)
                            tvTitle.text = "Thông báo"
                            tvContent.text = "Trả sách thành công."
                            btnAccept.setOnClickListener {
                                dialog.dismiss()
                                bookState = 2
                                binding.btnBorrowBook.text = "Mượn sách"
                            }
                            dialog.show()
                        }
                    }
                    else if (response.code() == 409){
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Dialog.createDialog(this@BookDetailActivity){
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
                        Dialog.createDialogLoginSessionExpired(this@BookDetailActivity)
                }

                override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                }
            })
        }
    }
    private fun checkBorrowState(bookID: String){
//        val token = db.getToken()
        AuthToken.refreshToken(this){
            token ->
            val data = brb.getMyBorrowReturnBook("Bearer ${token.accessToken}")
//        val data = brb.getMyBorrowReturnBook("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEwIiwibmFtZSI6InRoaWVuIiwicm9sZSI6IlVSIiwiYmFuU3RhdHVzIjoiMCIsImV4cCI6MTcxNjM3NTUwOSwiaWF0IjoxNzE2Mjg5MTA5fQ.LXMH6s4tu74DFld6dibxnQ-yBmq2K63CbZGn6W-vfa0")
            data.enqueue(object : Callback<BorrowReturnBooks>{
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            val listData : ArrayList<BorrowReturnBook> = result.data
                            for (i in listData){
                                if (i.bookId == bookID){
                                    when(i.status){
                                        "0" -> bookState = 0
                                        "1" -> bookState = 1
                                        "4" -> bookState = 4
                                    }
                                }
                            }

                            when(bookState){
                                0 -> {
                                    binding.btnBorrowBook.text = "Chờ duyệt"
                                    binding.btnBorrowBook.isEnabled = false
                                }
                                1 -> {
                                    binding.btnBorrowBook.text = "Đọc sách"
                                    binding.btnBorrowBook.isEnabled = true
                                }
//                                4 -> {
//                                    binding.btnBorrowBook.text = "Trả sách"
//                                    binding.btnBorrowBook.isEnabled = true
//                                }
                                2 -> {
                                    binding.btnBorrowBook.text = "Mượn sách"
                                    binding.btnBorrowBook.isEnabled = true
                                }
                            }
                            if (bookState != 4)
                                binding.btnBorrowBook.visibility = View.VISIBLE
                        }
                    }
                    else
                        Dialog.createDialogLoginSessionExpired(this@BookDetailActivity)
                }

                override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                }
            })
        }
    }

    override fun onItemBookClick(book: Book) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
        finish()
    }
}