package com.maverick.kmjshowroom.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.SharedCarViewModel
import android.view.View
import com.google.android.material.button.MaterialButton

class AddTrnActivity1 : AppCompatActivity() {

    private lateinit var sharedCarViewModel: SharedCarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_trn_1)

        sharedCarViewModel = ViewModelProvider(this)[SharedCarViewModel::class.java]

        // Initialize views - TANPA null safety operator karena harus ada
        val closeButton = findViewById<ImageView>(R.id.icon_close)
        val dropdown = findViewById<AutoCompleteTextView>(R.id.dropdown_text)
        val txtTipe = findViewById<TextInputEditText>(R.id.txttipe)
        val imgCar = findViewById<ImageView>(R.id.imgCar)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val txtDp = findViewById<TextView>(R.id.txtDp)
        val txtKm = findViewById<TextView>(R.id.txtKm)
        val txtYear = findViewById<TextView>(R.id.txtYear)
        val tipeContainer = findViewById<LinearLayout>(R.id.tipeContainer)
        val carDetailContainer = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.carDetailContainer)
        val btnNext = findViewById<MaterialButton>(R.id.btn_next)

        // Close button listener
        closeButton.setOnClickListener {
            finish()
        }

        // Hide containers initially
        tipeContainer.visibility = View.GONE
        carDetailContainer.visibility = View.GONE

        // Variables untuk menyimpan data mobil yang dipilih
        var selectedKodeMobil = ""
        var selectedCarName = ""
        var selectedHarga = ""  // Ubah ke String untuk konsistensi
        var selectedDp = ""

        // Observer untuk error
        sharedCarViewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                Log.e("AddTrnActivity1", "Error: $it")
            }
        }

        // Observer untuk loading
        sharedCarViewModel.isLoading.observe(this) { isLoading ->
            btnNext.isEnabled = !isLoading
            // Opsional: tampilkan progress bar
        }

        // Observer untuk car list
        sharedCarViewModel.carList.observe(this) { cars ->
            Log.d("AddTrnActivity1", "Received ${cars.size} cars")

            if (cars.isEmpty()) {
                Log.d("AddTrnActivity1", "Car list empty, loading from API")
                sharedCarViewModel.loadMobilFromApi()
                return@observe
            }

            // Filter hanya mobil yang available
            val availableCars = cars.filter {
                it.status.lowercase() == "available"
            }

            Log.d("AddTrnActivity1", "Available cars: ${availableCars.size}")

            if (availableCars.isEmpty()) {
                Toast.makeText(this, "Tidak ada mobil tersedia", Toast.LENGTH_SHORT).show()
                return@observe
            }

            val namaMobilList = availableCars.map { it.title }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                namaMobilList
            )
            dropdown.setAdapter(adapter)

            dropdown.setOnItemClickListener { _, _, position, _ ->
                try {
                    val selectedCar = availableCars[position]

                    Log.d("AddTrnActivity1", "Selected car: ${selectedCar.title}")

                    // Simpan data mobil
                    selectedKodeMobil = selectedCar.kodeMobil
                    selectedCarName = selectedCar.title
                    selectedHarga = selectedCar.fullPrice.toString()
                    selectedDp = selectedCar.dp

                    // Set tipe kendaraan
                    val tipeText = selectedCar.tipeKendaraan.ifEmpty {
                        selectedCar.bahanBakar
                    }
                    txtTipe.setText(tipeText)

                    // Load foto mobil
                    if (!selectedCar.fotoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(selectedCar.fotoUrl)
                            .placeholder(R.drawable.placeholder_car)
                            .error(R.drawable.placeholder_car)
                            .into(imgCar)
                    } else {
                        imgCar.setImageResource(R.drawable.placeholder_car)
                    }

                    // Set detail mobil
                    txtTitle.text = selectedCar.title
                    txtPrice.text = selectedCar.angsuran
                    txtDp.text = selectedCar.dp
                    txtKm.text = selectedCar.jarakTempuh
                    txtYear.text = selectedCar.year

                    // Tampilkan container
                    tipeContainer.visibility = View.VISIBLE
                    carDetailContainer.visibility = View.VISIBLE

                    Log.d("AddTrnActivity1", "Car details loaded successfully")

                } catch (e: Exception) {
                    Log.e("AddTrnActivity1", "Error selecting car: ${e.message}", e)
                    Toast.makeText(this, "Error memuat detail mobil", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Button Next listener
        btnNext.text = "Selanjutnya"
        btnNext.setOnClickListener {
            if (selectedKodeMobil.isEmpty()) {
                Toast.makeText(this, "Pilih mobil terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val intent = Intent(this, AddTrnActivity2::class.java).apply {
                    putExtra("selectedCar", selectedCarName)
                    putExtra("kodeMobil", selectedKodeMobil)
                    putExtra("hargaMobil", selectedHarga)
                    putExtra("dpMobil", selectedDp)
                }

                Log.d("AddTrnActivity1", "Starting AddTrnActivity2 with:")
                Log.d("AddTrnActivity1", "- selectedCar: $selectedCarName")
                Log.d("AddTrnActivity1", "- kodeMobil: $selectedKodeMobil")
                Log.d("AddTrnActivity1", "- hargaMobil: $selectedHarga")
                Log.d("AddTrnActivity1", "- dpMobil: $selectedDp")

                startActivity(intent)
            } catch (e: Exception) {
                Log.e("AddTrnActivity1", "Error starting next activity: ${e.message}", e)
                Toast.makeText(this, "Error melanjutkan transaksi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data saat kembali ke activity ini
        if (sharedCarViewModel.carList.value.isNullOrEmpty()) {
            sharedCarViewModel.loadMobilFromApi()
        }
    }
}