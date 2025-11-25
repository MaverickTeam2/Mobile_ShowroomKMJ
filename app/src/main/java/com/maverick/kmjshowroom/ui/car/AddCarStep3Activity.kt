package com.maverick.kmjshowroom.ui.car

import MobilDetailResponse
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.databinding.AddCarstep3Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCarStep3Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep3Binding
    private val listCheckBox = mutableListOf<CheckBox>()

    private var isEdit = false
    private var kodeMobil: String? = null
    private var mobilDetail: MobilDetailResponse? = null // ✅ TAMBAHKAN INI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("is_edit", false)
        kodeMobil = intent.getStringExtra("kode_mobil")

        setupHeader()
        setupProgress()
        registerAllCheckbox()

        if (isEdit && kodeMobil != null) loadFiturMobil(kodeMobil!!)

        binding.footerSave3.btnNext.setOnClickListener {
            val selected = getSelectedFitur()

            // validation: enforce data presence from step2
            val nama = intent.getStringExtra("nama_mobil") ?: ""
            val tahun = intent.getStringExtra("tahun") ?: ""
            val jarak = intent.getStringExtra("jarak_tempuh") ?: ""

            if (nama.isEmpty() || tahun.isEmpty() || jarak.isEmpty()) {
                Toast.makeText(this, "Data informasi mobil belum lengkap", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intentNext = Intent(this, AddCarStep4Activity::class.java)
            intentNext.putExtra("is_edit", isEdit)
            intentNext.putExtra("kode_mobil", kodeMobil)

            // carry all previous values (step1 + step2)
            intentNext.putExtra("foto_360", intent.getStringExtra("foto_360"))
            intentNext.putExtra("foto_depan", intent.getStringExtra("foto_depan"))
            intentNext.putExtra("foto_belakang", intent.getStringExtra("foto_belakang"))
            intentNext.putExtra("foto_samping", intent.getStringExtra("foto_samping"))
            intentNext.putExtra("foto_tambahan", intent.getStringArrayExtra("foto_tambahan"))

            intentNext.putExtra("nama_mobil", intent.getStringExtra("nama_mobil"))
            intentNext.putExtra("tahun", intent.getStringExtra("tahun"))
            intentNext.putExtra("jarak_tempuh", intent.getStringExtra("jarak_tempuh"))

            intentNext.putExtra("full_prize", intent.getStringExtra("full_prize"))
            intentNext.putExtra("uang_muka", intent.getStringExtra("uang_muka"))
            intentNext.putExtra("angsuran", intent.getStringExtra("angsuran"))
            intentNext.putExtra("tenor", intent.getStringExtra("tenor"))

            intentNext.putExtra("tipe_kendaraan", intent.getStringExtra("tipe_kendaraan"))
            intentNext.putExtra("bahan_bakar", intent.getStringExtra("bahan_bakar"))
            intentNext.putExtra("sistem_penggerak", intent.getStringExtra("sistem_penggerak"))
            intentNext.putExtra("warna_interior", intent.getStringExtra("warna_interior"))
            intentNext.putExtra("warna_exterior", intent.getStringExtra("warna_exterior"))
            intentNext.putIntegerArrayListExtra("fitur", ArrayList(selected))

            // ✅ KIRIM FOTO LAMA UNTUK UPDATE
            if (isEdit && mobilDetail != null) {
                val oldList = ArrayList<String>()
                mobilDetail!!.foto.forEach { f -> oldList.add(f.foto) }
                intentNext.putStringArrayListExtra("old_foto", oldList)
            }

            startActivity(intentNext)
        }
    }

    private fun setupHeader() {
        binding.layoutHeaderadd.iconClose.setOnClickListener { finish() }
    }

    private fun setupProgress() {
        binding.addNewcar3.step1Icon.setImageResource(com.maverick.kmjshowroom.R.drawable.ic_check_blue)
        binding.addNewcar3.step2Icon.setImageResource(com.maverick.kmjshowroom.R.drawable.ic_check_blue)
        binding.addNewcar3.step3Icon.setImageResource(com.maverick.kmjshowroom.R.drawable.ic_number3_blue)
    }

    private fun registerAllCheckbox() {
        listCheckBox.addAll(
            listOf(
                binding.cbAirbagPengemudi,
                binding.cbTractionControl,
                binding.cbBlindSpot,
                binding.cbForwardCollision,
                binding.cbRearCamera,
                binding.cbAbs,
                binding.cbEsc,
                binding.cbLaneDeparture,
                binding.cbEmergencyBraking,
                binding.cbParkingSensors,

                binding.cbAc,
                binding.cbPowerSteering,
                binding.cbCentralLocking,
                binding.cbBluetooth,
                binding.cbAudioSystem,
                binding.cbHeatedSeats,
                binding.cbClimateControl,
                binding.cbPowerWindows,
                binding.cbUsbPort,
                binding.cbWirelessCharging,
                binding.cbNavigationSeats,
                binding.cbVentilatedSeats,

                binding.cbLedHeadlights,
                binding.cbFogLamps,
                binding.cbPanoramicRoof,
                binding.cbRoofRails,
                binding.cbAlloyWheels,
                binding.cbLedTaillights,
                binding.cbSunroof,
                binding.cbSpoiler,
                binding.cbChromeTrim,
                binding.cbRunflatTires,

                binding.cbEngineImmobilizer,
                binding.cbPushButtonStart,
                binding.cbRainSensingWipers,
                binding.cbCruiseControl,
                binding.cbHillStartAssist,
                binding.cbKeylessEntry,
                binding.cbAutoHeadlamps,
                binding.cbParkingAssist,
                binding.cbAdaptiveCruise,
                binding.cbTirePressure
            )
        )
    }

    private fun loadFiturMobil(kode: String) {
        ApiClient.apiService.getMobilDetail(kode)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(call: Call<MobilDetailResponse>, response: Response<MobilDetailResponse>) {
                    val body = response.body() ?: return

                    if (body.code != 200) return

                    mobilDetail = body

                    // Sekarang fitur = List<FiturData>
                    body.fitur.forEach { fitur ->
                        val id = fitur.id          // ambil ID fitur
                        val index = id - 1         // checkbox berdasarkan urutan ID
                        if (index in listCheckBox.indices) {
                            listCheckBox[index].isChecked = true
                        }
                    }
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    Toast.makeText(this@AddCarStep3Activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun getSelectedFitur(): List<Int> {
        val sel = mutableListOf<Int>()
        listCheckBox.forEachIndexed { index, cb ->
            if (cb.isChecked) sel.add(index + 1)
        }
        return sel
    }
}