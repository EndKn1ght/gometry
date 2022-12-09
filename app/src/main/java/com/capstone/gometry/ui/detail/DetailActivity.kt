package com.capstone.gometry.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.gometry.databinding.ActivityDetailBinding
import com.capstone.gometry.model.Geometry
import com.capstone.gometry.ui.quiz.QuizActivity
import com.capstone.gometry.utils.Constants.EXTRA_DETAIL
import com.capstone.gometry.utils.Constants.EXTRA_GEOMETRY_ID
import com.capstone.gometry.utils.HandleIntent.handlePlayAR
import com.capstone.gometry.utils.ViewExtensions.setImageFromResource
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
            ivImage.setImageFromResource(this@DetailActivity, geometry.image)
            tvTheory.text = geometry.theory
            ivSurfaceAreaFormula.setBackgroundResource(geometry.surfaceArea)
            ivVolumeFormula.setBackgroundResource(geometry.volume)
            tvExampleQuestion.text = geometry.exampleQuestion
            ivExampleAnswer.setBackgroundResource(geometry.exampleAnswer)
            btnClose.setOnClickListener { finish() }
            btnPlayAr.setOnClickListener { handlePlayAR(this@DetailActivity, geometry.model3dUrl) }
            btnExam.setOnClickListener {
                Intent(this@DetailActivity, QuizActivity::class.java).also {
                    it.putExtra(EXTRA_GEOMETRY_ID, geometry.id)
                    startActivity(it)
                }
            }
        }
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}