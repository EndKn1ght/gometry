package com.capstone.gometry.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.gometry.R
import com.capstone.gometry.databinding.FragmentProfileBinding
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.auth.AuthActivity
import com.capstone.gometry.utils.ViewExtensions.setImageFromUrl
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

        val database = Firebase.database.reference
        database.child("users").child(firebaseAuth?.uid!!).get()
            .addOnSuccessListener {
                bindViews(it.getValue(User::class.java)!!)
                showLoading(false)
            }
    }

    private fun bindViews(user: User) {
        binding?.apply {
            ivPhoto.setImageFromUrl(requireContext(), user.photoUrl!!)
            tvName.text = user.displayName!!
            tvEmail.text = user.email!!
            btnSignOut.setOnClickListener { handleSignOut() }
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

    private fun showLoading(state: Boolean) {
        binding?.apply {
            clImage.setVisible(!state)
            tvName.setVisible(!state)
            tvEmail.setVisible(!state)
        }

        if (!state) binding?.clShimmerTop?.removeAllViews()
    }
}