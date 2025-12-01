package com.maverick.kmjshowroom.ui.akun

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.GenericResponse
import com.maverick.kmjshowroom.databinding.TambahAkunBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAkunActivity : AppCompatActivity() {

    private lateinit var binding: TambahAkunBinding
    private var currentUserKode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TambahAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadCurrentUser()
        setupClickListeners()
    }

    private fun loadCurrentUser() {
        // Ambil user yang sedang login dari SQLite
        val dbHelper = com.maverick.kmjshowroom.Database.UserDatabaseHelper(this)
        val currentUser = dbHelper.getUser()
        currentUserKode = currentUser?.kode_user

        if (currentUserKode.isNullOrEmpty()) {
            Toast.makeText(this, "Session tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Tambah Akun"
        }
    }

    private fun setupClickListeners() {
        binding.footerSaveAkun.btnNext.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val namaDepan = binding.etNamaDepan.text.toString().trim()
        val namaBelakang = binding.etNamaBelakang.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val noTelp = binding.etNoTelp.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val konfirmasiPassword = binding.etKonfirmasiPassword.text.toString()
        val alamat = binding.etAlamat.text.toString().trim()

        // Validasi input
        when {
            namaDepan.isEmpty() -> {
                binding.etNamaDepan.error = "Nama depan wajib diisi"
                binding.etNamaDepan.requestFocus()
                return
            }
            namaBelakang.isEmpty() -> {
                binding.etNamaBelakang.error = "Nama belakang wajib diisi"
                binding.etNamaBelakang.requestFocus()
                return
            }
            username.isEmpty() -> {
                binding.etUsername.error = "Username wajib diisi"
                binding.etUsername.requestFocus()
                return
            }
            username.length < 4 -> {
                binding.etUsername.error = "Username minimal 4 karakter"
                binding.etUsername.requestFocus()
                return
            }
            noTelp.isEmpty() -> {
                binding.etNoTelp.error = "No telp wajib diisi"
                binding.etNoTelp.requestFocus()
                return
            }
            noTelp.length < 10 -> {
                binding.etNoTelp.error = "No telp tidak valid"
                binding.etNoTelp.requestFocus()
                return
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Email wajib diisi"
                binding.etEmail.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Format email tidak valid"
                binding.etEmail.requestFocus()
                return
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password wajib diisi"
                binding.etPassword.requestFocus()
                return
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password minimal 6 karakter"
                binding.etPassword.requestFocus()
                return
            }
            konfirmasiPassword.isEmpty() -> {
                binding.etKonfirmasiPassword.error = "Konfirmasi password wajib diisi"
                binding.etKonfirmasiPassword.requestFocus()
                return
            }
            password != konfirmasiPassword -> {
                binding.etKonfirmasiPassword.error = "Password tidak sama"
                binding.etKonfirmasiPassword.requestFocus()
                return
            }
            alamat.isEmpty() -> {
                binding.etAlamat.error = "Alamat wajib diisi"
                binding.etAlamat.requestFocus()
                return
            }
        }

        // Gabungkan nama depan dan belakang
        val fullName = "$namaDepan $namaBelakang"

        // Kirim data ke server
        createAccount(fullName, username, email, password, noTelp, alamat)
    }

    private fun createAccount(
        fullName: String,
        username: String,
        email: String,
        password: String,
        noTelp: String,
        alamat: String
    ) {
        showLoading(true)

        // Buat multipart body untuk kirim data
        val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val noTelpBody = noTelp.toRequestBody("text/plain".toMediaTypeOrNull())
        val alamatBody = alamat.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestBodyMap = mutableMapOf<String, okhttp3.RequestBody>()
        requestBodyMap["full_name"] = fullNameBody
        requestBodyMap["username"] = usernameBody
        requestBodyMap["email"] = emailBody
        requestBodyMap["password"] = passwordBody
        requestBodyMap["no_telp"] = noTelpBody
        requestBodyMap["alamat"] = alamatBody

        // Karena endpoint create_manage_acc.php menggunakan FormData/POST
        // kita bisa gunakan cara alternatif dengan retrofit
        lifecycleScope.launch {
            try {
                // Kirim sebagai JSON body dengan Map
                val body: Map<String, String> = mapOf(
                    "full_name" to fullName,
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "no_telp" to noTelp,
                    "alamat" to alamat
                )

                Log.d("AddAkun", "=== CREATE ACCOUNT ===")
                Log.d("AddAkun", "Request Body: $body")
                Log.d("AddAkun", "Full Name: $fullName")
                Log.d("AddAkun", "Username: $username")
                Log.d("AddAkun", "Email: $email")

                val response = ApiClient.apiService.createManageAccount(body)

                showLoading(false)

                // Log untuk debug
                Log.d("AddAkun", "Response Code: ${response.code()}")
                Log.d("AddAkun", "Response Body: ${response.body()}")
                Log.d("AddAkun", "Error Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@AddAkunActivity,
                        "Akun berhasil dibuat",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val message = response.body()?.message ?: "Gagal membuat akun"
                    Toast.makeText(this@AddAkunActivity, message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@AddAkunActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.footerSaveAkun.btnNext.isEnabled = !isLoading
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}