package com.example.mytutor

import android.content.Intent
import android.os.Bundle
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
            selectedRole = "Student"
            binding.student.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.student.setTextColor(ContextCompat.getColor(this, R.color.btn))
            
            binding.tutor.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0EBFA"))
            binding.tutor.setTextColor(Color.BLACK)
        }

        binding.tutor.setOnClickListener {
            selectedRole = "Tutor"
            binding.tutor.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.tutor.setTextColor(ContextCompat.getColor(this, R.color.btn))
            
            binding.student.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0EBFA"))
            binding.student.setTextColor(Color.BLACK)
        }

        binding.loginBtn.setOnClickListener {
            val fullName = binding.fullName.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = databaseHelper.addUser(fullName, email, password, selectedRole)
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
}
