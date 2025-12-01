package com.maverick.kmjshowroom.ui.car

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.API.MultipartUtil
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
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
    private lateinit var userDb: UserDatabaseHelper

    private var isEdit = false
    private var kodeMobil: String? = null
    private var selectedStatus = "available"
    private var oldFotoList: List<FotoData> = emptyList()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        userDb = UserDatabaseHelper(this)

        setupLoading()

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

    private fun setupLoading() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        loadingDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    private fun showLoading() = loadingDialog.show()
    private fun hideLoading() = loadingDialog.dismiss()

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
                    if (body.code != 200) return

                    selectedStatus = body.mobil.status.lowercase()
                    highlightStatus(selectedStatus)
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    Log.e("AddCarStep4", "Failed to load status: ${t.message}")
                }
            })
    }

    private fun loadOldFotos(kode: String) {
        ApiClient.apiService.getMobilDetail(kode)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(
                    call: Call<MobilDetailResponse>,
                    response: Response<MobilDetailResponse>
                ) {
                    val body = response.body() ?: return
                    if (body.code == 200) {
                        oldFotoList = body.foto
                        Log.d("AddCarStep4", "✅ Loaded ${oldFotoList.size} old photos")
                        oldFotoList.forEach {
                            Log.d("AddCarStep4", "  - ID: ${it.id_foto}, Type: ${it.tipe_foto}, URL: ${it.foto}")
                        }
                    }
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {
                    Log.e("AddCarStep4", "Failed to load photos: ${t.message}")
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

    private fun submitAll() {

        showLoading()

        // ✅ AMBIL KODE USER DARI SQLITE
        val currentUser = userDb.getUser()
        if (currentUser == null) {
            hideLoading()
            Toast.makeText(this, "User belum login. Silakan login terlebih dahulu.", Toast.LENGTH_LONG).show()
            return
        }

        val kodeUser = currentUser.kode_user ?: ""
        if (kodeUser.isEmpty()) {
            hideLoading()
            Toast.makeText(this, "Kode user tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("AddCarStep4", "✅ Kode User dari SQLite: $kodeUser")

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

        val u360 = intent.getStringExtra("foto_360")
        val uDepan = intent.getStringExtra("foto_depan")
        val uBelakang = intent.getStringExtra("foto_belakang")
        val uSamping = intent.getStringExtra("foto_samping")
        val fotoTambahan = intent.getStringArrayExtra("foto_tambahan") ?: arrayOf()

        if (nama.isEmpty() || tahun.isEmpty() || jarak.isEmpty()
            || fullPrize.isEmpty() || uangMuka.isEmpty()
            || angsuran.isEmpty() || tenor.isEmpty()
        ) {
            hideLoading()
            Toast.makeText(this, "Lengkapi data sebelum publish", Toast.LENGTH_SHORT).show()
            return
        }

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
        map["kode_user"] = MultipartUtil.createPart(kodeUser)

        fiturList.forEachIndexed { index, id ->
            map["fitur[$index]"] = MultipartUtil.createPart(id.toString())
        }

        if (isEdit) {
            map["update"] = MultipartUtil.createPart("1")
            map["kode_mobil"] = MultipartUtil.createPart(kodeMobil ?: "")
        }

        val files = mutableListOf<MultipartBody.Part>()

        // ✅ Handle foto utama (4 FIXED SLOTS)
        fun addIfChanged(field: String, uri: String?) {
            if (!uri.isNullOrEmpty() && !uri.startsWith("http")) {
                MultipartUtil.prepareFile(field, Uri.parse(uri), this)?.let {
                    files.add(it)
                    Log.d("AddCarStep4", "📷 Adding NEW photo: $field")
                }
            } else {
                Log.d("AddCarStep4", "⏭️ Skipping (unchanged): $field")
            }
        }

        addIfChanged("foto_360", u360)
        addIfChanged("foto_depan", uDepan)
        addIfChanged("foto_belakang", uBelakang)
        addIfChanged("foto_samping", uSamping)

        // ✅ FIX: Handle foto tambahan (6 FIXED SLOTS dengan urutan tetap)
        if (isEdit && oldFotoList.isNotEmpty()) {

            // Build map: urutan -> FotoData untuk foto tambahan lama
            val oldTambahanMap = mutableMapOf<Int, FotoData>()
            oldFotoList.filter { normalizeType(it.tipe_foto) == "tambahan" }
                .forEach { foto ->
                    // Parsing urutan dari database
                    // Urutan 5 = foto_tambahan[0], urutan 6 = foto_tambahan[1], dst
                    val urutanDb = foto.id_foto.toIntOrNull() ?: 0

                    // Cari urutan berdasarkan posisi di oldFotoList
                    val index = oldFotoList.indexOf(foto)
                    if (index >= 4) { // index 4+ = foto tambahan
                        val slot = index - 4 // slot 0-5
                        oldTambahanMap[slot] = foto
                    }
                }

            Log.d("AddCarStep4", "📸 Old Tambahan Map: ${oldTambahanMap.size} items")

            // Loop 6 FIXED SLOTS (foto_tambahan[0..5])
            for (slot in 0 until 6) {
                val uri = fotoTambahan.getOrNull(slot)

                if (uri.isNullOrEmpty()) {
                    // ✅ Slot kosong - skip
                    Log.d("AddCarStep4", "⚪ Slot $slot: EMPTY")
                    continue
                }

                val isOldPhoto = uri.startsWith("http")
                val oldFotoInSlot = oldTambahanMap[slot]

                if (isOldPhoto) {
                    // ✅ Foto TIDAK berubah - skip (PHP akan keep foto ini)
                    Log.d("AddCarStep4", "✅ Slot $slot: UNCHANGED (${oldFotoInSlot?.id_foto})")

                } else {
                    // ✅ Foto BERUBAH atau BARU

                    if (oldFotoInSlot != null) {
                        // REPLACE: Ada foto lama di slot ini
                        Log.d("AddCarStep4", "🔄 Slot $slot: REPLACE old ID=${oldFotoInSlot.id_foto}")

                        // Kirim file baru + metadata replace
                        MultipartUtil.prepareFile("foto_tambahan_slot_$slot", Uri.parse(uri), this)?.let {
                            files.add(it)
                        }
                        map["tambahan_slot_$slot"] = MultipartUtil.createPart("replace")
                        map["tambahan_old_id_$slot"] = MultipartUtil.createPart(oldFotoInSlot.id_foto)

                    } else {
                        // INSERT: Slot kosong, isi baru
                        Log.d("AddCarStep4", "🆕 Slot $slot: NEW")

                        MultipartUtil.prepareFile("foto_tambahan_slot_$slot", Uri.parse(uri), this)?.let {
                            files.add(it)
                        }
                        map["tambahan_slot_$slot"] = MultipartUtil.createPart("new")
                    }
                }
            }

        } else {
            // Mode INSERT (bukan edit) - upload semua foto tambahan yang ada
            fotoTambahan.forEachIndexed { slot, uri ->
                if (!uri.isNullOrEmpty() && !uri.startsWith("http")) {
                    MultipartUtil.prepareFile("foto_tambahan_slot_$slot", Uri.parse(uri), this)?.let {
                        files.add(it)
                    }
                    map["tambahan_slot_$slot"] = MultipartUtil.createPart("new")
                }
            }
        }

        Log.d("AddCarStep4", "📤 Total files to upload: ${files.size}")

        ApiClient.apiService.uploadMobil(map, files)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    hideLoading()

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
                        Log.e("AddCarStep4", "Response error: $msg")
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    hideLoading()
                    Toast.makeText(this@AddCarStep4Activity, t.message, Toast.LENGTH_LONG).show()
                    Log.e("AddCarStep4", "Network error: ${t.message}")
                }
            })
    }
}