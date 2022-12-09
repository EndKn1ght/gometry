package com.capstone.gometry.ui.quiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.OptionAdapter
import com.capstone.gometry.databinding.ActivityQuizBinding
import com.capstone.gometry.model.Question
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.result_quiz.ResultQuizActivity
import com.capstone.gometry.utils.Constants.CHILD_GEOMETRY_ID
import com.capstone.gometry.utils.Constants.EXTRA_ACHIEVEMENT_ID
import com.capstone.gometry.utils.Constants.EXTRA_GEOMETRY_ID
import com.capstone.gometry.utils.Constants.EXTRA_HAVE_PASSED_BEFORE
import com.capstone.gometry.utils.Constants.EXTRA_SCORE
import com.capstone.gometry.utils.Constants.LEVEL_KEY_BEGINNER
import com.capstone.gometry.utils.Constants.LEVEL_KEY_PROFICIENT
import com.capstone.gometry.utils.Constants.LEVEL_KEY_SKILLED
import com.capstone.gometry.utils.Constants.MAX_QUESTION
import com.capstone.gometry.utils.Constants.REF_QUESTIONS
import com.capstone.gometry.utils.Constants.REF_USERS
import com.capstone.gometry.utils.ViewExtensions.setImageFromUrl
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.capstone.gometry.utils.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class QuizActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityQuizBinding::inflate)
    private val questions: ArrayList<Question> = arrayListOf()
    private var questionIndex: Int = 0
    private var score: Float = 0F
    private var geometryId: String = ""

    private lateinit var optionAdapter: OptionAdapter
    private lateinit var currentQuestion: Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        initialization()
    }

    private fun initialization() {
        showLoading(true)

        geometryId = intent.getStringExtra(EXTRA_GEOMETRY_ID)!!

        lifecycleScope.launchWhenResumed {
            launch {
                val database = Firebase.database.getReference(REF_QUESTIONS)
                database
                    .orderByChild(CHILD_GEOMETRY_ID)
                    .equalTo(geometryId)
                    .addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val resQuestions = ArrayList<Question>()
                                for (data in snapshot.children) {
                                    resQuestions.add(data.getValue(Question::class.java)!!)
                                }
                                resQuestions.filter { it.level == LEVEL_KEY_BEGINNER }
                                resQuestions.shuffle()
                                questions.addAll(resQuestions.take(MAX_QUESTION))

                                setupQuestion()
                                showLoading(false)
                            } else handleAlertEmptyQuestions()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
    }

    private fun setupQuestion() {
        refreshQuestion()
        binding.apply {
            progressHorizontal.max = questions.size
            btnAction.setOnClickListener { handleAction() }
        }
    }

    private fun handleNextQuestion() {
        questionIndex += 1
        refreshQuestion()
        binding.btnAction.text = getString(R.string.check)
    }

    private fun handleCheckAnswer() {
        binding.apply {
            btnAction.text = getString(
                if (questionIndex < questions.size-1) R.string._continue
                else R.string.finish
            )
            progressHorizontal.progress += 1
        }

        if (optionAdapter.selectedOption == optionAdapter.answer)
            score += 100 / questions.size

        optionAdapter.isChecked = true
        optionAdapter.updateView()
    }

    private fun handleFinishQuiz() {
        val mScore = score.roundToInt()

        if (mScore == 100) {
            lifecycleScope.launchWhenResumed {
                launch {
                    val userId = Firebase.auth.currentUser?.uid.toString()
                    val database = Firebase.database.getReference(REF_USERS).child(userId)

                    database.get().addOnSuccessListener {
                        val user = it.getValue(User::class.java)!!

                        val geometries: ArrayList<String> = (user.geometries ?: arrayListOf()) as ArrayList<String>
                        val mGeometries: ArrayList<String> = arrayListOf()
                        mGeometries.addAll(geometries)
                        if (geometryId !in mGeometries) mGeometries.add(geometryId)

                        val achievement =
                            if ("cone" in mGeometries && "tube" in mGeometries && "triangular_prism" in mGeometries) LEVEL_KEY_PROFICIENT
                            else if ("triangular_pyramid" in mGeometries && "ball" in mGeometries) LEVEL_KEY_SKILLED
                            else if ("cube" in mGeometries && "beam" in mGeometries) LEVEL_KEY_BEGINNER
                            else ""
                        val achievements: ArrayList<String> = (user.achievements ?: arrayListOf()) as ArrayList<String>
                        val mAchievements = ArrayList<String>()
                        mAchievements.addAll(achievements)
                        if (achievement !in mAchievements && achievement.isNotEmpty()) mAchievements.add(achievement)

                        var point: Int = user.point ?: 0
                        if (geometryId !in geometries) point += mScore

                        val updatedData = mapOf(
                            "point" to point,
                            "geometries" to mGeometries,
                            "achievements" to mAchievements
                        )

                        database
                            .updateChildren(updatedData)
                            .addOnSuccessListener {
                                val extraAchievement =
                                    if (achievement !in achievements) achievement
                                    else ""

                                Intent(this@QuizActivity, ResultQuizActivity::class.java).also { intent ->
                                    intent.putExtra(EXTRA_SCORE, mScore)
                                    intent.putExtra(EXTRA_GEOMETRY_ID, geometryId)
                                    intent.putExtra(EXTRA_HAVE_PASSED_BEFORE, geometryId in geometries)
                                    intent.putExtra(EXTRA_ACHIEVEMENT_ID, extraAchievement)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }
                }
            }
        } else Intent(this@QuizActivity, ResultQuizActivity::class.java).also { intent ->
            intent.putExtra(EXTRA_SCORE, mScore)
            intent.putExtra(EXTRA_GEOMETRY_ID, geometryId)
            startActivity(intent)
            finish()
        }
    }

    private fun handleAlertEmptySelectedOption() {
        AlertDialog.Builder(this@QuizActivity).apply {
            setTitle(getString(R.string.alert_empty_selected_option))
            setMessage(getString(R.string.message_empty_selected_option))
            setNegativeButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun handleAlertEmptyQuestions() {
        AlertDialog.Builder(this@QuizActivity).apply {
            setTitle(getString(R.string.alert_error))
            setMessage(getString(R.string.message_empty_quiz))
            setNegativeButton(getString(R.string.ok)) { _, _ -> finish()}
            create()
            show()
        }
    }

    private fun handleAction() {
        if (!optionAdapter.isChecked && optionAdapter.selectedOption.isEmpty())
            handleAlertEmptySelectedOption()
        else if (optionAdapter.isChecked) {
            if (questionIndex < questions.size-1) handleNextQuestion()
            else handleFinishQuiz()
        } else handleCheckAnswer()
    }

    private fun refreshQuestion() {
        currentQuestion = questions[questionIndex]
        if (!currentQuestion.image.isNullOrEmpty()) {
            val url = String.format(BuildConfig.BASE_URL_STORAGE, "images%2F${currentQuestion.image}")
            binding.apply {
                ivImage.setImageFromUrl(this@QuizActivity, url)
                clImage.setVisible(true)
            }
        } else binding.clImage.setVisible(false)
        binding.tvQuestion.text = currentQuestion.question
        optionAdapter = OptionAdapter(currentQuestion.options!!.shuffled())
        optionAdapter.apply {
            selectedOption = ""
            isChecked = false
            answer = currentQuestion.answer!!
        }
        binding.rvOption.apply {
            layoutManager = LinearLayoutManager(this@QuizActivity)
            adapter = optionAdapter
        }
    }
    
    private fun showLoading(state: Boolean) {
        binding.apply {
            clProgressbar.setVisible(!state)
            clQuestion.setVisible(!state)
            rvOption.setVisible(!state)
            btnAction.setVisible(!state)
            progressCircular.setVisible(state)
        }
    }
}