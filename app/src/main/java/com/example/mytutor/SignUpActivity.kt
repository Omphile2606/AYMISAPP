package com.example.mytutor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytutor.databinding.ActivitySignUpBinding
import android.graphics.Color
import android.content.res.ColorStateList
import android.widget.Button
import androidx.core.content.ContextCompat

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.student.setOnClickListener {
            // Student selected
            binding.student.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.student.setTextColor(ContextCompat.getColor(this, R.color.btn))
            
            binding.tutor.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0EBFA"))
            binding.tutor.setTextColor(Color.BLACK)
        }

        binding.tutor.setOnClickListener {
            // Tutor selected
            binding.tutor.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.tutor.setTextColor(ContextCompat.getColor(this, R.color.btn))
            
            binding.student.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0EBFA"))
            binding.student.setTextColor(Color.BLACK)
        }
    }
}
