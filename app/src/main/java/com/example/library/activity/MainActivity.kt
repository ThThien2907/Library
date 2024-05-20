package com.example.library.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.library.R
import com.example.library.databinding.ActivityMainBinding
import com.example.library.activity.fragment.AccountFragment
import com.example.library.activity.fragment.HistoryFragment
import com.example.library.activity.fragment.HomeFragment
import com.example.library.model.Token
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import java.util.*
import kotlin.concurrent.fixedRateTimer

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private var homeFragment = HomeFragment()
    private var historyFragment = HistoryFragment()
    private var accountFragment = AccountFragment()
    private var currentFragment: Fragment = homeFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        replaceFragment(homeFragment)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.content_frame, homeFragment)
            add(R.id.content_frame, historyFragment).hide(historyFragment)
            add(R.id.content_frame, accountFragment).hide(accountFragment)
        }.commit()

//        binding.bottomNav.setOnNavigationItemSelectedListener {menuItem ->
//            when (menuItem.itemId){
//                R.id.mnu_home -> {
//                    replaceFragment(homeFragment)
//                    true
//                }
//                R.id.mnu_history -> {
//                    replaceFragment(historyFragment)
//                    true
//                }
//                R.id.mnu_account -> {
//                    replaceFragment(accountFragment)
//                    true
//                }
//                else -> {
//                    false
//                }
//            }
//        }
        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottom_nav)
        bottomNavigation.add(CurvedBottomNavigation.Model(1,"Trang chủ", R.drawable.ic_home))
        bottomNavigation.add(CurvedBottomNavigation.Model(2, "Lịch sử",R.drawable.ic_history))
        bottomNavigation.add(CurvedBottomNavigation.Model(3, "Tài khoản",R.drawable.ic_account))

        bottomNavigation.show(1)
        bottomNavigation.setOnClickMenuListener {
            when (it.id){
                1 -> {
                    replaceFragment(homeFragment)
                }
                2 -> {
                    replaceFragment(historyFragment)
                }
                3 -> {
                    replaceFragment(accountFragment)
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.hide(currentFragment)
        transaction.show(fragment)
        transaction.commit()
        currentFragment = fragment
    }
}