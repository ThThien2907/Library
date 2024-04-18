package com.example.library.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.library.R
import com.example.library.adapter.BannerAdapterRcv
import com.example.library.adapter.BookAdapterRcv
import com.example.library.api.BookApi
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentHomeBinding
import com.example.library.model.Banner
import com.example.library.model.Book
import com.example.library.model.BookList
import retrofit2.Call
import retrofit2.Response


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterBook: BookAdapterRcv
    private lateinit var adapterBanner: BannerAdapterRcv
    private lateinit var listBook: ArrayList<Book>
    private lateinit var listBanner: ArrayList<Banner>

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
        initBanner()
        initBook()

        binding.edtSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                val manager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    private fun initBook() {
        listBook = ArrayList()
        adapterBook = BookAdapterRcv(this.requireContext())

        val bookApi = RetrofitService.getInstance().create(BookApi::class.java)
        val data = bookApi.getBook(10,1)

        data.enqueue(object : retrofit2.Callback<BookList> {
            override fun onResponse(call: Call<BookList>, response: Response<BookList>) {
                val bookList = response.body()
                listBook = bookList!!.data

                adapterBook.setData(listBook)
                binding.rcvBook.adapter = adapterBook
                binding.rcvBook.layoutManager = GridLayoutManager(this@HomeFragment.context, 3)
            }

            override fun onFailure(call: Call<BookList>, t: Throwable) {
                Toast.makeText(context, "Có lỗi gì đó xảy ra!", Toast.LENGTH_SHORT).show()
//                Log.e("abc", "abc", t)
            }
        })
    }

    private fun initBanner(){
        var handler = Handler()
        var autoBannerSlider = Runnable {
            var currentPosition = binding.viewPager2Banner.currentItem
            if (currentPosition == listBanner.size - 1)
                binding.viewPager2Banner.currentItem = 0
            else
                binding.viewPager2Banner.currentItem = currentPosition + 1
        }
        listBanner = ArrayList()
        adapterBanner = BannerAdapterRcv()
        listBanner.add(Banner(R.mipmap.banner_1))
        listBanner.add(Banner(R.mipmap.banner_2))
        listBanner.add(Banner(R.mipmap.banner_3))
        listBanner.add(Banner(R.mipmap.banner_4))
        listBanner.add(Banner(R.mipmap.banner_5))

        adapterBanner.setData(listBanner)
        binding.viewPager2Banner.adapter = adapterBanner
        binding.indicator3Banner.setViewPager(binding.viewPager2Banner)

        binding.viewPager2Banner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(autoBannerSlider)
                handler.postDelayed(autoBannerSlider, 3000)
            }
        })
    }
}