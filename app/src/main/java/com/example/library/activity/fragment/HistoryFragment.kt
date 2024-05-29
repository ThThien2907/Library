package com.example.library.activity.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.library.R
import com.example.library.activity.LoginActivity
import com.example.library.databinding.FragmentHistoryBinding
import com.example.library.utils.OnReloadListener
import com.example.library.utils.Utils
import com.google.android.material.tabs.TabLayout

@Suppress("DEPRECATION")
class HistoryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnReloadListener {
    private lateinit var binding : FragmentHistoryBinding
    private var borrowAcceptedFragment = BorrowAcceptedFragment(this)
    private var borrowPendingFragment = BorrowPendingFragment()
    private var borrowRejectedFragment = BorrowRejectedFragment()

    private lateinit var currentFragment: Fragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener(this@HistoryFragment)

        currentFragment = borrowAcceptedFragment

        //kiểm tra đăng nhập
        if (!Utils.isLogin(requireActivity()))
        {
            binding.container.visibility = View.GONE
            binding.layoutRequireLogin.visibility = View.VISIBLE
        }
        else {
            binding.container.visibility = View.VISIBLE
            binding.layoutRequireLogin.visibility = View.GONE

            //Ánh xạ view
            val tabLayout = binding.tabLayoutHistory

            //Set tiêu đề cho các tab
            tabLayout.addTab(tabLayout.newTab().setText("Hoàn thành"))
            tabLayout.addTab(tabLayout.newTab().setText("Đang xử lí"))
            tabLayout.addTab(tabLayout.newTab().setText("Bị từ chối"))

            //add các fragment vào fragment manager
            childFragmentManager.beginTransaction().apply {
                add(R.id.container_layout_history, borrowAcceptedFragment)
                add(R.id.container_layout_history, borrowPendingFragment).hide(borrowPendingFragment)
                add(R.id.container_layout_history, borrowRejectedFragment).hide(borrowRejectedFragment)
            }.commit()

            tabLayout.selectTab(tabLayout.getTabAt(0))
            //lắng nghe sự kiện khi click trên các tab
            tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab!!.position){
                        0 -> {
                            replaceFragment(borrowAcceptedFragment)
                        }

                        1 -> {
                            replaceFragment(borrowPendingFragment)
                        }

                        2 -> {
                            replaceFragment(borrowRejectedFragment)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRefresh() {
        Handler().postDelayed({
            onReload()
            binding.swipeRefreshLayout.isRefreshing = false
        },1000)
    }

    override fun onReload() {
        childFragmentManager.beginTransaction().apply {
            remove(borrowAcceptedFragment)
            remove(borrowPendingFragment)
            remove(borrowRejectedFragment)
        }.commitNow()
        parentFragmentManager.beginTransaction().detach(this).commitNow()
        parentFragmentManager.beginTransaction().attach(this).commitNow()
    }

    private fun replaceFragment(fragment: Fragment){
        val manager = childFragmentManager
        val transaction = manager.beginTransaction()
        transaction.hide(currentFragment)
        transaction.show(fragment)
        transaction.commit()
        currentFragment = fragment
    }
}