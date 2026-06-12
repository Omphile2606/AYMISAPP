package com.example.mytutor

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userRole = sharedPreferences.getString("userRole", "Student")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (userRole == "Tutor") {
                        replaceFragment(TutorHomeFragment())
                    } else {
                        replaceFragment(HomeFragment())
                    }
                    true
                }
                R.id.leason -> {
                    if (userRole == "Tutor") {
                        replaceFragment(TutorScheduleFragment())
                        true
                    } else {
                        replaceFragment(BookingFragment())
                        true
                    }
                }
                R.id.stats -> {
                    if (userRole == "Tutor") {
                         Toast.makeText(this, "Tutor Analytics coming soon", Toast.LENGTH_SHORT).show()
                         false
                    } else {
                        replaceFragment(StatsFragment())
                        true
                    }
                }
                R.id.profile -> {
                    // replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Set initial fragment based on role
        if (userRole == "Tutor") {
            replaceFragment(TutorHomeFragment())
        } else {
            replaceFragment(HomeFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
