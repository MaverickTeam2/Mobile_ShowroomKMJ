package com.maverick.kmjshowroom.Database

data class CachedCar(
    val kodeMobil: String,
    val title: String,
    val tahun: String,
    val warna: String,
    val status: String,
    val jarakTempuh: String,
    val bahanBakar: String,
    val hargaAngsuran: String,
    val hargaDp: String,
    val fotoUtama: String // URL foto utama
)
