package com.capstone.gometry.ui.result

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.gometry.R
import com.capstone.gometry.databinding.ActivityResultBinding
import com.capstone.gometry.ui.quiz.QuizActivity
import com.capstone.gometry.ui.quiz.QuizActivity.Companion.EXTRA_GEOMETRY_ID
import com.capstone.gometry.utils.ViewExtensions.setImageFromUrl
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.capstone.gometry.utils.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResultActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityResultBinding::inflate)
    private var score: Int = 0
    private var geometryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        score = intent.getIntExtra(EXTRA_SCORE, 0)
        geometryId = intent.getStringExtra(EXTRA_GEOMETRY_ID)!!

        determineView(score >= 80)
    }

    private fun determineView(passed: Boolean) {
        val user = Firebase.auth.currentUser
        binding.ivPhoto.setImageFromUrl(this, user?.photoUrl.toString())

        if (passed) binding.apply {
            tvGreeting.text = getString(R.string.congrats_success)
            tvTotalScore.text = String.format(getString(R.string.you_get_point), score)
            btnRetake.setVisible(false)
        } else binding.apply {
            tvGreeting.text = getString(R.string.congrats_failure)
            tvTotalScore.setVisible(false)
            btnRetake.setVisible(true)
        }

        binding.apply {
            btnBackToMaterial.setOnClickListener { finish() }
            btnRetake.setOnClickListener {
                Intent(this@ResultActivity, QuizActivity::class.java).also {
                    it.putExtra(EXTRA_GEOMETRY_ID, geometryId)
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    companion object {
        const val EXTRA_SCORE = "extra_score"
    }
}