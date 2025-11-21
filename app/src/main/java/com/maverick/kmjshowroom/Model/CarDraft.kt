package com.maverick.kmjshowroom.Model

import android.net.Uri

/**
 * Simple in-memory draft used to carry data across steps (no SQLite).
 * Reset after successful insert (or explicitly).
 */
object CarDraft {
    // Step1 images
    var foto360: Uri? = null
    var fotoDepan: Uri? = null
    var fotoBelakang: Uri? = null
    var fotoSamping: Uri? = null
    val fotoTambahan: MutableList<Uri?> = MutableList(6) { null } // index 0..5

    // Step2 general
    var namaMobil: String = ""
    var jarakTempuh: String = "0"
    var fullPrize: String = "0"   // note: matches API field 'full_prize'
    var tahunMobil: String = ""
    var jenisKendaraan: String = ""
    var bahanBakar: String = ""
    var sistemPenggerak: String = ""
    var warnaExterior: String = ""
    var warnaInterior: String = ""
    var uangMuka: String = "0"
    var angsuran: String = "0"
    var tenor: String = "0"

    // Step3 fitur (ids)
    val fitur: MutableList<Int> = mutableListOf()

    // Step4 status (lowercase: available, reserved, sold, shipping, delivered)
    var status: String = "available"

    fun clear() {
        foto360 = null
        fotoDepan = null
        fotoBelakang = null
        fotoSamping = null
        for (i in 0 until fotoTambahan.size) fotoTambahan[i] = null

        namaMobil = ""
        jarakTempuh = "0"
        fullPrize = "0"
        tahunMobil = ""
        jenisKendaraan = ""
        bahanBakar = ""
        sistemPenggerak = ""
        warnaExterior = ""
        warnaInterior = ""
        uangMuka = "0"
        angsuran = "0"
        tenor = "0"
        fitur.clear()
        status = "available"
    }
}
