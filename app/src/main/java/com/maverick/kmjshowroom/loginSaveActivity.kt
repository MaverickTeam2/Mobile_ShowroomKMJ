package com.maverick.kmjshowroom

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class loginSaveActivity : AppCompatActivity() {

    private lateinit var pref: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_login_save)

        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        pref = getSharedPreferences("KMJ_PREF", MODE_PRIVATE)

        val btnLoginPopup: Button = findViewById(R.id.LoginPopup)
        val btnFingerprint: ImageButton = findViewById(R.id.btnFingerprint)

        btnLoginPopup.setOnClickListener { showLoginPopup() }

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                btnFingerprint.visibility = View.VISIBLE
                btnFingerprint.setOnClickListener { checkBiometric() }
            }
            else -> {
                btnFingerprint.visibility = View.GONE
            }
        }
    }

    private fun showLoginPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        val tvUsername = dialogView.findViewById<TextView>(R.id.SavedUsername)
        val etPassword = dialogView.findViewById<EditText>(R.id.txtpassw)
        val changeAccount = dialogView.findViewById<TextView>(R.id.ChangeAccount)

        val savedUsername = pref.getString("USERNAME", "")
        tvUsername.text = savedUsername ?: ""
        tvUsername.isEnabled = false

        changeAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        val btnLoginDialog = dialogView.findViewById<Button>(R.id.LoginPopup)
        btnLoginDialog.setOnClickListener {
            val username = tvUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginWithApi(username, password)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Isi password untuk login", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun loginWithApi(username: String, password: String) {
        ApiClient.apiService.login(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        Toast.makeText(this@loginSaveActivity, "Login sukses", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@loginSaveActivity, MainNavBar::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@loginSaveActivity, body?.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@loginSaveActivity, "Response gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@loginSaveActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            startActivity(Intent(this@loginSaveActivity, MainNavBar::class.java))
                            finish()
                        }
                    })

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Login dengan Fingerprint")
                    .setSubtitle("Gunakan biometrik untuk login")
                    .setNegativeButtonText("Batal")
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(this, "Device tidak punya sensor biometrik", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(this, "Sensor biometrik tidak tersedia", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(this, "Belum ada sidik jari/face ID yang terdaftar", Toast.LENGTH_SHORT).show()
        }
    }
}
