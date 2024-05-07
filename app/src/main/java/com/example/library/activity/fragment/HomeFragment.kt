package com.example.library.activity.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.library.R
import com.example.library.activity.BookDetailActivity
import com.example.library.adapter.BannerAdapterRcv
import com.example.library.adapter.BookAdapterRcv
import com.example.library.adapter.CategoryAdapter
import com.example.library.api.BookApi
import com.example.library.api.RetrofitService
import com.example.library.databinding.FragmentHomeBinding
import com.example.library.model.*
import com.example.library.utils.OnItemBookClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class HomeFragment() : Fragment(), OnItemBookClickListener, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterBook: BookAdapterRcv
    private lateinit var adapterBanner: BannerAdapterRcv
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var listBook: ArrayList<Book>
    private lateinit var listBanner: ArrayList<Banner>
    private lateinit var listCategory: ArrayList<Category>

    private var isLoading = false
    private var isLastPage = false
    private var isFilterByCategory = false
    private var currentPage = 1
    private var totalPage = 0
    private var limitPerPage = 12


    private var bookApi = RetrofitService.getInstance().create(BookApi::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //#region set adapter cho recycleview book
        listBook = ArrayList()
        adapterBook = BookAdapterRcv(this.requireContext(),this)
        adapterBook.setData(listBook)
        binding.rcvBook.adapter = adapterBook
        val layoutManager = GridLayoutManager(context, 3)
        binding.rcvBook.layoutManager = layoutManager
        //#endregion

        initBanner()
        initCategory()
        closeKeyBoard()
        binding.swipeRefreshLayout.setOnRefreshListener(this@HomeFragment)

    }

    //đổ dữ liệu cho spinner thể loại
    private fun initCategory(){
        listCategory = ArrayList()
        val data = bookApi.getAllCategory()

        data.enqueue(object : Callback<Categories> {
            override fun onResponse(call: Call<Categories>, response: Response<Categories>) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        listCategory.addAll(result.categories)
                    }
                }
            }

            override fun onFailure(call: Call<Categories>, t: Throwable) {
                Toast.makeText(context, "Có lỗi gì đó xảy ra!", Toast.LENGTH_SHORT).show()
            }
        })
        listCategory.add(0,Category("ALL", "Tất cả thể loại"))
        adapterCategory = CategoryAdapter(this.requireActivity(), R.layout.item_category_selected, listCategory)
        binding.spnCategoryBook.adapter = adapterCategory

        //Lắng nghe sự kiện khi click vào từng mục của spinner
        binding.spnCategoryBook.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (adapterCategory.getItem(position)?.code.toString() != "ALL"){
                        isFilterByCategory = true
                        initBook(adapterCategory.getItem(position)?.code.toString())
                    }else{
                        isFilterByCategory = false
                        initBook("ALL")
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //Đổ dữ liệu cho RecycleView Book
    private fun initBook(category_code: String) {
        currentPage = 1
        isLastPage = false
        listBook = ArrayList()
        binding.loadingBook.visibility = View.VISIBLE

        //Kiểm tra có lọc theo thể loại chưa
        if (!isFilterByCategory)
            loadDataBook(currentPage)
        else{
            loadDataBookByCategory(category_code, currentPage)
        }

        //Loadmore data khi cuộn xuống cuối cùng
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
                v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //check điểm cuối cùng khi cuộn xuống, nếu đến cuối thì load thêm data
            if (scrollY - 82 == (binding.nestedScrollView.getChildAt(0).measuredHeight - binding.nestedScrollView.measuredHeight)){
                if (isFilterByCategory && !isLastPage && !isLoading){
                    isLoading = true
                    loadDataBookByCategory(category_code, currentPage)
                }else if (!isLoading && !isLastPage){
                    isLoading = true
                    loadDataBook(currentPage)
                }
            }
        })
    }

    private fun loadDataBookByCategory(category_code: String, currentPage: Int){
        Handler().postDelayed( {
            var data = bookApi.getBookByCategory(limitPerPage, currentPage, category_code)
            data.enqueue(object : Callback<Books> {
                override fun onResponse(call: Call<Books>, response: Response<Books>) {
                    val result = response.body()
                    if (result != null) {
                        if (result.message == "Successfully"){
                            listBook.addAll(result.data)
                            totalPage = result.totalPage!!
                            adapterBook.setData(listBook)

                            this@HomeFragment.currentPage += 1

                            binding.loadingBook.visibility = View.GONE

                            //Kiểm tra trang tiếp theo của thể loại này có tồn tại sách nào không
                            data = bookApi.getBookByCategory(limitPerPage, currentPage + 1, category_code)
                            data.enqueue(object : Callback<Books> {
                                override fun onResponse(call: Call<Books>, response: Response<Books>) {
                                    val result1 = response.body()
                                    if (result1 != null){
                                        //Nếu có thì ProgressBar loading_more sẽ hiện lên
                                        if (result1.message == "Successfully")
                                            binding.loadingMore.visibility = View.VISIBLE
                                        //Ngược lại thì ẩn đi và isLastPage bằng true để không load thêm nữa
                                        else{
                                            binding.loadingMore.visibility = View.GONE
                                            isLastPage = true
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<Books>, t: Throwable) {
                                }
                            })
                        }else{
                            isLastPage = true
                        }
                    }
                }
                override fun onFailure(call: Call<Books>, t: Throwable) {
                    Toast.makeText(context, "Có lỗi gì đó xảy ra!", Toast.LENGTH_SHORT).show()
                }
            })
            isLoading = false
        }, 2000)
    }

    private fun loadDataBook(currentPage: Int) {
        Handler().postDelayed({
            val data = bookApi.getBook(limitPerPage, currentPage)
            data.enqueue(object : Callback<Books> {
                override fun onResponse(call: Call<Books>, response: Response<Books>) {
                    val result = response.body()
                    if (result != null) {
                        if (result.message == "Successfully"){
                            listBook.addAll(result.data)
                            adapterBook.setData(listBook)
                            totalPage = result.totalPage!!
                            this@HomeFragment.currentPage += 1

                            binding.loadingBook.visibility = View.GONE

                            //Nếu trang hiện tại nhỏ hơn tổng số trang thì loading_more hiện lên
                            if (currentPage < totalPage)
                                binding.loadingMore.visibility = View.VISIBLE
                            else{
                                binding.loadingMore.visibility = View.GONE
                                isLastPage = true
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Books>, t: Throwable) {
                    Toast.makeText(context, "Có lỗi gì đó xảy ra!", Toast.LENGTH_SHORT).show()
                }
            })
            isLoading = false
        }, 2000)
    }

    private fun initBanner() {
        val handler = Handler()
        val autoBannerSlider = Runnable {
            //Lấy ra vị trí banner hiện tại
            val currentPosition = binding.viewPager2Banner.currentItem
            //Nếu banner ở vị trí cuối cùng thì set vị trí quay lại ban đầu ngược lại tăng vị trí lên 1
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

        //Đăng kí sự kiện khi banner thay đổi
        binding.viewPager2Banner.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(autoBannerSlider)
                handler.postDelayed(autoBannerSlider, 3000)
            }
        })
    }

    private fun closeKeyBoard() {
        //đóng bàn phím khi nhập xong từ tìm kiếm
        binding.edtSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val manager =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    override fun onItemBookClick(book: Book) {
        val intent = Intent(this.activity, BookDetailActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    override fun onRefresh() {
        Handler().postDelayed(Runnable {
            binding.swipeRefreshLayout.isRefreshing = false
        },2000)
    }

}