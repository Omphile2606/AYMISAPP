package com.example.mytutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userEmail: String
    private var userRole: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "") ?: ""
        userRole = sharedPreferences.getString("userRole", "Student") ?: "Student"

        loadUserData()

        binding.btnSaveProfile.setOnClickListener {
            saveProfileChanges()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        return binding.root
    }

    private fun loadUserData() {
        if (userEmail.isEmpty()) return

        val user = databaseHelper.getUser(userEmail)
        user?.let {
            binding.etFullName.setText(it.fullName)
            binding.etEmail.setText(it.email)
            binding.etBio.setText(it.bio)
            
            // Password is not displayed for security, but we'll allow updating it
            // We can fetch the password directly if needed, but usually we just let them set a new one
            
            if (it.role == "Tutor") {
                binding.bioLayout.visibility = View.VISIBLE
            } else {
                binding.bioLayout.visibility = View.GONE
            }
        }
    }

    private fun saveProfileChanges() {
        val fullName = binding.etFullName.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(requireContext(), "Full name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // If password field is empty, keep existing password. 
        val finalPassword = if (password.isEmpty()) {
            databaseHelper.getUserPassword(userEmail) ?: ""
        } else {
            password
        }

        if (finalPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Error retrieving user security data", Toast.LENGTH_SHORT).show()
            return
        }

        val result = databaseHelper.updateUser(userEmail, fullName, bio, finalPassword)
        if (result > 0) {
            // Update SharedPreferences so the name change reflects everywhere
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("userName", fullName).apply()
            
            Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
