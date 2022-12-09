package com.capstone.gometry.ui.result_quiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.gometry.R
import com.capstone.gometry.databinding.ActivityResultQuizBinding
import com.capstone.gometry.model.Achievement
import com.capstone.gometry.ui.quiz.QuizActivity
import com.capstone.gometry.utils.Constants.EXTRA_ACHIEVEMENT_ID
import com.capstone.gometry.utils.Constants.EXTRA_GEOMETRY_ID
import com.capstone.gometry.utils.Constants.EXTRA_HAVE_PASSED_BEFORE
import com.capstone.gometry.utils.Constants.EXTRA_SCORE
import com.capstone.gometry.utils.ViewExtensions.setImageFromResource
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.capstone.gometry.utils.viewBinding

class ResultQuizActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityResultQuizBinding::inflate)
    private var geometryId: String = ""
    private var point: Int = 0
    private var havePassedBefore: Boolean = false
    private var mAchievementId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        geometryId = intent.getStringExtra(EXTRA_GEOMETRY_ID)!!
        point = intent.getIntExtra(EXTRA_SCORE, 0)
        havePassedBefore = intent.getBooleanExtra(EXTRA_HAVE_PASSED_BEFORE, false)
        mAchievementId = intent.getStringExtra(EXTRA_ACHIEVEMENT_ID) ?: ""

        determineView()
    }

    private fun determineView() {
        var achievement: Achievement? = null
        if (mAchievementId.isNotEmpty()) achievement = getAchievementData()

        when {
            havePassedBefore ->
                binding.apply {
                    ivImage.setImageFromResource(this@ResultQuizActivity, R.drawable.happy)
                    tvGreeting.text = getString(R.string.congrats_have_passed_before)
                    tvPoint.setVisible(false)
                    btnRetake.setVisible(false)
                }
            mAchievementId.isNotEmpty() && achievement != null ->
                binding.apply {
                    ivImage.setImageFromResource(this@ResultQuizActivity, achievement.medal)
                    tvGreeting.text = getString(R.string.congrats_success)
                    tvPoint.text = String.format(getString(R.string.you_get_medal), achievement.name)
                    btnRetake.setVisible(false)
                }
            point == 100 ->
                binding.apply {
                    ivImage.setImageFromResource(this@ResultQuizActivity, R.drawable.happy)
                    tvGreeting.text = getString(R.string.congrats_success)
                    tvPoint.text = String.format(getString(R.string.you_get_point), point)
                    btnRetake.setVisible(false)
                }
            else ->
                binding.apply {
                    ivImage.setImageFromResource(this@ResultQuizActivity, R.drawable.sad)
                    tvGreeting.text = getString(R.string.congrats_failure)
                    tvPoint.setVisible(false)
                }
        }

        binding.apply {
            btnBackToMaterial.setOnClickListener { finish() }
            btnRetake.setOnClickListener {
                Intent(this@ResultQuizActivity, QuizActivity::class.java).also {
                    it.putExtra(EXTRA_GEOMETRY_ID, geometryId)
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private fun getAchievementData(): Achievement? {
        val achievementId = resources.getStringArray(R.array.achievement_id)
        val achievementName = resources.getStringArray(R.array.achievement_name)
        val achievementMedal = resources.obtainTypedArray(R.array.achievement_medal)

        for (i in achievementId.indices) {
            if (achievementId[i] == mAchievementId)
                return Achievement(
                    achievementId[i],
                    achievementName[i],
                    achievementMedal.getResourceId(i, -1)
                )
        }
        achievementMedal.recycle()

        return null
    }
}