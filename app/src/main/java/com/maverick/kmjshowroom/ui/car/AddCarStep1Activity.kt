package com.maverick.kmjshowroom.ui.car

import MobilDetailResponse
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.databinding.AddCarstep1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCarStep1Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep1Binding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var currentTarget: ImageView? = null

    // store URIs as strings to pass between activities
    private var uri360: String? = null
    private var uriDepan: String? = null
    private var uriBelakang: String? = null
    private var uriSamping: String? = null
    private val tambahanUris = mutableListOf<String?>() // size up to 6

    private var isEdit = false
    private var kodeMobil: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("is_edit", false)
        kodeMobil = intent.getStringExtra("kode_mobil")

        // initialize tambahan slots
        for (i in 0 until 6) tambahanUris.add(null)

        setupHeader()
        setupImagePicker()
        setupNextButton()

        if (isEdit && kodeMobil != null) {
            loadFotosForEdit(kodeMobil!!)
        }
    }

    private fun setupHeader() {
        binding.layoutHeaderadd.iconClose.setOnClickListener { finish() }
    }

    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null && currentTarget != null) {
                    currentTarget!!.setImageURI(uri)
                    // save to corresponding var by id
                    when (currentTarget!!.id) {
                        binding.foto360.id -> uri360 = uri.toString()
                        binding.fotoDepan.id -> uriDepan = uri.toString()
                        binding.fotoBelakang.id -> uriBelakang = uri.toString()
                        binding.tampilanSamping.id -> uriSamping = uri.toString()
                        binding.addfoto1.id -> tambahanUris[0] = uri.toString()
                        binding.addfoto2.id -> tambahanUris[1] = uri.toString()
                        binding.addfoto3.id -> tambahanUris[2] = uri.toString()
                        binding.addfoto4.id -> tambahanUris[3] = uri.toString()
                        binding.addfoto5.id -> tambahanUris[4] = uri.toString()
                        binding.addfoto6.id -> tambahanUris[5] = uri.toString()
                    }
                }
            }
        }

        val listImages = listOf(
            binding.foto360, binding.fotoDepan, binding.fotoBelakang, binding.tampilanSamping,
            binding.addfoto1, binding.addfoto2, binding.addfoto3, binding.addfoto4, binding.addfoto5, binding.addfoto6
        )

        listImages.forEach { img ->
            img.setOnClickListener {
                currentTarget = img
                val pick = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                pickImageLauncher.launch(pick)
            }
        }
    }

    private fun loadFotosForEdit(kode: String) {
        ApiClient.apiService.getMobilDetail(kode)
            .enqueue(object : Callback<MobilDetailResponse> {
                override fun onResponse(call: Call<MobilDetailResponse>, response: Response<MobilDetailResponse>) {
                    val body = response.body() ?: return
                    if (!body.success) return

                    // foto datang sebagai list; isi sesuai tipe_foto
                    val tambahanSlots = listOf(binding.addfoto1, binding.addfoto2, binding.addfoto3, binding.addfoto4, binding.addfoto5, binding.addfoto6)
                    var tambahanIndex = 0
                    body.foto.forEach { f ->
                        val url = f.foto
                        when (f.tipe_foto.lowercase()) {
                            "360" -> { uri360 = url; loadInto(binding.foto360, url) }
                            "depan" -> { uriDepan = url; loadInto(binding.fotoDepan, url) }
                            "belakang" -> { uriBelakang = url; loadInto(binding.fotoBelakang, url) }
                            "samping" -> { uriSamping = url; loadInto(binding.tampilanSamping, url) }
                            else -> { // semua yg bukan 360, depan, belakang, samping = tambahan
                                if (tambahanIndex < tambahanSlots.size) {
                                    tambahanUris[tambahanIndex] = url
                                    loadInto(tambahanSlots[tambahanIndex], url)
                                    tambahanIndex++
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MobilDetailResponse>, t: Throwable) {}
            })
    }

    private fun loadInto(img: ImageView, url: String) {
        Glide.with(this).load(url).centerCrop().into(img)
    }

    private fun setupNextButton() {
        binding.footerSave1.btnNext.setOnClickListener {
            // no required fields in step1 — but we still pass collected URIs forward
            val intent = Intent(this, AddCarStep2Activity::class.java)
            intent.putExtra("is_edit", isEdit)
            intent.putExtra("kode_mobil", kodeMobil)

            // pass image URIs (may be null)
            intent.putExtra("foto_360", uri360)
            intent.putExtra("foto_depan", uriDepan)
            intent.putExtra("foto_belakang", uriBelakang)
            intent.putExtra("foto_samping", uriSamping)
            // tambahan as string array
            intent.putExtra("foto_tambahan", tambahanUris.map { it ?: "" }.toTypedArray())

            startActivity(intent)
        }
    }
}
