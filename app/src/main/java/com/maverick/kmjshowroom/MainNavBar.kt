package com.maverick.kmjshowroom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.databinding.ActivityMainNavBarBinding

class MainNavBar : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNavBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_nav_bar)

        val db = UserDatabaseHelper(this)
        val currentUser = db.getUser()
        val userRole = currentUser?.role ?: ""

        val menu = navView.menu

        if (userRole.equals("admin", ignoreCase = true)) {
            menu.findItem(R.id.navigation_akun).isVisible = false
//            menu.findItem(R.id.navigation_report).isVisible = false
        }

        navView.setupWithNavController(navController)
    }
}
