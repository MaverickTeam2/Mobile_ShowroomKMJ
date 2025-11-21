package com.maverick.kmjshowroom.ui.car

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.API.MultipartUtil
import com.maverick.kmjshowroom.Model.GenericResponse
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.AddCarstep4Binding
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import FotoData
import MobilDetailResponse

class AddCarStep4Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep4Binding

    private var isEdit = false
    private var kodeMobil: String? = null
    private var selectedStatus = "available"
    private var oldFotoList: List<FotoData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("is_edit", false)
        kodeMobil = intent.getStringExtra("kode_mobil")

        setupHeader()
        setupStatusButtons()
        setupProgressindicator()
        setupFooterButtons()

        if (isEdit && kodeMobil != null) {
            loadStatusMobil(kodeMobil!!)
            loadOldFotos(kodeMobil!!)
        }
    }

    private fun setupHeader() {
        binding.layoutHeaderadd.iconClose.setOnClickListener { finish() }
    }

    private fun setupStatusButtons() {
        val statusButtons = listOf(
            binding.btnAvailable to "available",
            binding.btnReserved to "reserved",
            binding.btnSold to "sold",
            binding.btnShipping to "shipping",
            binding.btnDelivered to "delivered"
        )

        val selectedBg = ContextCompat.getColor(this, R.color.blue_50)
        val selectedText = ContextCompat.getColor(this, R.color.blue_700)
        val defaultBg = ContextCompat.getColor(this, R.color.white)
        val defaultText = ContextCompat.getColor(this, R.color.loginText)

        for ((btn, value) in statusButtons) {
            btn.setOnClickListener {
                statusButtons.forEach {
                    it.first.setBackgroundColor(defaultBg)
                    it.first.setTextColor(defaultText)
                }
                btn.setBackgroundColor(selectedBg)
                btn.setTextColor(selectedText)
                selectedStatus = value
            }
        }
    }

    private fun highlightStatus(status: String) {
        when (status) {
            "available" -> binding.btnAvailable.performClick()
            "reserved" -> binding.btnReserved.performClick()
            "sold" -> binding.btnSold.performClick()
            "shipping" -> binding.btnShipping.performClick()
            "delivered" -> binding.btnDelivered.performClick()
        }
    }

    private fun setupProgressindicator() {
        binding.addNewcar4.step1Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar4.step2Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar4.step3Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar4.step4Icon.setImageResource(R.drawable.ic_number4_blue)
    }

    private fun setupFooterButtons() {
        binding.footerSave4.btnDraft.text = "Hapus"
        binding.footerSave4.btnNext.text = if (isEdit) "Update" else "Publish"

        // Tombol HAPUS
        binding.footerSave4.btnDraft.setOnClickListener {
            if (kodeMobil == null) {
                Toast.makeText(this, "Kode mobil kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.apiService.deleteMobil(true, kodeMobil!!)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(
                                this@AddCarStep4Activity,
                                "Mobil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddCarStep4Activity,
                                "Gagal menghapus",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(
                            this@AddCarStep4Activity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // Tombol Publish / Update
        binding.footerSave4.btnNext.setOnClickListener { submitAll() }
    }

    private fun loadStatusMobil(kode: String) {
        ApiClient.apiService.getMobilDetail(kode)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(
                    call: Call<MobilDetailResponse>,
                    response: Response<MobilDetailResponse>
                ) {
                    val body = response.body() ?: return
                    selectedStatus = body.mobil.status.lowercase()
                    highlightStatus(selectedStatus)
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {}
            })
    }

    // ✅ LOAD FOTO LAMA UNTUK UPDATE
    private fun loadOldFotos(kode: String) {
        ApiClient.apiService.getMobilDetail(kode)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(
                    call: Call<MobilDetailResponse>,
                    response: Response<MobilDetailResponse>
                ) {
                    val body = response.body() ?: return
                    if (body.success) {
                        oldFotoList = body.foto
                        Log.d("AddCarStep4", "Loaded ${oldFotoList.size} old photos")
                    }
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    Log.e("AddCarStep4", "Failed to load old photos: ${t.message}")
                }
            })
    }

    private fun normalizeType(raw: String?): String {
        if (raw == null) return ""
        return raw.lowercase()
            .replace("°", "")
            .replace(" ", "")
            .trim()
    }

    /** ===================== 🔥 SUBMIT / API (FINAL FIX) ===================== */
    // Ganti seluruh fungsi submitAll() dengan versi ini
    private fun submitAll() {
        // Ambil data dari step sebelumnya
        val nama = intent.getStringExtra("nama_mobil") ?: ""
        val jarak = intent.getStringExtra("jarak_tempuh") ?: ""
        val fullPrize = intent.getStringExtra("full_prize") ?: ""
        val uangMuka = intent.getStringExtra("uang_muka") ?: ""
        val angsuran = intent.getStringExtra("angsuran") ?: ""
        val tenor = intent.getStringExtra("tenor") ?: ""
        val tahun = intent.getStringExtra("tahun") ?: ""
        val tipeKendaraan = intent.getStringExtra("tipe_kendaraan") ?: ""
        val bahanBakar = intent.getStringExtra("bahan_bakar") ?: ""
        val sistemPenggerak = intent.getStringExtra("sistem_penggerak") ?: ""
        val warnaInterior = intent.getStringExtra("warna_interior") ?: ""
        val warnaExterior = intent.getStringExtra("warna_exterior") ?: ""
        val fiturList = intent.getIntegerArrayListExtra("fitur") ?: arrayListOf()

        // Foto dari step sebelumnya
        val u360 = intent.getStringExtra("foto_360")
        val uDepan = intent.getStringExtra("foto_depan")
        val uBelakang = intent.getStringExtra("foto_belakang")
        val uSamping = intent.getStringExtra("foto_samping")
        val fotoTambahan = intent.getStringArrayExtra("foto_tambahan") ?: arrayOf()

        // Validasi
        if (nama.isEmpty() || tahun.isEmpty() || jarak.isEmpty()
            || fullPrize.isEmpty() || uangMuka.isEmpty()
            || angsuran.isEmpty() || tenor.isEmpty()
        ) {
            Toast.makeText(this, "Lengkapi data sebelum publish", Toast.LENGTH_SHORT).show()
            return
        }

        // Form fields
        val map = mutableMapOf<String, okhttp3.RequestBody>()
        map["nama_mobil"] = MultipartUtil.createPart(nama)
        map["tahun"] = MultipartUtil.createPart(tahun)
        map["jarak_tempuh"] = MultipartUtil.createPart(jarak)
        map["full_prize"] = MultipartUtil.createPart(fullPrize)
        map["uang_muka"] = MultipartUtil.createPart(uangMuka)
        map["angsuran"] = MultipartUtil.createPart(angsuran)
        map["tenor"] = MultipartUtil.createPart(tenor)
        map["tipe_kendaraan"] = MultipartUtil.createPart(tipeKendaraan)
        map["bahan_bakar"] = MultipartUtil.createPart(bahanBakar)
        map["sistem_penggerak"] = MultipartUtil.createPart(sistemPenggerak)
        map["warna_interior"] = MultipartUtil.createPart(warnaInterior)
        map["warna_exterior"] = MultipartUtil.createPart(warnaExterior)
        map["status"] = MultipartUtil.createPart(selectedStatus)

        fiturList.forEachIndexed { index, id ->
            map["fitur[$index]"] = MultipartUtil.createPart(id.toString())
        }

        if (isEdit) {
            map["update"] = MultipartUtil.createPart("1")
            map["kode_mobil"] = MultipartUtil.createPart(kodeMobil ?: "")
        }

        val files = mutableListOf<MultipartBody.Part>()

        // Mapping tipe foto → URI baru
        val newPhotoMap = mapOf(
            "360" to u360,
            "depan" to uDepan,
            "belakang" to uBelakang,
            "samping" to uSamping
        )

        var fotoIndex = 0

        if (isEdit && oldFotoList.isNotEmpty()) {
            oldFotoList.forEach { old ->
                val normalizedType = normalizeType(old.tipe_foto)
                val newUri = newPhotoMap[normalizedType]

                val userReplaced = !newUri.isNullOrEmpty() && !newUri.startsWith("http")

                if (!userReplaced) {
                    val rawUrl = old.foto ?: ""
                    val fotoPath =
                        if (rawUrl.contains("/images/mobil/")) {
                            "/images/mobil/" + rawUrl.substringAfter("/images/mobil/")
                        } else rawUrl

                    map["foto[$fotoIndex][id_foto]"] =
                        MultipartUtil.createPart((old.id_foto ?: "").toString())
                    map["foto[$fotoIndex][tipe_foto]"] =
                        MultipartUtil.createPart(old.tipe_foto ?: "")
                    map["foto[$fotoIndex][nama_file]"] =
                        MultipartUtil.createPart(fotoPath)
                    map["foto[$fotoIndex][urutan]"] =
                        MultipartUtil.createPart((fotoIndex + 1).toString())

                    fotoIndex++
                }
            }
        }

        // Kirim file baru (jika ada penggantian)
        fun addIfChanged(field: String, uri: String?) {
            if (!uri.isNullOrEmpty() && !uri.startsWith("http")) {
                MultipartUtil.prepareFile(field, Uri.parse(uri), this)?.let { files.add(it) }
            }
        }

        addIfChanged("foto_360", u360)
        addIfChanged("foto_depan", uDepan)
        addIfChanged("foto_belakang", uBelakang)
        addIfChanged("foto_samping", uSamping)

        fotoTambahan.forEach { uri ->
            if (!uri.isNullOrEmpty() && !uri.startsWith("http")) {
                MultipartUtil.prepareFile("foto_tambahan[]", Uri.parse(uri), this)
                    ?.let { files.add(it) }
            }
        }

        Log.d("AddCarStep4", "FINAL REQUEST => meta: ${fotoIndex} | files: ${files.size}")

        ApiClient.apiService.uploadMobil(map, files)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AddCarStep4Activity,
                            if (isEdit) "Berhasil update" else "Berhasil publish",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Gagal"
                        Toast.makeText(this@AddCarStep4Activity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@AddCarStep4Activity, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }
}