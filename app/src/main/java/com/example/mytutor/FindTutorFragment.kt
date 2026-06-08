package com.example.mytutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentFindTutorBinding

class FindTutorFragment : Fragment() {

    private var _binding: FragmentFindTutorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFindTutorBinding.inflate(inflater, container, false)

        // Spinner data
        val subjects = arrayOf(
            "Select Subject",
            "Mathematics",
            "English",
            "Science",
            "History",
            "Geography",
            "Programming"
        )
        val Location = arrayOf(
            "Select Location"
        )

        // Create adapter
        val adapter1 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjects
        )
        val adapter2 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Location
        )

        adapter1.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter2.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        // Attach adapter to spinner
        binding.subjectSpinner.adapter = adapter1
        binding.locationSpinner.adapter = adapter2



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}