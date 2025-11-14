package com.maverick.kmjshowroom.ui.car

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.AddCarstep2Binding
import java.util.Calendar

class AddCarStep2Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep2Binding
    private var selectedYear: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupDropDowns()
        setupProgressIndicator()
        setupNextButton()

        binding.btnPilihTahun.setOnClickListener {
            showYearPickerDialog()
        }
    }

    private fun setupHeader() {
        binding.layoutHeaderadd.iconClose.setOnClickListener {
            finish()
        }
    }

    private fun setupDropDowns() {
        val tipeKendaraan = listOf("SUV", "Sedan", "Hatchback", "Pickup", "Sport", "Convertible")
        val bahanBakar = listOf("Bensin", "Diesel", "Hybrid", "Listrik")
        val sistemPenggerak = listOf(
            "FWD (Front Wheel Drive)",
            "RWD (Rear Wheel Drive)",
            "AWD (All Wheel Drive)",
            "4WD (Four Wheel Drive)"
        )

        val adapterTipe = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipeKendaraan)
        val adapterBahan = ArrayAdapter(this, android.R.layout.simple_spinner_item, bahanBakar)
        val adapterSistem = ArrayAdapter(this, android.R.layout.simple_spinner_item, sistemPenggerak)

        adapterTipe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterBahan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSistem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.dropdownTipeKendaraan.adapter = adapterTipe
        binding.dropdownBahanBakar.adapter = adapterBahan
        binding.dropdownSistemPenggerak.adapter = adapterSistem
    }

    private fun showYearPickerDialog() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.year_picker, null)
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)

        yearPicker.minValue = 1990
        yearPicker.maxValue = currentYear + 1
        yearPicker.value = selectedYear.takeIf { it != 0 } ?: currentYear
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
            val intent = Intent(this, AddCarStep3Activity::class.java)
            startActivity(intent)
        }
    }

    private fun setupProgressIndicator() {
        binding.addNewcar2.apply {
            step1Icon.setImageResource(R.drawable.ic_check_blue)
            step2Icon.setImageResource(R.drawable.ic_number2_blue)
        }
    }
}
