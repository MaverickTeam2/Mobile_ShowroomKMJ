package com.maverick.kmjshowroom.Model

data class MobilListResponse(
    val success: Boolean,
    val code: Int,
    val data: List<MobilItem>
)

data class MobilItem(
    val kode_mobil: String,
    val nama_mobil: String,
    val tahun_mobil: Int,
    val warna_exterior: String,
    val tipe_bahan_bakar: String,
    val jarak_tempuh: Int,
    val full_prize: Int,
    val angsuran: Int,
    val tenor: Int,
    val dp: Int,
    val status: String,
    val foto: String?
)

