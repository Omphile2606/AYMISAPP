package com.example.mytutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentTutorHomeBinding

class TutorHomeFragment : Fragment() {

    private var _binding: FragmentTutorHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val tutorName = sharedPreferences.getString("userName", "Tutor") ?: "Tutor"
        
        binding.welcomeText.text = "Welcome Tutor, \n$tutorName! 👋"

        // Load sessions
        loadTutorSessions(tutorName)

        binding.profileIcon.setOnClickListener {
            showLogoutMenu(it)
        }

        updateStats(tutorName)
    }

    private fun showLogoutMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.add("Logout")
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.title == "Logout") {
                logout()
                true
            } else {
                false
            }
        }
        popupMenu.show()
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadTutorSessions(tutorName: String) {
        val bookings = databaseHelper.getBookingsForTutor(tutorName)
        // Set isTutor = true to show student names in the list
        val adapter = SessionAdapter(requireContext(), bookings, isTutor = true)
        binding.tutorSessionListView.adapter = adapter
    }

    private fun updateStats(tutorName: String) {
        val bookings = databaseHelper.getBookingsForTutor(tutorName)
        val uniqueStudents = bookings.map { it.studentName }.distinct().size
        binding.totalStudentsCount.text = uniqueStudents.toString()
        binding.hoursTaughtCount.text = bookings.size.toDouble().toString()
        
        // Populate the grading progress stat
        val gradedCount = bookings.count { !it.grade.isNullOrEmpty() }
        binding.gradedSessionsCount.text = "$gradedCount/${bookings.size} sessions graded"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
