package com.example.mytutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentBookingBinding
import java.util.Calendar

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        
        // Set default date
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        binding.btnBookNow.setOnClickListener {
            val selectedSubjectId = binding.subjectGroup.checkedRadioButtonId
            val tutorName = binding.tutorNameInput.text.toString().trim()

            if (selectedSubjectId == -1) {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tutorName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a tutor name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val radioButton = binding.root.findViewById<RadioButton>(selectedSubjectId)
            val subject = radioButton.text.toString()

            // For now, using a placeholder for student name. 
            // In a real app, you'd get this from the logged-in user session.
            val studentName = "Current Student" 

            val result = databaseHelper.addBooking(selectedDate, subject, tutorName, studentName)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Booking Successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Booking Failed", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
