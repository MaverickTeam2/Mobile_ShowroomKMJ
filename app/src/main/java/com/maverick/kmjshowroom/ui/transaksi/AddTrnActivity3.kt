package com.maverick.kmjshowroom.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.MainNavBar
import com.maverick.kmjshowroom.Model.CreateTransaksiResponse
import com.maverick.kmjshowroom.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class AddTrnActivity3 : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_trn_3)

        dbHelper = UserDatabaseHelper(this)

        val spinnerJenisPembayaran = findViewById<AutoCompleteTextView>(R.id.spinnerJenisPembayaran)
        val layoutFullPay = findViewById<LinearLayout>(R.id.layoutFullPay)
        val layoutKredit = findViewById<LinearLayout>(R.id.layoutKredit)
        val tvFullPrize = findViewById<TextInputEditText>(R.id.tvFullPrize)
        val tvFullPrizeKredit = findViewById<TextInputEditText>(R.id.tvFullPrizeKredit)
        val etDealFull = findViewById<TextInputEditText>(R.id.etDealFull)
        val etDealKredit = findViewById<TextInputEditText>(R.id.etDeal)
        val btnFinish = findViewById<Button>(R.id.btnFinish)
        val btnSaveDraft = findViewById<Button>(R.id.btnSaveDraft)

        if (btnFinish == null || btnSaveDraft == null) {
            Toast.makeText(this, "Error: Button tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<ImageView>(R.id.icon_close)?.setOnClickListener {
            finish()
        }

        layoutFullPay?.visibility = View.GONE
        layoutKredit?.visibility = View.GONE

        val carName = intent.getStringExtra("selectedCar") ?: "Mobil tidak diketahui"
        val kodeMobil = intent.getStringExtra("kodeMobil") ?: ""
        val hargaMobilStr = intent.getStringExtra("hargaMobil") ?: "0"
        val namaPembeli = intent.getStringExtra("namaPembeli") ?: ""
        val noTelp = intent.getStringExtra("noTelp") ?: ""
        val jaminanStr = intent.getStringExtra("jaminan") ?: ""

        val jaminanKtp = if (jaminanStr.contains("KTP", ignoreCase = true)) 1 else 0
        val jaminanKk = if (jaminanStr.contains("KK", ignoreCase = true)) 1 else 0
        val jaminanRekening = if (jaminanStr.contains("Buku Tabungan", ignoreCase = true)) 1 else 0

        val hargaMobil = hargaMobilStr.toDoubleOrNull() ?: 0.0
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val hargaTeks = formatter.format(hargaMobil)

        val currentUser = dbHelper.getUser()
        val kodeUser = currentUser?.kode_user ?: "US001"

        val jenisPembayaran = listOf("Full Payment", "Kredit")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jenisPembayaran)
        spinnerJenisPembayaran?.setAdapter(adapter)

        spinnerJenisPembayaran?.setOnItemClickListener { _, _, position, _ ->
            when (jenisPembayaran[position]) {
                "Full Payment" -> {
                    layoutFullPay?.visibility = View.VISIBLE
                    layoutKredit?.visibility = View.GONE
                    tvFullPrize?.setText(hargaTeks)
                    etDealFull?.setText("")
                }
                "Kredit" -> {
                    layoutFullPay?.visibility = View.GONE
                    layoutKredit?.visibility = View.VISIBLE
                    val jumlahCicilan = 24
                    val cicilan = hargaMobil / jumlahCicilan
                    val cicilanTeks = formatter.format(cicilan)
                    tvFullPrizeKredit?.setText("$cicilanTeks x $jumlahCicilan")
                    etDealKredit?.setText("")
                }
            }
        }

        btnSaveDraft.setOnClickListener {
            val jenis = spinnerJenisPembayaran?.text.toString()
            if (jenis.isEmpty()) {
                Toast.makeText(this, "Pilih jenis pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dealPaymentStr = if (jenis == "Full Payment")
                etDealFull?.text.toString()
            else
                etDealKredit?.text.toString()

            if (dealPaymentStr.isEmpty()) {
                Toast.makeText(this, "Masukkan deal payment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dealPayment = dealPaymentStr.replace("[^\\d]".toRegex(), "").toDoubleOrNull() ?: hargaMobil
            val tipePembayaran = if (jenis == "Full Payment") "cash" else "kredit"

            createTransaksi(
                namaPembeli = namaPembeli,
                noHp = noTelp,
                tipePembayaran = tipePembayaran,
                hargaAkhir = dealPayment,
                kodeMobil = kodeMobil,
                kodeUser = kodeUser,
                status = "pending",
                jaminanKtp = jaminanKtp,
                jaminanKk = jaminanKk,
                jaminanRekening = jaminanRekening
            )
        }

        btnFinish.setOnClickListener {
            val jenis = spinnerJenisPembayaran?.text.toString()
            if (jenis.isEmpty()) {
                Toast.makeText(this, "Pilih jenis pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dealPaymentStr = if (jenis == "Full Payment")
                etDealFull?.text.toString()
            else
                etDealKredit?.text.toString()

            if (dealPaymentStr.isEmpty()) {
                Toast.makeText(this, "Masukkan deal payment terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dealPayment = dealPaymentStr.replace("[^\\d]".toRegex(), "").toDoubleOrNull() ?: hargaMobil
            val tipePembayaran = if (jenis == "Full Payment") "cash" else "kredit"

            createTransaksi(
                namaPembeli = namaPembeli,
                noHp = noTelp,
                tipePembayaran = tipePembayaran,
                hargaAkhir = dealPayment,
                kodeMobil = kodeMobil,
                kodeUser = kodeUser,
                status = "completed",
                jaminanKtp = jaminanKtp,
                jaminanKk = jaminanKk,
                jaminanRekening = jaminanRekening
            )
        }
    }

    private fun createTransaksi(
        namaPembeli: String,
        noHp: String,
        tipePembayaran: String,
        hargaAkhir: Double,
        kodeMobil: String,
        kodeUser: String,
        status: String,
        jaminanKtp: Int,
        jaminanKk: Int,
        jaminanRekening: Int
    ) {
        Toast.makeText(this, "Menyimpan transaksi...", Toast.LENGTH_SHORT).show()

        ApiClient.apiService.createTransaksi(
            action = "create",
            namaPembeli = namaPembeli,
            noHp = noHp,
            tipePembayaran = tipePembayaran,
            hargaAkhir = hargaAkhir,
            kodeMobil = kodeMobil,
            kodeUser = kodeUser,
            status = status,
            note = "",
            namaKredit = "",
            jaminanKtp = jaminanKtp,
            jaminanKk = jaminanKk,
            jaminanRekening = jaminanRekening
        ).enqueue(object : Callback<CreateTransaksiResponse> {
            override fun onResponse(
                call: Call<CreateTransaksiResponse>,
                response: Response<CreateTransaksiResponse>
            ) {
                if (response.isSuccessful && response.body()?.code == "200") {
                    val kodeTransaksi = response.body()?.data?.kodeTransaksi ?: "-"

                    Toast.makeText(
                        this@AddTrnActivity3,
                        "Transaksi berhasil! Kode: $kodeTransaksi",
                        Toast.LENGTH_LONG
                    ).show()

                    // Kembali ke MainNavBar dan buka tab Transaksi
                    val intent = Intent(this@AddTrnActivity3, MainNavBar::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("OPEN_TAB", "transaksi")
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal menyimpan transaksi"
                    Toast.makeText(this@AddTrnActivity3, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateTransaksiResponse>, t: Throwable) {
                Toast.makeText(this@AddTrnActivity3, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
