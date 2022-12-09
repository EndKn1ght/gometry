package com.capstone.gometry.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.adapter.LeaderboardAdapter
import com.capstone.gometry.databinding.FragmentLeaderboardBinding
import com.capstone.gometry.model.User
import com.capstone.gometry.utils.Constants.REF_USERS
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding
    private val listOfUser = ArrayList<User>()

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
        initialization()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initialization() {
        showLoading(true)

        lifecycleScope.launchWhenResumed {
            launch {
                val database = Firebase.database.getReference(REF_USERS)
                database.get()
                    .addOnSuccessListener { snapshot ->
                        for (data in snapshot.children) {
                            listOfUser.add(data.getValue(User::class.java)!!)
                        }
                        bindViews()
                        showLoading(false)
                    }
            }
        }
    }

    private fun bindViews() {
        listOfUser.sortByDescending { it.point }

        val leaderboardAdapter = LeaderboardAdapter()
        leaderboardAdapter.submitList(listOfUser)

        binding?.rvLeaderboard?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderboardAdapter
        }
    }

    private fun showLoading(state: Boolean) {
        binding?.rvLeaderboard?.setVisible(!state)
        if (!state) binding?.apply {
            shimmerLayout.stopShimmer()
            fakeRvLeaderboard.removeAllViews()
        }
    }
}