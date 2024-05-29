package com.example.library.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
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
import com.example.library.utils.AuthToken
import com.example.library.utils.Dialog
import com.example.library.utils.OnItemBookClickListener
import com.example.library.utils.Utils
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

    private lateinit var bookSelected : Book
    private var bookState : Int = 2
    private var borrowReturnBookID = 0


    private val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
    private val bookApi = RetrofitService.getInstance().create(BookApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get intent
        val intent = intent
        if (intent != null){
            //lấy dữ liệu được gửi từ intent
            bookSelected = intent.getSerializableExtra("book") as Book
            //đổ dữ liệu
            initBookDetail(bookSelected)
            //kiểm tra trạng thái của sách đối với user đang được login
            checkBookState(bookSelected)
            //đổ dữ liệu cho phần sách liên quan
            initBookByCategory(bookSelected.categoryCode!!)
        }

        binding.btnBorrowBook.setOnClickListener {
            when (bookState) {
                1 -> readBook()
                2 -> borrowBook(bookSelected.id!!)
                4 -> returnBook(borrowReturnBookID)
                5 -> requireLogin()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun requireLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun initBookDetail(bookSelected: Book){
        Glide.with(this).load(bookSelected.image).into(binding.imgBook)
        binding.tvTitle.text = bookSelected.title
        binding.tvAuthor.text = bookSelected.author
        binding.tvDescription.text = bookSelected.description
    }

    private fun initBookByCategory(categoryCode: String) {
        Handler().postDelayed({
            //khởi tạo adapter và set adapter cho rcv
            listBook = ArrayList()
            adapter = BookAdapterRcv(this, this)
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rcvBookByCategory.layoutManager = layoutManager
            binding.rcvBookByCategory.adapter = adapter
            adapter.setData(listBook)

            //gọi api
            val data = bookApi.getBookByCategory(12, 1, categoryCode)
            data.enqueue(object : Callback<Books>{
                override fun onResponse(call: Call<Books>, response: Response<Books>) {
                    //gọi thành công
                    if (response.isSuccessful){
                        val result = response.body()
                        if (result != null){
                            //set data cho rcv
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
        //tạo dialog chọn ngày giờ hẹn trả
        Dialog.createDialogDatePicker(this){
            expirationTimestamp ->

            //Kiểm tra token, refresh khi sắp hết hạn
            AuthToken.refreshToken(this){
                    token ->
                //gọi api
                val data = brb.borrowBook("Bearer ${token.accessToken}", bookID.toInt(), expirationTimestamp)
                data.enqueue(object : Callback<BorrowReturnBooks>{
                    override fun onResponse(
                        call: Call<BorrowReturnBooks>,
                        response: Response<BorrowReturnBooks>,
                    ) {
                        //gọi thành công
                        if (response.isSuccessful){
                            Dialog.createDialog(this@BookDetailActivity) { dialog, tvTitle, tvContent, btnAccept, _ ->
                                dialog.setCancelable(false)
                                tvTitle.text = "Thông báo"
                                tvContent.text = "Mượn sách thành công."
                                btnAccept.setOnClickListener {
                                    dialog.dismiss()
                                    binding.btnBorrowBook.text = "Chờ duyệt"
                                    binding.btnBorrowBook.isEnabled = false
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
                            Dialog.createDialog(this@BookDetailActivity) {
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
                            Dialog.createDialogLoginSessionExpired(this@BookDetailActivity)
                    }

                    override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                        Dialog.createDialogConnectionError(this@BookDetailActivity)
                    }
                })
            }
        }

    }

    private fun returnBook(borrowReturnBookID: Int) {
        //Kiểm tra token, refresh khi sắp hết hạn
        AuthToken.refreshToken(this){
                token ->
            //gọi api
            val brb = RetrofitService.getInstance().create(BorrowReturnBookApi::class.java)
            val data = brb.returnBook("Bearer ${token.accessToken}", borrowReturnBookID)
            data.enqueue(object : Callback<BorrowReturnBooks>{
                override fun onResponse(
                    call: Call<BorrowReturnBooks>,
                    response: Response<BorrowReturnBooks>,
                ) {
                    //gọi thành công
                    if (response.isSuccessful){
                        //tạo dialog thông báo trả thành công
                        Dialog.createDialog(this@BookDetailActivity){
                                dialog, tvTitle, tvContent, btnAccept, _ ->
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
                    //nếu response code == 409 là đang bị ban
                    else if (response.code() == 409){
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        //tạo dialog thông báo đang bị ban kh thể thực hiện
                        Dialog.createDialog(this@BookDetailActivity){
                                dialog, tvTitle, tvContent, btnAccept, _ ->
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
                    Dialog.createDialogConnectionError(this@BookDetailActivity)
                }
            })
        }
    }

    private fun checkBookState(book: Book){
        //kiểm tra sách free
        if (book.free == "1"){
            bookState = 1
            binding.btnBorrowBook.text = "Đọc sách"
            binding.btnBorrowBook.isEnabled = true
            binding.btnBorrowBook.visibility = View.VISIBLE
        }
        //kiểm tra đăng nhập
        else if (!Utils.isLogin(this)) {
            bookState = 5
            binding.btnBorrowBook.text = "Đăng nhập"
            binding.btnBorrowBook.isEnabled = true
            binding.btnBorrowBook.visibility = View.VISIBLE
        }
        else {
            //Kiểm tra token, refresh khi sắp hết hạn
            AuthToken.refreshToken(this){
                    token ->
                //gọi api
                val data = brb.getMyBorrowReturnBook("Bearer ${token.accessToken}")
                data.enqueue(object : Callback<BorrowReturnBooks>{
                    override fun onResponse(
                        call: Call<BorrowReturnBooks>,
                        response: Response<BorrowReturnBooks>,
                    ) {
                        //gọi thành công
                        if (response.isSuccessful){
                            val result = response.body()
                            if (result != null){
                                val listData : ArrayList<BorrowReturnBook> = result.data
                                //duyệt từng phần tử trong danh sách phiếu mượn
                                for (i in listData){
                                    //tìm tới phần tử có bookid giống với id của book đang hiện
                                    if (i.bookId == book.id && i.status != "2" && i.status != "3"){
                                        when(i.status){
                                            "0" -> bookState = 0
                                            "1" -> bookState = 1
                                            //status = 4 nghĩa là đã quá hạn mượn
                                            //buộc phải trả sách và lưu lại giá borrowReturnBookID để thực hiện trả sách
                                            "4" -> {
                                                bookState = 4
                                                borrowReturnBookID = i.id!!.toInt()
                                            }
                                        }
                                    }
                                }

                                //kiểm tra trạng thái sách
                                when(bookState){
                                    0 -> {
                                        binding.btnBorrowBook.text = "Chờ duyệt"
                                        binding.btnBorrowBook.isEnabled = false
                                    }
                                    1 -> {
                                        binding.btnBorrowBook.text = "Đọc sách"
                                        binding.btnBorrowBook.isEnabled = true
                                    }
                                    4 -> {
                                        binding.btnBorrowBook.text = "Trả sách"
                                        binding.btnBorrowBook.isEnabled = true
                                    }
                                    2 -> {
                                        binding.btnBorrowBook.text = "Mượn sách"
                                        binding.btnBorrowBook.isEnabled = true
                                    }
                                }
                                    binding.btnBorrowBook.visibility = View.VISIBLE
                            }
                        }
                        //không có phiếu mượn nào
                        else if (response.code() == 404){
                            binding.btnBorrowBook.text = "Mượn sách"
                            binding.btnBorrowBook.isEnabled = true
                            binding.btnBorrowBook.visibility = View.VISIBLE
                        }
                        else{
                            Dialog.createDialogLoginSessionExpired(this@BookDetailActivity)
                        }
                    }

                    override fun onFailure(call: Call<BorrowReturnBooks>, t: Throwable) {
                    }
                })
            }
        }

    }

    override fun onItemBookClick(book: Book) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("onRestart", "onRestart")
        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val isLogin = sharedPreferences.getBoolean("isLogin", false)
        if (isLogin){
            startActivity(intent)
            finish()
        }
    }
}