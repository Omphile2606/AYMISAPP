package com.example.mytutor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytutor.databinding.ActivitySignUpBinding
import android.graphics.Color
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedRole = "Student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        databaseHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.student.setOnClickListener {
            updateRoleSelection("Student")
        }

        binding.tutor.setOnClickListener {
            updateRoleSelection("Tutor")
        }

        binding.admin.setOnClickListener {
            updateRoleSelection("Admin")
        }

        binding.loginBtn.setOnClickListener {
            val fullName = binding.fullName.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val bio = binding.bio.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedRole == "Tutor" && bio.isEmpty()) {
                Toast.makeText(this, "Please provide a description/bio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val finalBio = if (selectedRole == "Tutor") bio else ""
            val status = if (selectedRole == "Admin") "Approved" else "Approved"

            val result = databaseHelper.addUser(fullName, email, password, selectedRole, finalBio, status)
            if (result != -1L) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registration failed (email might exist)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRoleSelection(role: String) {
        selectedRole = role
        
        // Reset all buttons
        val defaultBg = ColorStateList.valueOf(Color.parseColor("#F0EBFA"))
        val selectedBg = ColorStateList.valueOf(Color.WHITE)
        val defaultText = Color.BLACK
        val selectedText = ContextCompat.getColor(this, R.color.btn)

        binding.student.backgroundTintList = defaultBg
        binding.student.setTextColor(defaultText)
        binding.tutor.backgroundTintList = defaultBg
        binding.tutor.setTextColor(defaultText)
        binding.admin.backgroundTintList = defaultBg
        binding.admin.setTextColor(defaultText)

        // Highlight selected
        when (role) {
            "Student" -> {
                binding.student.backgroundTintList = selectedBg
                binding.student.setTextColor(selectedText)
                binding.bioLayout.visibility = View.GONE
            }
            "Tutor" -> {
                binding.tutor.backgroundTintList = selectedBg
                binding.tutor.setTextColor(selectedText)
                binding.bioLayout.visibility = View.VISIBLE
            }
            "Admin" -> {
                binding.admin.backgroundTintList = selectedBg
                binding.admin.setTextColor(selectedText)
                binding.bioLayout.visibility = View.GONE
            }
        }
    }
}
