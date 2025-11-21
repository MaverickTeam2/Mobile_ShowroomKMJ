package com.maverick.kmjshowroom.ui.car

import MobilDetailResponse
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kodeMobil = intent.getStringExtra("kode_mobil")

        binding.footerSavedetail.btnDraft.text = "Hapus"
        binding.footerSavedetail.btnNext.text = "Edit"

        loadDetailMobil()
        setupClickListeners()
    }

    private fun loadDetailMobil() {
        ApiClient.apiService.getMobilDetail(kodeMobil!!)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(call: Call<MobilDetailResponse>, response: Response<MobilDetailResponse>) {
                    val body = response.body()
                    if (body?.success == true) bindData(body)
                    else Toast.makeText(this@DetailMobilActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    Toast.makeText(this@DetailMobilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun bindData(data: MobilDetailResponse) {

        val m = data.mobil // agar lebih singkat

        // ---------- FOTO ----------
        binding.containerFotoMobil.removeAllViews()
        data.foto.forEach { item ->
            val img = ImageView(this)
            val size = resources.displayMetrics.widthPixels / 2 - 32
            img.layoutParams = LinearLayout.LayoutParams(size, size)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(this)
                .load(item.foto) // ✔ ini yang benar
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
        binding.txtUangMuka.text = "Rp ${m.uang_muka}"
        binding.txtAngsuran.text = "Rp ${m.angsuran}"
        binding.txtTenor.text = "${m.tenor} bulan"
        binding.txtStatusMobil.text = m.status

        // ---------- FITUR ----------
        binding.containerFitur.removeAllViews()
        if (data.fitur.isEmpty()) {
            binding.containerFitur.addView(TextView(this).apply { text = "Tidak ada fitur" })
        } else {
            data.fitur.forEach { id ->
                val t = TextView(this)
                t.text = "• Fitur #$id"
                binding.containerFitur.addView(t)
            }
        }
    }

    private fun setupClickListeners() {

        // EDIT
        binding.footerSavedetail.btnNext.setOnClickListener {
            val i = Intent(this, AddCarStep1Activity::class.java)
            i.putExtra("is_edit", true)
            i.putExtra("kode_mobil", kodeMobil)
            startActivity(i)
        }

        // HAPUS
        binding.footerSavedetail.btnDraft.setOnClickListener { deleteMobil() }
    }

    private fun deleteMobil() {
        ApiClient.apiService.deleteMobil(true, kodeMobil!!)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@DetailMobilActivity, "Mobil berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish()
                    } else Toast.makeText(this@DetailMobilActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@DetailMobilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
