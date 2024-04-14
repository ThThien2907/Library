package com.example.library.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.library.R
import com.example.library.databinding.ActivityMainBinding
import com.example.library.fragment.ViewPager2Adapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adaperViewPager = ViewPager2Adapter(this)
        binding.viewPager2.adapter = adaperViewPager
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position){
                    0 -> {
                        binding.bottomNav.menu.findItem(R.id.mnu_home).isChecked = true
                    }
                    1 -> {
                        binding.bottomNav.menu.findItem(R.id.mnu_borrow_return).isChecked = true
                    }
                    2 -> {
                        binding.bottomNav.menu.findItem(R.id.mnu_account).isChecked = true
                    }
                }
            }
        })
        binding.bottomNav.setOnNavigationItemSelectedListener {menuItem ->
            when (menuItem.itemId){
                R.id.mnu_home -> {
                    binding.viewPager2.currentItem = 0
                    true
                }
                R.id.mnu_borrow_return -> {
                    binding.viewPager2.currentItem = 1
                    true
                }
                R.id.mnu_account -> {
                    binding.viewPager2.currentItem = 2
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}