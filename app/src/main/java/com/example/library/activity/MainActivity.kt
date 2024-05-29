package com.example.library.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.library.R
import com.example.library.databinding.ActivityMainBinding
import com.example.library.activity.fragment.AccountFragment
import com.example.library.activity.fragment.HistoryFragment
import com.example.library.activity.fragment.HomeFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import java.util.*

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

        //add các fragment vào fragment manager
        supportFragmentManager.beginTransaction().apply {
            add(R.id.content_frame, homeFragment)
            add(R.id.content_frame, historyFragment).hide(historyFragment)
            add(R.id.content_frame, accountFragment).hide(accountFragment)
        }.commit()

        //ánh xạ view
        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottom_nav)
        //add các bottom nav item vào bottomNav
        bottomNavigation.add(CurvedBottomNavigation.Model(1,"Trang chủ", R.drawable.ic_home))
        bottomNavigation.add(CurvedBottomNavigation.Model(2, "Lịch sử",R.drawable.ic_history))
        bottomNavigation.add(CurvedBottomNavigation.Model(3, "Tài khoản",R.drawable.ic_account))

        bottomNavigation.show(1)

        //lắng nghe sự kiện khi click
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

    override fun onRestart() {
        super.onRestart()
        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val isLogin = sharedPreferences.getBoolean("isLogin", false)
        if (isLogin){
            val intent = Intent(this, MainActivity::class.java)
            sharedPreferences.edit().putBoolean("isLogin", false).apply()
            startActivity(intent)
            finish()
        }
    }
}