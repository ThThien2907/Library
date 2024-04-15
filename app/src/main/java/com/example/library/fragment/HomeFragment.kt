package com.example.library.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.library.R
import com.example.library.adapter.BookAdapterRcv
import com.example.library.api.BookApi
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentHomeBinding
import com.example.library.model.Book
import com.example.library.model.BookList
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: BookAdapterRcv
    private lateinit var listBook: ArrayList<Book>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    //http://localhost/CT06/do_an/api/routes/book/get_books.php?limit=99&page=1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listBook = ArrayList()
        adapter = BookAdapterRcv()

        val bookApi = RetrofitService.getInstance().create(BookApi::class.java)
        val data = bookApi.getBook(10,1)

        data.enqueue(object : retrofit2.Callback<BookList> {
            override fun onResponse(call: Call<BookList>, response: Response<BookList>) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                val bookList = response.body()
                listBook = bookList!!.data

                adapter.setData(listBook)
                binding.rcvBook.adapter = adapter
                binding.rcvBook.layoutManager = GridLayoutManager(this@HomeFragment.context, 3)
            }

            override fun onFailure(call: Call<BookList>, t: Throwable) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                Log.e("abc", "abc", t)
            }

        })
    }
}