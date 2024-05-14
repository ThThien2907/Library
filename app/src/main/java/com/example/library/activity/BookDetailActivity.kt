package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.api.BorrowReturnBookApi
import com.example.library.api.RetrofitService
import com.example.library.databinding.ActivityBookDetailBinding
import com.example.library.model.Book
import com.example.library.model.BorrowReturnBook
import com.example.library.model.BorrowReturnBooks
import com.example.library.utils.AuthDBHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var db: AuthDBHelper
    private var bookState : Int = 2
    private val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)

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
        }

        binding.btnBorrowBook.setOnClickListener {
            if (bookState == 1)
                readBook()
            else
                borrowBook(bookSelected.id!!)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initBookDetail(bookSelected: Book){
        Glide.with(this).load(bookSelected.image).into(binding.imgBook)
        binding.tvTitle.text = bookSelected.title
        binding.tvAuthor.text = bookSelected.author
        binding.tvAvailable.text = bookSelected.available
        binding.tvDescription.text = bookSelected.description
    }

    private fun readBook(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(resources.getString(R.string.read_book))
        startActivity(intent)
    }
    private fun borrowBook(bookID: String){
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

    private fun checkBorrowState(bookID: String){
        val token = db.getToken()
//        val data = brb.getMyBorrowReturnBook("Bearer " + token.accessToken)
        val data = brb.getMyBorrowReturnBook("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjciLCJuYW1lIjoidXNlcjEiLCJyb2xlIjoiVVIiLCJleHAiOjE3MTU2NzYwNDIsImlhdCI6MTcxNTU4NjA0M30.prrpsNdzJvtdMdTKYDTZi_8Eg10aR6vVcjtycJg8SUA")
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
                                }
                            }
                        }
                        when(bookState){
                            0 -> {
                                binding.btnBorrowBook.text = "Chờ duyệt"
                                binding.btnBorrowBook.isEnabled = false
                            }
                            1 -> {
                                binding.btnBorrowBook.text = "Đọc ngay"
                                binding.btnBorrowBook.isEnabled = true
                            }
                            2 -> {
                                binding.btnBorrowBook.text = "Mượn ngay"
                                binding.btnBorrowBook.isEnabled = true
                            }
                        }
                        binding.btnBorrowBook.visibility = View.VISIBLE
                    }
                }
                else
                    Toast.makeText(this@BookDetailActivity, "Co loi ", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
            }
        })
    }
}