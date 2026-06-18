package com.example.mytutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytutor.databinding.ActivityLoginBinding
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (databaseHelper.checkUser(email, password)) {
                val user = databaseHelper.getUser(email)
                if (user != null) {
                    if (user.status == "Pending" && user.role != "Admin") {
                        Toast.makeText(this, "Your account is pending approval.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    val editor = sharedPreferences.edit()
                    editor.putString("userName", user.fullName)
                    editor.putString("userEmail", user.email)
                    editor.putString("userRole", user.role)
                    editor.putBoolean("isLoggedIn", true)
                    
                    // Increment streak every login
                    val currentStreak = sharedPreferences.getInt("streak", 0)
                    editor.putInt("streak", currentStreak + 1)
                    
                    // Increment hours by 30 mins (0.5 hours) every login
                    val currentHours = sharedPreferences.getFloat("loginHours", 0f)
                    editor.putFloat("loginHours", currentHours + 0.5f)

                    editor.apply()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}