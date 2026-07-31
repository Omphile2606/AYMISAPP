package com.example.mytutor

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        selectedDate = "$day/${month + 1}/$year"
        binding.dateAutoComplete.setText(selectedDate)

        binding.dateAutoComplete.setOnClickListener {
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.dateAutoComplete.setText(selectedDate)
            }, year, month, day).show()
        }

        // Setup Subject Dropdown
        val subjects = resources.getStringArray(R.array.subjects_array)
        val subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjects)
        binding.subjectAutoComplete.setAdapter(subjectAdapter)

        // Setup Tutor Dropdown with Bio
        val tutors = databaseHelper.getAllTutorsWithBio()
        val adapter = TutorAdapter(requireContext(), tutors)
        binding.tutorAutoComplete.setAdapter(adapter)

        binding.tutorAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selectedTutor = parent.getItemAtPosition(position) as DatabaseHelper.User
            binding.tutorAutoComplete.setText(selectedTutor.fullName, false)
            binding.tutorBioText.text = selectedTutor.bio
            binding.tutorBioText.visibility = View.VISIBLE
        }

        binding.btnBookNow.setOnClickListener {
            val subject = binding.subjectAutoComplete.text.toString().trim()
            val tutorName = binding.tutorAutoComplete.text.toString().trim()

            if (subject.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tutorName.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a tutor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve student name from SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val studentName = sharedPreferences.getString("userName", "Anonymous Student") ?: "Anonymous Student"

            val result = databaseHelper.addBooking(selectedDate, subject, tutorName, studentName)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Booking Successful for $tutorName!", Toast.LENGTH_SHORT).show()
                
                // Navigate back to HomeFragment to show the new booking
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, HomeFragment())
                    .commit()
                
                // Update bottom navigation selection if possible
                (activity as? HomeActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.home
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
