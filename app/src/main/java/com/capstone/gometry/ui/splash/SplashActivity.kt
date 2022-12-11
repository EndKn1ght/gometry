package com.capstone.gometry.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstone.gometry.ui.auth.AuthActivity
import com.capstone.gometry.ui.main.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Firebase.database.setPersistenceEnabled(true)

        val authenticated = Firebase.auth.currentUser != null
        determineScreenDirection(authenticated)
    }

    private fun determineScreenDirection(authenticated: Boolean) {
        Intent(this@SplashActivity, if (authenticated) MainActivity::class.java else AuthActivity::class.java)
            .also {
                startActivity(it)
                finish()
            }
    }
}