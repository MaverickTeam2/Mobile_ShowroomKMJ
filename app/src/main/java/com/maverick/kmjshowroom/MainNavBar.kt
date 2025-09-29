package com.maverick.kmjshowroom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maverick.kmjshowroom.databinding.ActivityMainNavBarBinding

class MainNavBar : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNavBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil BottomNavigationView dari binding
        val navView: BottomNavigationView = binding.navView

        // Ambil NavController dari FragmentContainerView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_nav_bar)

        // Hubungkan BottomNavigationView dengan NavController
        navView.setupWithNavController(navController)
    }
}
