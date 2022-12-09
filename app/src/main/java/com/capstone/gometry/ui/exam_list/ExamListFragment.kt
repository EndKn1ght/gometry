package com.capstone.gometry.ui.exam_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.R
import com.capstone.gometry.adapter.ExamAdapter
import com.capstone.gometry.databinding.FragmentExamListBinding
import com.capstone.gometry.model.Exam
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.exam.ExamActivity
import com.capstone.gometry.utils.Constants.EXTRA_EXAM_ID
import com.capstone.gometry.utils.Constants.EXTRA_EXAM_POINT
import com.capstone.gometry.utils.Constants.REF_USERS
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ExamListFragment : Fragment() {
    private var _binding: FragmentExamListBinding? = null
    private val binding get() = _binding
    private lateinit var examAdapter: ExamAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExamListBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
    }

    private fun getUserData() {
        lifecycleScope.launchWhenResumed {
            launch {
                val currentUser = Firebase.auth.currentUser!!
                val database = Firebase.database.getReference(REF_USERS).child(currentUser.uid)
                database.get()
                    .addOnSuccessListener {
                        val user = it.getValue(User::class.java)!!
                        val geometries = (user.geometries ?: arrayListOf()) as ArrayList<String>
                        bindViews(generateListOfExam(geometries))
                    }
            }
        }
    }

    private fun bindViews(listOfExam: List<Exam>) {
        examAdapter = ExamAdapter()
        examAdapter.submitList(listOfExam)

        binding?.rvExam?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = examAdapter
        }

        examAdapter.setOnStartActivityCallback(object: ExamAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(exam: Exam) {
                if (exam.locked) showAlertLockedGeometry()
                else showExam(exam.id, exam.point)
            }
        })

        binding?.apply {
            rvExam.setVisible(true)
            shimmerLayout.stopShimmer()
            fakeRvExam.removeAllViews()
        }
    }

    private fun showExam(examId: String, examPoint: Int) {
        Intent(requireContext(), ExamActivity::class.java).also {
            it.putExtra(EXTRA_EXAM_ID, examId)
            it.putExtra(EXTRA_EXAM_POINT, examPoint)
            startActivity(it)
        }
    }

    private fun generateListOfExam(geometries: List<String>): List<Exam> {
        val examId = resources.getStringArray(R.array.exam_id)
        val examIcon = resources.obtainTypedArray(R.array.exam_icon)
        val examLevel = resources.getStringArray(R.array.exam_level)
        val examPoint = resources.getStringArray(R.array.exam_point)
        val geometryId = resources.getStringArray(R.array.geometry_id)
        val locked = geometries.size == geometryId.size && geometries.toSet() == geometryId.toSet()

        val listOfExam: ArrayList<Exam> = arrayListOf()
        for (i in examId.indices) {
            val exam = Exam(
                id = examId[i],
                icon = examIcon.getResourceId(i, -1),
                level = examLevel[i],
                point = examPoint[i].toInt(),
                locked = !locked
            )
            listOfExam.add(exam)
        }
        examIcon.recycle()

        return listOfExam
    }

    private fun showAlertLockedGeometry() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.locked))
            setMessage(getString(R.string.message_locked_exam))
            setNegativeButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }
}