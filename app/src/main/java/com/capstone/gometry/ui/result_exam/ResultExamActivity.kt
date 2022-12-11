package com.capstone.gometry.ui.result_exam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.gometry.R
import com.capstone.gometry.databinding.ActivityResultExamBinding
import com.capstone.gometry.utils.Constants.EXTRA_EXAM_POINT
import com.capstone.gometry.utils.Constants.EXTRA_SCORE
import com.capstone.gometry.utils.Constants.MAX_QUESTION
import com.capstone.gometry.utils.ViewExtensions.setImageFromResource
import com.capstone.gometry.utils.viewBinding

class ResultExamActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityResultExamBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        determineView()
    }

    private fun determineView() {
        val examPoint = intent.getIntExtra(EXTRA_EXAM_POINT, 0)
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val minimumPoint = examPoint / MAX_QUESTION * (MAX_QUESTION-1)

        if (score >= minimumPoint) binding.apply {
            ivImage.setImageFromResource(this@ResultExamActivity, R.drawable.happy)
            tvGreeting.text = getString(R.string.congrats_succeed)
        } else binding.apply {
            ivImage.setImageFromResource(this@ResultExamActivity, R.drawable.sad)
            tvGreeting.text = getString(R.string.congrats_not_succeed)
        }

        binding.apply {
            tvPoint.text = String.format(getString(R.string.you_get_point), score)
            btnFinish.setOnClickListener { view ->
                view.isEnabled = false
                finish()
            }
        }
    }
}