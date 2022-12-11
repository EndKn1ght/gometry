package com.capstone.gometry.ui.leaderboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.adapter.LeaderboardAdapter
import com.capstone.gometry.databinding.FragmentLeaderboardBinding
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.other_profile.OtherProfileActivity
import com.capstone.gometry.utils.Constants.EXTRA_USER
import com.capstone.gometry.utils.Constants.REF_USERS
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding

    private lateinit var leaderboardAdapter: LeaderboardAdapter

    private val launchPostActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { getDataUsers { leaderboardAdapter.submitList(it) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderboardBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataUsers { initialization(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initialization(listUser: List<User>) {
        leaderboardAdapter = LeaderboardAdapter()
        leaderboardAdapter.submitList(listUser)

        binding?.rvLeaderboard?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderboardAdapter
        }

        leaderboardAdapter.setOnStartActivityCallback(object: LeaderboardAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(user: User) {
                Intent(requireContext(), OtherProfileActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_USER, user)
                    launchPostActivity.launch(intent)
                }
            }
        })
    }

    private fun getDataUsers(handleAction: (List<User> ) -> Unit) {
        showLoading(true)

        lifecycleScope.launchWhenResumed {
            launch {
                val database = Firebase.database.getReference(REF_USERS)
                database.get()
                    .addOnSuccessListener { snapshot ->
                        val listOfUser = ArrayList<User>()
                        for (data in snapshot.children) {
                            listOfUser.add(data.getValue(User::class.java)!!)
                        }
                        listOfUser.sortByDescending { it.point }
                        handleAction(listOfUser)
                        showLoading(false)
                    }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding?.apply {
            rvLeaderboard.setVisible(!state)
            fakeRvLeaderboard.setVisible(state)
            if (state) shimmerLayout.startShimmer() else shimmerLayout.stopShimmer()
        }
    }
}