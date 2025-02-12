package com.example.sosotalot

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sosotalot.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

//        if (!isUserLoggedIn()) {
//            // 如果未登录，导航到 LoginFragment
//            navController.navigate(R.id.loginFragment)
//            // 隐藏底部导航栏
//            binding.navView.visibility = View.GONE
//        } else {
//            // 显示底部导航栏
            binding.navView.visibility = View.VISIBLE
//        }

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            // 根据目的地 ID 显示或隐藏导航栏
//            binding.navView.visibility = if (destination.id == R.id.loginFragment) View.GONE else View.VISIBLE
//        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_history,
                R.id.navigation_shop,
                R.id.navigation_bug,
                R.id.navigation_my
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // 为底部导航栏设置监听器
        binding.navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    // 首先尝试弹出回退栈中的实例
                    navController.popBackStack(R.id.tarotMasterSelectionFragment, true)
                    // 然后导航到 TarotMasterSelectionFragment
                    navController.navigate(R.id.tarotMasterSelectionFragment)
                    true // 表示事件已处理
                }
                R.id.navigation_shop -> {
                    navController.navigate(R.id.navigation_shop)
                    true
                }
                R.id.navigation_bug -> {
                    navController.navigate(R.id.navigation_bug)
                    true
                }
                R.id.navigation_history -> {
                    navController.navigate(R.id.navigation_history)
                    true
                }
                R.id.navigation_my -> {
                    navController.navigate(R.id.navigation_my)
                    true
                }
                else -> false
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        // 这里可以添加检查逻辑，比如从 SharedPreferences 检查登录状态
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}

