package com.maverick.kmjshowroom

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("KMJ_PREF", MODE_PRIVATE)
            val savedUsername = sharedPref.getString("USERNAME", null)

            // Jika username tersimpan, ke loginSaveActivity
            val intent = if (savedUsername.isNullOrEmpty()) {
                Intent(this, LoginActivity::class.java)
            } else {
                Intent(this, loginSaveActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 2000)
    }
}
