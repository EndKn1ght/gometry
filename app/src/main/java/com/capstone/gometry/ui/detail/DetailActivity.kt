package com.capstone.gometry.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.gometry.databinding.ActivityDetailBinding
import com.capstone.gometry.model.Geometry
import com.capstone.gometry.utils.viewBinding
import java.io.Serializable

class DetailActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityDetailBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        val geometry = intent.serializable<Geometry>(EXTRA_DETAIL)
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}