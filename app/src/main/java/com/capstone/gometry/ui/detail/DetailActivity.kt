package com.capstone.gometry.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.gometry.databinding.ActivityDetailBinding
import com.capstone.gometry.model.Geometry
import com.capstone.gometry.ui.quiz.QuizActivity
import com.capstone.gometry.ui.quiz.QuizActivity.Companion.EXTRA_GEOMETRY_ID
import com.capstone.gometry.utils.viewBinding
import java.io.Serializable

class DetailActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityDetailBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        initialization()
    }

    private fun initialization() {
        val geometry = intent.serializable<Geometry>(EXTRA_DETAIL)!!

        binding.apply {
            tvName.text = geometry.name
            btnClose.setOnClickListener { finish() }
            btnPlayAr.setOnClickListener { handlePlayAR(geometry.model3dUrl) }
            btnExam.setOnClickListener {
                Intent(this@DetailActivity, QuizActivity::class.java).also {
                    it.putExtra(EXTRA_GEOMETRY_ID, geometry.id)
                    startActivity(it)
                }
            }
        }
    }

    private fun handlePlayAR(model3dUrl: String) {
        val sceneViewer = Intent(Intent.ACTION_VIEW)
        val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
            .buildUpon()
            .appendQueryParameter("file", model3dUrl)
            .appendQueryParameter("mode", "ar_preferred")
            .build()
        sceneViewer.data = intentUri
        sceneViewer.setPackage("com.google.ar.core")
        startActivity(sceneViewer)
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}