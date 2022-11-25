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
import com.capstone.gometry.ui.auth.AuthActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

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
        binding?.btnSignOut?.setOnClickListener { handleSignOut() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun handleSignOut() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.sign_out)
            setMessage(getString(R.string.message_confirmation))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                Firebase.auth.signOut()
                Intent(requireContext(), AuthActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finish()
                }
            }
            setNegativeButton("No") { _, _ -> }
            create()
            show()
        }
    }
}