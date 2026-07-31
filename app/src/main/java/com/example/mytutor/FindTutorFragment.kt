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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytutor.databinding.FragmentFindTutorBinding
import java.util.Calendar

class FindTutorFragment : Fragment() {

    private var _binding: FragmentFindTutorBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tutorAdapter: TutorRecyclerAdapter
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFindTutorBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        setupDropdowns()
        setupDatePicker()
        setupRecyclerView()

        binding.filterButton.setOnClickListener {
            applyFilter()
        }

        return binding.root
    }

    private fun setupDropdowns() {
        val subjects = arrayOf("Select Subject", "Mathematics", "Science", "English", "History", "Geography", "Programming")
        val locations = arrayOf("Select Location", "Cape Town", "Johannesburg", "Durban", "Online")

        binding.subjectAutoComplete.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjects))
        binding.locationAutoComplete.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locations))
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.dateAutoComplete.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedDate = "$d/${m + 1}/$y"
                binding.dateAutoComplete.setText(selectedDate)
            }, year, month, day).show()
        }
    }

    private fun setupRecyclerView() {
        tutorAdapter = TutorRecyclerAdapter(emptyList()) { tutor ->
            bookTutor(tutor)
        }
        binding.timeTable.layoutManager = LinearLayoutManager(requireContext())
        binding.timeTable.adapter = tutorAdapter
    }

    private fun applyFilter() {
        val subject = binding.subjectAutoComplete.text.toString()
        val location = binding.locationAutoComplete.text.toString()
        
        val tutors = databaseHelper.searchTutors(subject, location)
        tutorAdapter.updateList(tutors)
        
        if (tutors.isEmpty()) {
            Toast.makeText(requireContext(), "No tutors found matching your criteria", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bookTutor(tutor: DatabaseHelper.User) {
        if (selectedDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a date first", Toast.LENGTH_SHORT).show()
            return
        }

        val subject = binding.subjectAutoComplete.text.toString()
        if (subject.isEmpty() || subject == "Select Subject") {
            Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val studentName = sharedPreferences.getString("userName", "Student") ?: "Student"

        val result = databaseHelper.addBooking(selectedDate, subject, tutor.fullName, studentName)
        if (result != -1L) {
            Toast.makeText(requireContext(), "Booking successful for ${tutor.fullName}", Toast.LENGTH_LONG).show()
            
            // Navigate back to Home
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, HomeFragment())
                .commit()
            
            // Update bottom navigation selection
            (activity as? HomeActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.home
        } else {
            Toast.makeText(requireContext(), "Booking failed", Toast.LENGTH_SHORT).show()
        }
    }    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}