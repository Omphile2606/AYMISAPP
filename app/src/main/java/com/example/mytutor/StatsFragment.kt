package com.example.mytutor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
    }

    private fun loadStats() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val studentName = sharedPreferences.getString("userName", "Anonymous Student") ?: "Anonymous Student"

        val bookings = databaseHelper.getBookingsForStudent(studentName)
        
        // Total Sessions
        binding.totalSessionsCount.text = bookings.size.toString()
        
        // Hours Spent: Incremented by 0.5h every login
        val loginHours = sharedPreferences.getFloat("loginHours", 0f)
        binding.hoursSpent.text = String.format("%.1f", loginHours)

        // Subject Breakdown
        val total = if (bookings.isNotEmpty()) bookings.size.toFloat() else 1f
        
        val mathCount = bookings.count { it.subject.contains("Math", ignoreCase = true) }
        val physicsCount = bookings.count { it.subject.contains("Physics", ignoreCase = true) }
        val biologyCount = bookings.count { it.subject.contains("Biology", ignoreCase = true) }

        binding.mathProgress.progress = ((mathCount / total) * 100).toInt()
        binding.physicsProgress.progress = ((physicsCount / total) * 100).toInt()
        binding.biologyProgress.progress = ((biologyCount / total) * 100).toInt()
        
        // Streak: Incremented every login
        val streak = sharedPreferences.getInt("streak", 0)
        binding.streakCount.text = "$streak Days"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
