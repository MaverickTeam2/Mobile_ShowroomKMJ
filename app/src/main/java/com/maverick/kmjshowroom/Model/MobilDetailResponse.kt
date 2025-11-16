package com.maverick.kmjshowroom.Model

data class MobilDetailResponse(
    val success: Boolean,
    val mobil: MobilDetail,
    val foto: List<MobilFoto>,
    val fitur: List<Int>
)

data class MobilDetail(
    val kode_mobil: String,
    val nama_mobil: String,
    val tahun: Int,
    val warna: String,
    val bahan_bakar: String,
    val jarak_tempuh: Int,
    val harga_cicilan: Int,
    val angsuran: Int,
    val dp: Int,
    val status: String
)

data class MobilFoto(
    val id_foto: Int,
    val foto: String,
    val tipe: String
)
