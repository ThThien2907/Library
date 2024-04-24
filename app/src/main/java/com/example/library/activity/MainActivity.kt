package com.example.library.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.library.R
import com.example.library.databinding.ActivityMainBinding
import com.example.library.activity.fragment.AccountFragment
import com.example.library.activity.fragment.BorrowReturnFragment
import com.example.library.activity.fragment.HomeFragment
import com.example.library.activity.fragment.ViewPager2Adapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var HomeFragment = HomeFragment()
    private var BorrowReturnFragment = BorrowReturnFragment()
    private var AccountFragment = AccountFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment)
//        val adaperViewPager = ViewPager2Adapter(this)
//        binding.viewPager2.adapter = adaperViewPager
//        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                when (position){
//                    0 -> {
//                        binding.bottomNav.menu.findItem(R.id.mnu_home).isChecked = true
//                    }
//                    1 -> {
//                        binding.bottomNav.menu.findItem(R.id.mnu_borrow_return).isChecked = true
//                    }
//                    2 -> {
//                        binding.bottomNav.menu.findItem(R.id.mnu_account).isChecked = true
//                    }
//                }
//            }
//        })
        binding.bottomNav.setOnNavigationItemSelectedListener {menuItem ->
            when (menuItem.itemId){
                R.id.mnu_home -> {
                    replaceFragment(HomeFragment)
                    true
                }
                R.id.mnu_borrow_return -> {
                    replaceFragment(BorrowReturnFragment)
                    true
                }
                R.id.mnu_account -> {
                    replaceFragment(AccountFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
    fun replaceFragment(fragment: Fragment){
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(binding.contentFrame.id, fragment)
        transaction.commit()
    }
}