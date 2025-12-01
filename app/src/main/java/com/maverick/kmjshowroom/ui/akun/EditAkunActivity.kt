package com.maverick.kmjshowroom.ui.akun

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ManageAkun
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.TambahAkunBinding
import kotlinx.coroutines.launch

class EditAkunActivity : AppCompatActivity() {

    private lateinit var binding: TambahAkunBinding
    private var kodeUser: String? = null
    private var currentAkun: ManageAkun? = null

    companion object {
        const val EXTRA_KODE_USER = "extra_kode_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TambahAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kodeUser = intent.getStringExtra(EXTRA_KODE_USER)

        if (kodeUser.isNullOrEmpty()) {
            Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupUI()
        loadAkunData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Akun"
        }
    }

    private fun setupUI() {
        // Sembunyikan field yang tidak bisa diedit
        binding.etUsername.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPassword.visibility = View.GONE
        binding.etKonfirmasiPassword.visibility = View.GONE

        // Sembunyikan label password juga
        binding.root.findViewById<View>(R.id.etPassword)?.let { passwordField ->
            // Cari TextView label di atasnya
            val parent = passwordField.parent as? ViewGroup
            parent?.let {
                val index = it.indexOfChild(passwordField)
                if (index > 0) {
                    it.getChildAt(index - 1)?.visibility = View.GONE
                }
            }
        }
    }

    private fun loadAkunData() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val response = ApiClient.apiService.getManageAccDetail(kodeUser!!)

                if (response.isSuccessful && response.body()?.success == true) {
                    currentAkun = response.body()!!.data
                    displayAkunData(currentAkun!!)
                } else {
                    Toast.makeText(
                        this@EditAkunActivity,
                        "Gagal memuat data",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@EditAkunActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun displayAkunData(akun: ManageAkun) {
        // Split nama
        val namaParts = akun.full_name.split(" ", limit = 2)
        binding.etNamaDepan.setText(namaParts.getOrNull(0) ?: "")
        binding.etNamaBelakang.setText(namaParts.getOrNull(1) ?: "")

        // Data lainnya
        binding.etUsername.setText(akun.username ?: "")
        binding.etEmail.setText(akun.email)
        binding.etNoTelp.setText(akun.no_telp ?: "")
        binding.etAlamat.setText(akun.alamat ?: "")
    }

    private fun setupClickListeners() {
        binding.footerSaveAkun.btnNext.setOnClickListener {
            validateAndUpdate()
        }
    }

    private fun validateAndUpdate() {
        val namaDepan = binding.etNamaDepan.text.toString().trim()
        val namaBelakang = binding.etNamaBelakang.text.toString().trim()
        val noTelp = binding.etNoTelp.text.toString().trim()
        val alamat = binding.etAlamat.text.toString().trim()

        // Validasi
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
            alamat.isEmpty() -> {
                binding.etAlamat.error = "Alamat wajib diisi"
                binding.etAlamat.requestFocus()
                return
            }
        }

        val fullName = "$namaDepan $namaBelakang"
        updateAkun(fullName, noTelp, alamat)
    }

    private fun updateAkun(fullName: String, noTelp: String, alamat: String) {
        lifecycleScope.launch {
            try {
                showLoading(true)

                // Kirim sebagai JSON body
                val body = mapOf(
                    "kode_user" to kodeUser!!,
                    "full_name" to fullName,
                    "no_telp" to noTelp,
                    "alamat" to alamat
                )

                Log.d("EditAkun", "=== UPDATE ACCOUNT ===")
                Log.d("EditAkun", "Kode User: $kodeUser")
                Log.d("EditAkun", "Request Body: $body")

                val response = ApiClient.apiService.updateManageAccount(body)

                Log.d("EditAkun", "Response Code: ${response.code()}")
                Log.d("EditAkun", "Response Message: ${response.message()}")
                Log.d("EditAkun", "Response Success: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    Log.d("EditAkun", "Response Body: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditAkun", "Error Body: $errorBody")
                }

                showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@EditAkunActivity,
                        "Akun berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val message = response.body()?.message ?: "Gagal memperbarui akun"
                    Log.e("EditAkun", "Error Message: $message")
                    Toast.makeText(this@EditAkunActivity, message, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                showLoading(false)
                Log.e("EditAkun", "Exception Type: ${e.javaClass.simpleName}")
                Log.e("EditAkun", "Exception Message: ${e.message}", e)
                e.printStackTrace()
                Toast.makeText(
                    this@EditAkunActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
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