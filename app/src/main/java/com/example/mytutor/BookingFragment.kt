package com.example.mytutor

import android.content.Context
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
            val selectedSubjectId = binding.subjectGroup.checkedRadioButtonId
            val tutorName = binding.tutorAutoComplete.text.toString().trim()

            if (selectedSubjectId == -1) {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tutorName.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a tutor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val radioButton = binding.root.findViewById<RadioButton>(selectedSubjectId)
            val subject = radioButton.text.toString()

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
