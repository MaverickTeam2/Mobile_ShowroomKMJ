package com.maverick.kmjshowroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.Model.RegisterResponse
import com.maverick.kmjshowroom.Model.UserData
import com.maverick.kmjshowroom.utils.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var loading: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_register)
        loading = LoadingDialog(this)

        dbHelper = UserDatabaseHelper(this)

        val fullName = findViewById<EditText>(R.id.txt_full_name)
        val email = findViewById<EditText>(R.id.txtemail)
        val username = findViewById<EditText>(R.id.txtusername)
        val password = findViewById<EditText>(R.id.txtPass)
        val confirmPassword = findViewById<EditText>(R.id.txtKonfirmPass)
        val btnRegister = findViewById<Button>(R.id.btnLogin)

        btnRegister.setOnClickListener {
            val name = fullName.text.toString().trim()
            val mail = email.text.toString().trim()
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()
            val cpass = confirmPassword.text.toString().trim()

            if (name.isEmpty() || mail.isEmpty() || user.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            } else if (pass != cpass) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, mail, user, pass)
            }
        }
    }

    private fun registerUser(fullName: String, email: String, username: String, password: String) {

        loading.show("Mendaftarkan akun...")

        val requestBody = mapOf(
            "role" to "owner",
            "username" to username,
            "email" to email,
            "password" to password,
            "full_name" to fullName,
            "provider_type" to "local"
        )

        ApiClient.apiService.registerUser(requestBody).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                loading.dismiss()
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null && body.code == 200) {

                        val userData = UserData(
                            kode_user = body.kode_user ?: "",
                            username = username,
                            email = email,
                            no_telp = null,
                            full_name = fullName,
                            alamat = null,
                            role = "owner",
                            avatar_url = body.avatar_url,
                            provider_type = "local",
                            status = 1
                        )
                        dbHelper.insertUser(userData)

                        body.token?.let { saveToken(it) }

                        Toast.makeText(
                            this@RegisterActivity,
                            "Registrasi berhasil!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@RegisterActivity, MainNavBar::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            body?.message ?: "Gagal registrasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(this@RegisterActivity, "Response error", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun saveToken(token: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPrefs = EncryptedSharedPreferences.create(
            "auth_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        with(sharedPrefs.edit()) {
            putString("token", token)
            apply()
        }
    }
}
