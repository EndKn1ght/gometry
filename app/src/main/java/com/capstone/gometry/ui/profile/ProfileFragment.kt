package com.capstone.gometry.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.R
import com.capstone.gometry.adapter.AchievementAdapter
import com.capstone.gometry.databinding.FragmentProfileBinding
import com.capstone.gometry.model.Achievement
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.auth.AuthActivity
import com.capstone.gometry.utils.Constants.REF_USERS
import com.capstone.gometry.utils.ViewExtensions.setImageFromUrl
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _firebaseAuth = null
    }

    private fun initialization() {
        _firebaseAuth = Firebase.auth
        getUserData()
    }

    private fun getUserData() {
        showLoading(true)

        lifecycleScope.launchWhenResumed {
            launch {
                val database = Firebase.database.reference
                database.child(REF_USERS).child(firebaseAuth?.uid!!).get()
                    .addOnSuccessListener {
                        bindViews(it.getValue(User::class.java)!!)
                        showLoading(false)
                    }
            }
        }
    }

    private fun bindViews(user: User) {
        binding?.apply {
            ivPhoto.setImageFromUrl(requireContext(), user.photoUrl!!)
            tvName.text = user.displayName
            tvEmail.text = user.email
            tvTotalPoint.text = if (user.point == null) "0" else user.point.toString()
            tvTotalMedal.text = if (user.achievements == null) "0" else "${user.achievements.size}"
            btnSignOut.setOnClickListener { handleSignOut() }
        }

        if (user.achievements != null) {
            binding?.clEmptyAchievement?.setVisible(false)

            val achievementAdapter = AchievementAdapter()
            achievementAdapter.submitList(generateAchievement(user.achievements))

            binding?.rvAchievement?.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = achievementAdapter
            }
        } else binding?.apply {
            rvAchievement.setVisible(false)
            clEmptyAchievement.setVisible(true)
        }
    }

    private fun handleSignOut() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.sign_out)
            setMessage(getString(R.string.message_confirmation))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                firebaseAuth?.signOut()
                Intent(requireContext(), AuthActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finish()
                }
            }
            setNegativeButton(getString(R.string.no)) { _, _ -> }
            create()
            show()
        }
    }

    private fun generateAchievement(userAchievements: List<String>): List<Achievement> {
        val achievementId = resources.getStringArray(R.array.achievement_id)
        val achievementName = resources.getStringArray(R.array.achievement_name)
        val achievementMedal = resources.obtainTypedArray(R.array.achievement_medal)

        val listAchievement = ArrayList<Achievement>()
        for (i in achievementId.indices) {
            val achievement = Achievement(
                achievementId[i],
                achievementName[i],
                achievementMedal.getResourceId(i, -1)
            )
            if (achievementId[i] in userAchievements)
                listAchievement.add(achievement)
        }
        achievementMedal.recycle()

        return listAchievement
    }

    private fun showLoading(state: Boolean) {
        binding?.clProfile?.setVisible(!state)
        if (!state) binding?.apply {
                shimmerLayout.stopShimmer()
                clShimmer.removeAllViews()
            }
    }
}