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
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.Model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class loginSaveActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        dbHelper = UserDatabaseHelper(this)

        setContentView(R.layout.activity_login_save)

        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

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

        val user = dbHelper.getUser()
        if (user != null) {
            tvUsername.text = user["username"] ?: ""
            tvUsername.isEnabled = false
        } else {
            Toast.makeText(this, "Tidak ada data user tersimpan", Toast.LENGTH_SHORT).show()
            return
        }

        changeAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val params = window?.attributes
        params?.gravity = Gravity.BOTTOM
        window?.attributes = params

        window?.attributes?.windowAnimations = R.style.DialogSlideAnimation

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
        val requestBody = mapOf(
            "identifier" to username,
            "password" to password,
            "provider_type" to "local"
        )

        ApiClient.apiService.login(requestBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        when (body.code) {
                            200 -> {
                                val user = body.user
                                if (user != null) {
                                    if (dbHelper.getUserCount() > 0) {
                                        val db = dbHelper.writableDatabase
                                        db.delete("users", null, null)
                                        db.close()
                                    }

                                    dbHelper.insertUser(user)

                                    Toast.makeText(
                                        this@loginSaveActivity,
                                        "${user.full_name}, ${body.message ?: "Login berhasil"}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@loginSaveActivity, MainNavBar::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@loginSaveActivity,
                                        "Data user tidak ditemukan dalam response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            401 -> {
                                Toast.makeText(
                                    this@loginSaveActivity,
                                    body.message ?: "Username atau password salah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            500 -> {
                                Toast.makeText(
                                    this@loginSaveActivity,
                                    body.message ?: "Terjadi kesalahan di server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    this@loginSaveActivity,
                                    body.message ?: "Login gagal (kode: ${body.code})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@loginSaveActivity,
                            "Response kosong dari server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@loginSaveActivity,
                        "Gagal login: HTTP ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@loginSaveActivity,
                    "Tidak bisa terhubung ke server: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun checkBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(
                    this, executor,
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
                Toast.makeText(this, "Device tidak punya sensor biometrik", Toast.LENGTH_SHORT)
                    .show()

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(this, "Sensor biometrik tidak tersedia", Toast.LENGTH_SHORT).show()

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(
                    this,
                    "Belum ada sidik jari/face ID yang terdaftar",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}
