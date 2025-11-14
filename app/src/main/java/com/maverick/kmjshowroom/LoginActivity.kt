package com.maverick.kmjshowroom

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.Model.LoginResponse
import com.maverick.kmjshowroom.Model.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_login)

        dbHelper = UserDatabaseHelper(this)

        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        val etUsername = findViewById<EditText>(R.id.txtusername)
        val etPassword = findViewById<EditText>(R.id.txtPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Username & Password tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                loginUser(username, password)
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        val requestBody = mapOf(
            "identifier" to username,
            "password" to password,
            "provider_type" to "local"
        )

        ApiClient.apiService.login(requestBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null && loginResponse.code == 200) {
                        val user = loginResponse.user

                        if (user != null) {
                            if (dbHelper.getUserCount() > 0) {
                                val db = dbHelper.writableDatabase
                                db.delete("users", null, null)
                                db.close()
                            }

                            dbHelper.insertUser(user)

                            Toast.makeText(
                                this@LoginActivity,
                                "${user.full_name}, ${loginResponse.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@LoginActivity, MainNavBar::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Data user tidak ditemukan dalam response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            loginResponse?.message ?: "Login gagal, periksa kembali akun Anda",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Response tidak valid dari server (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Gagal terhubung ke server: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
