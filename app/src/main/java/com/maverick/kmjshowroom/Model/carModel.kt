package com.maverick.kmjshowroom.Model

import java.math.BigInteger


data class CarData(
    val kodeMobil: String = "",
    val title: String,
    val year: String,
    val warna: String,
    val imageRes: Int = 0,
    val fotoUrl: String? = null,
    val status: String,
    val jarakTempuh: String,
    val bahanBakar: String,
    val tipeKendaraan: String = "",
    val angsuran: String,
    val dp: String,
    val fullPrice: BigInteger
)