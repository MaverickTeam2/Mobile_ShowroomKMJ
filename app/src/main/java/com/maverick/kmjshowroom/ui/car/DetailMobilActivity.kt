package com.maverick.kmjshowroom.ui.car

import MobilDetailResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.GenericResponse
import com.maverick.kmjshowroom.databinding.DetailMobilBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailMobilActivity : AppCompatActivity() {

    private lateinit var binding: DetailMobilBinding
    private var kodeMobil: String? = null
    private var loadingCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kodeMobil = intent.getStringExtra("kode_mobil")

        binding.footerSavedetail.btnDraft.visibility = View.VISIBLE
        binding.footerSavedetail.btnDraft.text = "Hapus"
        binding.footerSavedetail.btnNext.text = "Edit"

        loadDetailMobil()
        setupClickListeners()
    }

    private fun loadDetailMobil() {
        showLoading(true)

        ApiClient.apiService.getMobilDetail(kodeMobil!!)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(call: Call<MobilDetailResponse>, response: Response<MobilDetailResponse>) {
                    showLoading(false)

                    val body = response.body()

                    // 🔍 DEBUG LOG
                    Log.d("DetailMobil", "Response code: ${response.code()}")
                    Log.d("DetailMobil", "Response successful: ${response.isSuccessful}")
                    Log.d("DetailMobil", "Body: $body")
                    Log.d("DetailMobil", "Body code: ${body?.code}")

                    // ✅ FIX: Ganti body?.success dengan body?.code == 200
                    if (response.isSuccessful && body != null && body.code == 200) {
                        bindData(body)
                    } else {
                        val errorMsg = when {
                            body == null -> "Response body kosong"
                            body.code != 200 -> "Server error: code ${body.code}"
                            else -> "Gagal mengambil data"
                        }
                        Toast.makeText(this@DetailMobilActivity, errorMsg, Toast.LENGTH_SHORT).show()
                        Log.e("DetailMobil", "Error: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@DetailMobilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DetailMobil", "Network error: ${t.message}", t)
                }
            })
    }

    private fun bindData(data: MobilDetailResponse) {

        val m = data.mobil

        // ---------- FOTO ----------
        binding.containerFotoMobil.removeAllViews()
        data.foto.forEach { item ->
            val img = ImageView(this)
            val size = resources.displayMetrics.widthPixels / 2 - 32
            img.layoutParams = LinearLayout.LayoutParams(size, size)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(this)
                .load(item.foto)
                .into(img)
            binding.containerFotoMobil.addView(img)
        }

        // ---------- INFORMASI ----------
        binding.txtNamaMobil.text = m.nama_mobil
        binding.txtTahunMobil.text = m.tahun_mobil.toString()
        binding.txtJarakTempuh.text = "${m.jarak_tempuh} km"
        binding.txtWarnaExterior.text = m.warna_exterior
        binding.txtWarnaInterior.text = m.warna_interior
        binding.txtSistemPenggerak.text = m.sistem_penggerak
        binding.txtJenisKendaraan.text = m.jenis_kendaraan
        binding.txtBahanBakar.text = m.tipe_bahan_bakar
        binding.txtHargaFull.text = "Rp ${m.full_prize}"
        binding.txtUangMuka.text = "Rp ${m.uang_muka}"
        binding.txtAngsuran.text = "Rp ${m.angsuran}"
        binding.txtTenor.text = "${m.tenor} bulan"
        binding.txtStatusMobil.text = m.status

        // ---------- FITUR ----------
        binding.containerFitur.removeAllViews()

        if (data.fitur.isEmpty()) {
            binding.containerFitur.addView(TextView(this).apply { text = "Tidak ada fitur" })
        } else {
            data.fitur.forEach { fitur ->
                val t = TextView(this)
                t.text = "• ${fitur.nama}"
                binding.containerFitur.addView(t)
            }
        }
    }

    private fun setupClickListeners() {

        // === KONFIRMASI EDIT ===
        binding.footerSavedetail.btnNext.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Edit Mobil")
                .setMessage("Apakah Anda yakin ingin mengedit data mobil ini?")
                .setPositiveButton("Ya") { _, _ ->
                    val i = Intent(this, AddCarStep1Activity::class.java)
                    i.putExtra("is_edit", true)
                    i.putExtra("kode_mobil", kodeMobil)
                    startActivity(i)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // === KONFIRMASI HAPUS ===
        binding.footerSavedetail.btnDraft.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Mobil")
                .setMessage("Mobil akan dihapus secara permanen. Lanjutkan?")
                .setPositiveButton("Hapus") { _, _ ->
                    deleteMobil()  // tetap pakai fungsi yang sama
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun deleteMobil() {
        showLoading(true)
        Toast.makeText(this, "Menghapus mobil...", Toast.LENGTH_SHORT).show()

        ApiClient.apiService.deleteMobil(true, kodeMobil!!)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    showLoading(false)

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@DetailMobilActivity, "Mobil berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish() // kembali ke CarFragment
                    } else {
                        Toast.makeText(this@DetailMobilActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@DetailMobilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // fungsi untuk menyembunyikan loading
    private fun showLoading(show: Boolean) {
        if (show) loadingCount++ else loadingCount--

        if (loadingCount > 0) {
            binding.detailMobil.visibility = View.GONE
            binding.loadingProgress.visibility = View.VISIBLE
            binding.loadingProgress.playAnimation()
        } else {
            binding.loadingProgress.pauseAnimation()
            binding.loadingProgress.visibility = View.GONE

            binding.detailMobil.visibility = View.VISIBLE
        }
    }
}