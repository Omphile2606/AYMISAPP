package com.example.mytutor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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

        binding.btnGenerateReport.setOnClickListener {
            generateReport()
        }
    }

    private fun loadStats() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val studentName = sharedPreferences.getString("userName", "Anonymous Student") ?: "Anonymous Student"

        val bookings = databaseHelper.getBookingsForStudent(studentName)
        
        // Total Sessions
        binding.totalSessionsCount.text = bookings.size.toString()
        
        // Hours Spent
        val loginHours = sharedPreferences.getFloat("loginHours", 0f)
        binding.hoursSpent.text = String.format("%.1f", loginHours)

        // Subject Breakdown
        val total = if (bookings.isNotEmpty()) bookings.size.toFloat() else 1f
        
        val mathCount = bookings.count { it.subject.contains("Math", ignoreCase = true) }
        val scienceCount = bookings.count { it.subject.contains("Science", ignoreCase = true) }
        val englishCount = bookings.count { it.subject.contains("English", ignoreCase = true) }

        binding.mathProgress.progress = ((mathCount / total) * 100).toInt()
        binding.scienceProgress.progress = ((scienceCount / total) * 100).toInt()
        binding.englishProgress.progress = ((englishCount / total) * 100).toInt()
        
        // Load Grades
        val adapter = SessionAdapter(requireContext(), bookings, isTutor = false)
        binding.gradesListView.adapter = adapter
    }

    private fun generateReport() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val studentName = sharedPreferences.getString("userName", "Student") ?: "Student"
        val bookings = databaseHelper.getBookingsForStudent(studentName)

        if (bookings.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Academic Report")
                .setMessage("No session data available to generate a report.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val report = StringBuilder()
        report.append("Academic Progress Report for $studentName\n")
        report.append("-------------------------------------------\n\n")
        
        val totalSessions = bookings.size
        val attendedSessions = bookings.count { it.attendance == "PRESENT" }
        val attendanceRate = if (totalSessions > 0) (attendedSessions.toFloat() / totalSessions * 100).toInt() else 0
        
        report.append("OVERALL ATTENDANCE: $attendanceRate%\n")
        report.append("Total Sessions: $totalSessions\n")
        report.append("Sessions Attended: $attendedSessions\n\n")
        
        report.append("ACADEMIC PERFORMANCE:\n")
        bookings.forEach { booking ->
            val grade = booking.grade ?: "Not Graded"
            val attendance = booking.attendance ?: "N/A"
            report.append("• ${booking.date} - ${booking.subject}: Grade: $grade | Status: $attendance\n")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Student Academic Report")
            .setMessage(report.toString())
            .setPositiveButton("Close", null)
            .setNeutralButton("Share") { _, _ ->
                // Simple share functionality
                val shareIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, report.toString())
                    type = "text/plain"
                }
                startActivity(android.content.Intent.createChooser(shareIntent, "Send Report To"))
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
