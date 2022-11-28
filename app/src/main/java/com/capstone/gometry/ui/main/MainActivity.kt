package com.capstone.gometry.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.capstone.gometry.R
import com.capstone.gometry.databinding.ActivityMainBinding
import com.capstone.gometry.utils.viewBinding

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        val hostFragment =
            supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
        val navigationController = hostFragment.navController
        val bottomNavigation = binding.bottomNavigation
        bottomNavigation.setupWithNavController(navigationController)
    }
}