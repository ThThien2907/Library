package com.example.library.activity.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                HomeFragment()
            }
            1 -> {
                BorrowReturnFragment()
            }
            2 -> {
                AccountFragment()
            }
            else -> {
                HomeFragment()
            }
        }
    }
}