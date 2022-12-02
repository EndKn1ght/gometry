package com.capstone.gometry.ui.quiz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.R
import com.capstone.gometry.adapter.OptionAdapter
import com.capstone.gometry.databinding.ActivityQuizBinding
import com.capstone.gometry.model.Question
import com.capstone.gometry.utils.viewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class QuizActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityQuizBinding::inflate)
    private val questions: ArrayList<Question> = arrayListOf()
    private var questionIndex: Int = 0
    private var score: Int = 0

    private lateinit var optionAdapter: OptionAdapter
    private lateinit var currentQuestion: Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        testRealtimeDatabase()
    }

    private fun testRealtimeDatabase() {
        val database = Firebase.database.reference
        database.child("questions").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val question = data.getValue(Question::class.java)
                    questions.add(question!!)
                }
                setupQuestion()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun setupQuestion() {
        currentQuestion = questions[questionIndex]

        binding.apply {
            tvQuestion.text = currentQuestion.question
            progressHorizontal.max = questions.size
        }

        optionAdapter = OptionAdapter(currentQuestion.options!!)
        optionAdapter.answer = currentQuestion.answer!!
        binding.rvOption.apply {
            layoutManager = LinearLayoutManager(this@QuizActivity)
            adapter = optionAdapter
        }

        binding.btnCheckAnswer.setOnClickListener { handleCheckAnswer()  }
    }

    private fun handleCheckAnswer() {
        if (optionAdapter.isChecked) {
            // Handle next question
            if (questionIndex < questions.size-1) {
                questionIndex += 1
                val currentQuestion = questions[questionIndex]

                optionAdapter = OptionAdapter(currentQuestion.options!!)
                optionAdapter.apply {
                    selectedOption = ""
                    isChecked = false
                    answer = currentQuestion.answer!!
                }

                binding.apply {
                    tvQuestion.text = currentQuestion.question
                    rvOption.adapter = optionAdapter
                    btnCheckAnswer.text = getString(R.string.check)
                }
            } else {
                Toast.makeText(this@QuizActivity, "Score $score", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.apply {
                btnCheckAnswer.text = getString(
                    if (questionIndex < questions.size-1) R.string._continue
                    else R.string.finish
                )
                progressHorizontal.progress += 1
            }

            if (optionAdapter.selectedOption == optionAdapter.answer)
                score += 100 / questions.size

            optionAdapter.isChecked = true
            optionAdapter.updateView()

            Log.d(TAG, score.toString())
        }
    }

    companion object {
        private const val TAG = "QUIZ_ACTIVITY"
    }
}