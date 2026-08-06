package com.example.mytutor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentTutorStatsBinding

class TutorStatsFragment : Fragment() {

    private var _binding: FragmentTutorStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorStatsBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTutorStats()
    }

    private fun loadTutorStats() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val tutorName = sharedPreferences.getString("userName", "") ?: ""

        val bookings = databaseHelper.getBookingsForTutor(tutorName)
        
        // Update summary text
        val gradedCount = bookings.count { !it.grade.isNullOrEmpty() }
        binding.tutorSummaryText.text = "You have graded $gradedCount out of ${bookings.size} sessions."

        // Update Overview Cards
        binding.totalSessionsTaught.text = bookings.size.toString()
        val estimatedEarnings = bookings.size * 50 // Assuming $50 per session
        binding.totalEarnings.text = "$$estimatedEarnings"

        // Subject Distribution Stats
        binding.subjectDistributionContainer.removeAllViews()
        if (bookings.isNotEmpty()) {
            val total = bookings.size.toFloat()
            val subjectsMap = bookings.groupBy { it.subject }

            for ((subject, sessions) in subjectsMap) {
                val itemView = layoutInflater.inflate(R.layout.item_subject_progress, binding.subjectDistributionContainer, false)
                val nameText = itemView.findViewById<TextView>(R.id.subjectNameText)
                val progressBar = itemView.findViewById<ProgressBar>(R.id.subjectProgressBar)
                
                nameText.text = subject
                progressBar.progress = ((sessions.size / total) * 100).toInt()
                binding.subjectDistributionContainer.addView(itemView)
            }
        } else {
            val emptyText = TextView(requireContext())
            emptyText.text = "No sessions taught yet."
            emptyText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            emptyText.setPadding(0, 40, 0, 40)
            binding.subjectDistributionContainer.addView(emptyText)
        }

        val adapter = SessionAdapter(requireContext(), bookings, isTutor = true)
        binding.tutorStatsListView.adapter = adapter

        binding.tutorStatsListView.setOnItemClickListener { _, _, position, _ ->
            val session = bookings[position]
            showGradeDialog(session)
        }
    }

    private fun showGradeDialog(session: DatabaseHelper.Booking) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Grade Student: ${session.studentName}")
        
        val input = EditText(requireContext())
        input.hint = "Enter Grade or Feedback (e.g. A, Excellent, 85%)"
        if (!session.grade.isNullOrEmpty()) {
            input.setText(session.grade)
        }
        
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val grade = input.text.toString().trim()
            if (grade.isNotEmpty()) {
                val result = databaseHelper.updateGrade(session.id, grade)
                if (result > 0) {
                    Toast.makeText(requireContext(), "Grade saved!", Toast.LENGTH_SHORT).show()
                    loadTutorStats() // Refresh list and summary
                } else {
                    Toast.makeText(requireContext(), "Failed to save grade", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
