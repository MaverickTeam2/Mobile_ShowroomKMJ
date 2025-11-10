package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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
        setupYearPicker()
        setupNextButton()
        setupProgressIndicator()
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

        val adapterTipe = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipeKendaraan)
        val adapterBahan = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bahanBakar)
        val adapterSistem = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sistemPenggerak)

        binding.dropdownTipeKendaraan.setAdapter(adapterTipe)
        binding.dropdownBahanBakar.setAdapter(adapterBahan)
        binding.dropdownSistemPenggerak.setAdapter(adapterSistem)

        binding.dropdownTipeKendaraan.setOnClickListener { binding.dropdownTipeKendaraan.showDropDown() }
        binding.dropdownBahanBakar.setOnClickListener { binding.dropdownBahanBakar.showDropDown() }
        binding.dropdownSistemPenggerak.setOnClickListener { binding.dropdownSistemPenggerak.showDropDown() }
    }

    private fun setupYearPicker() {
        val yearPicker = binding.yearPicker
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        yearPicker.minValue = 1990
        yearPicker.maxValue = currentYear + 1
        yearPicker.value = currentYear
        yearPicker.wrapSelectorWheel = false

        selectedYear = currentYear

        yearPicker.setOnValueChangedListener { _, _, newVal ->
            selectedYear = newVal
        }
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
