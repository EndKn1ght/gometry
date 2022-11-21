package com.example.capstonegometry.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.capstonegometry.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        determineScreenDirection()
    }

    private fun determineScreenDirection() {
        Intent(this@SplashActivity, MainActivity::class.java)
            .also {
                startActivity(it)
                finish()
            }
    }
}