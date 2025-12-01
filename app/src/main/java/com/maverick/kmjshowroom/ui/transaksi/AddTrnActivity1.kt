package com.maverick.kmjshowroom.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.SharedCarViewModel
import android.view.View

class AddTrnActivity1 : AppCompatActivity() {

    private lateinit var sharedCarViewModel: SharedCarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_trn_1)

        sharedCarViewModel = ViewModelProvider(this)[SharedCarViewModel::class.java]

        val closeButton = findViewById<ImageView>(R.id.icon_close)
        closeButton?.setOnClickListener {
            finish()
        }

        val dropdown = findViewById<AutoCompleteTextView>(R.id.dropdown_text)
        val txtTipe = findViewById<TextInputEditText>(R.id.txttipe)
        val imgCar = findViewById<ImageView>(R.id.imgCar)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val txtDp = findViewById<TextView>(R.id.txtDp)
        val txtKm = findViewById<TextView>(R.id.txtKm)
        val txtYear = findViewById<TextView>(R.id.txtYear)

        // PERBAIKAN: Container untuk tipe mobil dan detail mobil - HIDDEN di awal
        val tipeContainer = findViewById<LinearLayout>(R.id.tipeContainer)
        val carDetailContainer = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.carDetailContainer)

        tipeContainer?.visibility = View.GONE
        carDetailContainer?.visibility = View.GONE

        var selectedKodeMobil = ""
        var selectedCarName = ""
        var selectedHarga = 0
        var selectedDp = ""

        sharedCarViewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        sharedCarViewModel.carList.observe(this) { cars ->
            if (cars.isEmpty()) {
                sharedCarViewModel.loadMobilFromApi()
                return@observe
            }

            val availableCars = cars.filter { it.status.lowercase() == "available" }
            val namaMobilList = availableCars.map { it.title }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                namaMobilList
            )
            dropdown.setAdapter(adapter)

            dropdown.setOnItemClickListener { _, _, position, _ ->
                val selectedCar = availableCars[position]

                selectedKodeMobil = selectedCar.kodeMobil
                selectedCarName = selectedCar.title
                selectedHarga = selectedCar.fullPrice
                selectedDp = selectedCar.dp

                txtTipe.setText(selectedCar.tipeKendaraan.ifEmpty { selectedCar.bahanBakar })

                if (!selectedCar.fotoUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(selectedCar.fotoUrl)
                        .placeholder(R.drawable.placeholder_car)
                        .error(R.drawable.placeholder_car)
                        .into(imgCar)
                } else {
                    imgCar.setImageResource(R.drawable.placeholder_car)
                }

                txtTitle.text = selectedCar.title
                txtPrice.text = selectedCar.angsuran
                txtDp.text = selectedCar.dp
                txtKm.text = selectedCar.jarakTempuh
                txtYear.text = selectedCar.year

                // PERBAIKAN: Tampilkan tipe dan detail mobil setelah dipilih
                tipeContainer?.visibility = View.VISIBLE
                carDetailContainer?.visibility = View.VISIBLE
            }
        }

        val btnNext = findViewById<View>(R.id.btn_next)
        btnNext?.setOnClickListener {
            if (selectedKodeMobil.isNotEmpty()) {
                val intent = Intent(this, AddTrnActivity2::class.java).apply {
                    putExtra("selectedCar", selectedCarName)
                    putExtra("kodeMobil", selectedKodeMobil)
                    putExtra("hargaMobil", selectedHarga.toString())
                    putExtra("dpMobil", selectedDp)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Pilih mobil terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}