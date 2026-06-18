package com.example.mytutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytutor.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var pendingAdapter: PendingUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadPendingUsers()
        loadAllSessions()
    }

    private fun setupRecyclerView() {
        pendingAdapter = PendingUserAdapter(emptyList()) { user ->
            approveUser(user)
        }
        binding.pendingUsersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pendingUsersRecyclerView.adapter = pendingAdapter
    }

    private fun loadPendingUsers() {
        val pendingUsers = databaseHelper.getPendingUsers()
        pendingAdapter.updateData(pendingUsers)
    }

    private fun approveUser(user: DatabaseHelper.User) {
        val result = databaseHelper.approveUser(user.email)
        if (result > 0) {
            Toast.makeText(requireContext(), "User ${user.fullName} approved!", Toast.LENGTH_SHORT).show()
            loadPendingUsers()
        } else {
            Toast.makeText(requireContext(), "Failed to approve user", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAllSessions() {
        val sessions = databaseHelper.getAllSessions()
        val adapter = SessionAdapter(requireContext(), sessions, isTutor = false) // isTutor false shows tutor name
        binding.allSessionsListView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
