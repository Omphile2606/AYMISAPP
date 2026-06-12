package com.example.mytutor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentTutorScheduleBinding
import java.util.Calendar

class TutorScheduleFragment : Fragment() {

    private var _binding: FragmentTutorScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorScheduleBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        // Set default date
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

        binding.scheduleCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        // Setup Student Dropdown
        val students = databaseHelper.getAllStudents()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, students)
        binding.studentAutoComplete.setAdapter(adapter)

        binding.btnScheduleLesson.setOnClickListener {
            val studentName = binding.studentAutoComplete.text.toString().trim()
            val selectedSubjectId = binding.tutorSubjectGroup.checkedRadioButtonId

            if (studentName.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a student", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedSubjectId == -1) {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val radioButton = binding.root.findViewById<RadioButton>(selectedSubjectId)
            val subject = radioButton.text.toString()

            // Retrieve tutor name from SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val tutorName = sharedPreferences.getString("userName", "Anonymous Tutor") ?: "Anonymous Tutor"

            val result = databaseHelper.addBooking(selectedDate, subject, tutorName, studentName)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Lesson scheduled for $studentName!", Toast.LENGTH_SHORT).show()
                // Clear inputs
                binding.studentAutoComplete.setText("")
                binding.tutorSubjectGroup.clearCheck()
            } else {
                Toast.makeText(requireContext(), "Scheduling Failed", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
