package com.maverick.kmjshowroom.Model

data class MobilListResponse(
    val success: Boolean,
    val data: List<MobilItem>
)

data class MobilItem(
    val kode_mobil: String,
    val nama_mobil: String,
    val tahun_mobil: String,
    val warna_exterior: String,
    val tipe_bahan_bakar: String,
    val jarak_tempuh: String,
    val angsuran: Int,
    val dp: Int,
    val status: String,
    val foto: String?
)
