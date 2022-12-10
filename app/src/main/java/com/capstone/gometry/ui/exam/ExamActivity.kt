package com.capstone.gometry.ui.exam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.OptionAdapter
import com.capstone.gometry.databinding.ActivityExamBinding
import com.capstone.gometry.model.Question
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.result_exam.ResultExamActivity
import com.capstone.gometry.utils.Constants
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

class ExamActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityExamBinding::inflate)
    private val questions: ArrayList<Question> = arrayListOf()
    private var questionIndex: Int = 0
    private var score: Int = 0
    private var examId: String = ""
    private var examPoint: Int = 0

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

        examId = intent.getStringExtra(Constants.EXTRA_EXAM_ID)!!
        examPoint = intent.getIntExtra(Constants.EXTRA_EXAM_POINT, 0)

        lifecycleScope.launchWhenResumed {
            launch {
                val database = Firebase.database.getReference(Constants.REF_QUESTIONS)
                database
                    .orderByChild(Constants.CHILD_LEVEL)
                    .equalTo(examId)
                    .addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val resQuestions = ArrayList<Question>()
                                for (data in snapshot.children) {
                                    resQuestions.add(data.getValue(Question::class.java)!!)
                                }
                                resQuestions.shuffle()
                                questions.addAll(resQuestions.take(Constants.MAX_QUESTION))

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
            score += examPoint / Constants.MAX_QUESTION

        optionAdapter.isChecked = true
        optionAdapter.updateView()
    }

    private fun handleFinishQuiz() {
        binding.btnAction.isEnabled = false

        lifecycleScope.launchWhenResumed {
            launch {
                val userId = Firebase.auth.currentUser?.uid.toString()
                val database = Firebase.database.getReference(Constants.REF_USERS).child(userId)

                database.get().addOnSuccessListener {
                    val user = it.getValue(User::class.java)!!
                    var point: Int = user.point ?: 0
                    point += score

                    val updatedData = mapOf("point" to point)
                    database
                        .updateChildren(updatedData)
                        .addOnSuccessListener {
                            Intent(this@ExamActivity, ResultExamActivity::class.java).also { intent ->
                                intent.putExtra(Constants.EXTRA_SCORE, score)
                                intent.putExtra(Constants.EXTRA_EXAM_POINT, examPoint)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
            }
        }
    }

    private fun handleAlertEmptySelectedOption() {
        AlertDialog.Builder(this@ExamActivity).apply {
            setTitle(getString(R.string.alert_empty_selected_option))
            setMessage(getString(R.string.message_empty_selected_option))
            setNegativeButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun handleAlertEmptyQuestions() {
        AlertDialog.Builder(this@ExamActivity).apply {
            setTitle(getString(R.string.alert_error))
            setMessage(getString(R.string.message_empty_quiz))
            setCancelable(false)
            setFinishOnTouchOutside(false)
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
                ivImage.setImageFromUrl(this@ExamActivity, url)
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
            layoutManager = LinearLayoutManager(this@ExamActivity)
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