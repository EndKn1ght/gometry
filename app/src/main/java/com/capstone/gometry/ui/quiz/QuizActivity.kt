package com.capstone.gometry.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.OptionAdapter
import com.capstone.gometry.databinding.ActivityQuizBinding
import com.capstone.gometry.model.Question
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.result.ResultActivity
import com.capstone.gometry.ui.result.ResultActivity.Companion.EXTRA_SCORE
import com.capstone.gometry.utils.ViewExtensions.setImageFromUrl
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.capstone.gometry.utils.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

        val database = Firebase.database.reference
        database
            .child("questions")
            .orderByChild("geometryId")
            .equalTo(geometryId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val question = data.getValue(Question::class.java)
                        questions.add(question!!)

                        Log.d(TAG, question.toString())
                    }
                    setupQuestion()
                    showLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
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
        val userId = Firebase.auth.currentUser?.uid.toString()
        val database = Firebase.database.getReference("users").child(userId)
        database.get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            val mScore = score.roundToInt()

            val geometries = user?.geometries
            val mGeometries: ArrayList<String> = arrayListOf()
            geometries?.forEach { mGeometryId -> mGeometries.add(mGeometryId) }
            if (geometryId !in mGeometries) mGeometries.add(geometryId)

            val updatedData = mapOf(
                "score" to if (user?.score != null) user.score + mScore else mScore,
                "geometries" to mGeometries
            )

            database
                .updateChildren(updatedData)
                .addOnSuccessListener {
                    Intent(this@QuizActivity, ResultActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_SCORE, mScore)
                        intent.putExtra(EXTRA_GEOMETRY_ID, geometryId)
                        startActivity(intent)
                        finish()
                    }
                }
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
        optionAdapter = OptionAdapter(currentQuestion.options!!)
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

    companion object {
        const val EXTRA_GEOMETRY_ID = "extra_geometry_id"
        private const val TAG = "QUIZ_ACTIVITY"
    }
}