package com.maverick.kmjshowroom.ui.akun

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ManageAkun
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.DetailAkunBinding
import kotlinx.coroutines.launch

class DetailAkunActivity : AppCompatActivity() {

    private lateinit var binding: DetailAkunBinding
    private var kodeUser: String? = null

    companion object {
        const val EXTRA_KODE_USER = "extra_kode_user"
    }

    private val editAkunLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadDetailAkun()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil kode_user dari intent
        kodeUser = intent.getStringExtra(EXTRA_KODE_USER)

        if (kodeUser.isNullOrEmpty()) {
            Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        loadDetailAkun()
        setupClickListeners()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Detail Akun"
        }
    }

    private fun loadDetailAkun() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                Log.d("DetailAkun", "Loading detail untuk: $kodeUser")
                val response = ApiClient.apiService.getManageAccDetail(kodeUser!!)

                if (response.isSuccessful && response.body()?.success == true) {
                    val akun = response.body()!!.data
                    Log.d("DetailAkun", "Data diterima: ${akun.full_name}")
                    displayAkunData(akun)
                } else {
                    Log.e("DetailAkun", "Response gagal: ${response.message()}")
                    Toast.makeText(this@DetailAkunActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                Log.e("DetailAkun", "Error: ${e.message}", e)
                Toast.makeText(this@DetailAkunActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun displayAkunData(akun: ManageAkun) {
        // Load foto profil - PAKE ApiClient.getImageUrl()
        val imageUrl = ApiClient.getImageUrl(akun.avatar_url)
        Glide.with(this)
            .load(imageUrl.ifEmpty { R.drawable.sample_profile })
            .placeholder(R.drawable.sample_profile)
            .error(R.drawable.sample_profile)
            .circleCrop()
            .into(binding.imgFotoProfil)

        // Split nama depan dan belakang
        val namaParts = akun.full_name.split(" ", limit = 2)
        binding.tvNamaDepan.text = namaParts.getOrNull(0) ?: "—"
        binding.tvNamaBelakang.text = namaParts.getOrNull(1) ?: "—"

        binding.tvUsername.text = akun.username ?: "—"
        binding.tvNoTelp.text = akun.no_telp ?: "—"
        binding.tvEmail.text = akun.email
        binding.tvAlamat.text = akun.alamat ?: "—"
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        binding.btnEditAkun.setOnClickListener {
            val intent = Intent(this, EditAkunActivity::class.java)
            intent.putExtra(EditAkunActivity.EXTRA_KODE_USER, kodeUser)
            editAkunLauncher.launch(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}