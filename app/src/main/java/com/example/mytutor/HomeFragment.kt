package com.example.mytutor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mytutor.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentHomeBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.findtutor.setOnClickListener {
            // 1. Navigate to FindTutorFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, FindTutorFragment())
                .addToBackStack(null) // Recommended so the back button works
                .commit()

            // 2. Optional: If you want to update the Bottom Nav highlight WITHOUT
            // triggering the Activity's fragment replacement again, you'd usually
            // just change the UI. But if you WANT to go to the "Leason" tab:
            // activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.leason
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


