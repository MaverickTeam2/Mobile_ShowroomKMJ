package com.maverick.kmjshowroom.ui.car

import MobilDetailResponse
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.databinding.AddCarstep2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlin.jvm.java

class AddCarStep2Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep2Binding
    private var selectedYear = 0
    private var isEdit = false
    private var kodeMobil: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("is_edit", false)
        kodeMobil = intent.getStringExtra("kode_mobil")

        setupHeader()
        setupDropDowns()
        setupProgressIndicator()
        setupNextButton()

        binding.btnPilihTahun.setOnClickListener { showYearPickerDialog() }

        if (isEdit && kodeMobil != null) {
            loadMobilDetail(kodeMobil!!)
        } else {
            // if coming from step1, prefill possible image extras (we forward only when go to next)
        }
    }

    private fun setupHeader() { binding.layoutHeaderadd.iconClose.setOnClickListener { finish() } }
    private fun setupDropDowns() {
        val tipeKendaraan = listOf("SUV", "Sedan", "Hatchback", "Pickup", "Sport", "Convertible")
        val bahanBakar = listOf("Bensin", "Diesel", "Hybrid", "Listrik")
        val sistemPenggerak = listOf("FWD (Front Wheel Drive)", "RWD (Rear Wheel Drive)", "AWD (All Wheel Drive)", "4WD (Four Wheel Drive)")

        binding.dropdownTipeKendaraan.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipeKendaraan)
        binding.dropdownBahanBakar.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bahanBakar)
        binding.dropdownSistemPenggerak.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sistemPenggerak)
    }

    private fun showYearPickerDialog() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val dialogView = LayoutInflater.from(this).inflate(com.maverick.kmjshowroom.R.layout.year_picker, null)
        val yearPicker = dialogView.findViewById<NumberPicker>(com.maverick.kmjshowroom.R.id.yearPicker)
        yearPicker.minValue = 1990
        yearPicker.maxValue = currentYear + 1
        yearPicker.value = if (selectedYear != 0) selectedYear else currentYear
        yearPicker.wrapSelectorWheel = false

        AlertDialog.Builder(this)
            .setTitle("Pilih Tahun")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                selectedYear = yearPicker.value
                binding.btnPilihTahun.text = selectedYear.toString()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupNextButton() {
        binding.footerSave2.btnNext.setOnClickListener {
            val nama = binding.namaMobil.text.toString().trim()
            val jarak = binding.jarakTempuh.text.toString().trim()
            val warnaExterior = binding.warnaExterior.text.toString().trim()
            val warnaInterior = binding.warnaInterior.text.toString().trim()
            val fullPrice = binding.fullPrice.text.toString().trim()
            val uangMuka = binding.uangMuka.text.toString().trim()
            val angsuran = binding.angsuran.text.toString().trim()
            val tenor = binding.tenor.text.toString().trim()

            if (nama.isEmpty() || jarak.isEmpty() || warnaExterior.isEmpty() || warnaInterior.isEmpty()
                || fullPrice.isEmpty() || uangMuka.isEmpty() || angsuran.isEmpty() || tenor.isEmpty() || selectedYear == 0) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intentNext = Intent(this, AddCarStep3Activity::class.java)
            intentNext.putExtra("is_edit", isEdit)
            intentNext.putExtra("kode_mobil", kodeMobil)

            // step1
            intentNext.putExtra("foto_360", intent.getStringExtra("foto_360"))
            intentNext.putExtra("foto_depan", intent.getStringExtra("foto_depan"))
            intentNext.putExtra("foto_belakang", intent.getStringExtra("foto_belakang"))
            intentNext.putExtra("foto_samping", intent.getStringExtra("foto_samping"))
            intentNext.putExtra("foto_tambahan", intent.getStringArrayExtra("foto_tambahan"))

            // step2
            intentNext.putExtra("nama_mobil", nama)
            intentNext.putExtra("tahun", selectedYear.toString())
            intentNext.putExtra("jarak_tempuh", jarak)
            intentNext.putExtra("full_prize", fullPrice)
            intentNext.putExtra("uang_muka", uangMuka)
            intentNext.putExtra("angsuran", angsuran)
            intentNext.putExtra("tenor", tenor)
            intentNext.putExtra("warna_interior", warnaInterior)
            intentNext.putExtra("warna_exterior", warnaExterior)


            // spinners
            intentNext.putExtra("tipe_kendaraan", binding.dropdownTipeKendaraan.selectedItem.toString())
            intentNext.putExtra("bahan_bakar", binding.dropdownBahanBakar.selectedItem.toString())
            intentNext.putExtra("sistem_penggerak", binding.dropdownSistemPenggerak.selectedItem.toString())

            startActivity(intentNext)
        }
    }

    private fun setupProgressIndicator() {
        binding.addNewcar2.step1Icon.setImageResource(com.maverick.kmjshowroom.R.drawable.ic_check_blue)
        binding.addNewcar2.step2Icon.setImageResource(com.maverick.kmjshowroom.R.drawable.ic_number2_blue)
    }

    private fun loadMobilDetail(kode: String) {
        ApiClient.apiService.getMobilDetail(kode)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(call: Call<MobilDetailResponse>, response: Response<MobilDetailResponse>) {
                    val body = response.body() ?: return

                    if (body.code != 200) return
                    val m = body.mobil

                    binding.namaMobil.setText(m.nama_mobil)
                    binding.jarakTempuh.setText(m.jarak_tempuh.toString())
                    binding.warnaExterior.setText(m.warna_exterior)
                    binding.warnaInterior.setText(m.warna_interior)
                    binding.fullPrice.setText(m.full_prize.toString())
                    binding.uangMuka.setText(m.uang_muka.toString())
                    binding.angsuran.setText(m.angsuran.toString())
                    binding.tenor.setText(m.tenor.toString())
                    selectedYear = m.tahun_mobil
                    binding.btnPilihTahun.text = selectedYear.toString()

                    setSpinnerValue(binding.dropdownTipeKendaraan, m.jenis_kendaraan)
                    setSpinnerValue(binding.dropdownBahanBakar, m.tipe_bahan_bakar)
                    setSpinnerValue(binding.dropdownSistemPenggerak, m.sistem_penggerak)
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    Toast.makeText(this@AddCarStep2Activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setSpinnerValue(spinner: android.widget.Spinner, value: String) {
        val adapter = spinner.adapter ?: return
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString().equals(value, ignoreCase = true)) {
                spinner.setSelection(i)
                break
            }
        }
    }
}
