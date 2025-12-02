package com.maverick.kmjshowroom.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.maverick.kmjshowroom.R

class AddTrnActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_trn_2)

        val closeButton = findViewById<ImageView>(R.id.icon_close)
        closeButton.setOnClickListener {
            finish()
        }

        val txtNama = findViewById<TextInputEditText>(R.id.txtusername)
        val txtTlp = findViewById<TextInputEditText>(R.id.txttlp)
        val cbKTP = findViewById<CheckBox>(R.id.cbKTP)
        val cbKK = findViewById<CheckBox>(R.id.cbKK)
        val cbRekening = findViewById<CheckBox>(R.id.cbRekening)
        val btnNext = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_next)
        btnNext.setText("Selanjutnya")

        val selectedCar = intent.getStringExtra("selectedCar") ?: ""
        val kodeMobil = intent.getStringExtra("kodeMobil") ?: ""
        val hargaMobil = intent.getStringExtra("hargaMobil") ?: "0"
        val dpMobil = intent.getStringExtra("dpMobil") ?: ""

        btnNext.setOnClickListener {
            val nama = txtNama.text.toString().trim()
            val telp = txtTlp.text.toString().trim()

            if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(telp)) {
                Toast.makeText(this, "Nama dan Nomor Telepon wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!telp.matches(Regex("^[0-9]+$"))) {
                Toast.makeText(this, "Nomor Telepon hanya boleh angka!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dokumenDipilih = listOf(cbKTP.isChecked, cbKK.isChecked, cbRekening.isChecked).any { it }
            if (!dokumenDipilih) {
                Toast.makeText(this, "Minimal pilih 1 dokumen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val jaminanList = mutableListOf<String>()
            if (cbKTP.isChecked) jaminanList.add("KTP")
            if (cbKK.isChecked) jaminanList.add("KK")
            if (cbRekening.isChecked) jaminanList.add("Buku Tabungan")

            val intent = Intent(this, AddTrnActivity3::class.java).apply {
                putExtra("selectedCar", selectedCar)
                putExtra("kodeMobil", kodeMobil)
                putExtra("hargaMobil", hargaMobil)
                putExtra("dpMobil", dpMobil)
                putExtra("namaPembeli", nama)
                putExtra("noTelp", telp)
                putExtra("jaminan", jaminanList.joinToString(", "))
            }
            startActivity(intent)
        }
    }
}