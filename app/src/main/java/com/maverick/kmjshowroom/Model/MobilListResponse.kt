package com.maverick.kmjshowroom.Model

import java.math.BigInteger

data class MobilListResponse(
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
    val full_prize: BigInteger,
    val angsuran: Int,
    val tenor: Int,
    val dp: Int,
    val status: String,
    val foto: String?
)

