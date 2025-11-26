package com.maverick.kmjshowroom.Model

data class Pendapatan(
    val kode_transaksi: String,
    val kode_user: String,
    val kode_mobil: String,
    val tipe_pembayaran: String,
    val harga_akhir: Double,
    val nama_pembeli: String,
    val no_hp: String,
    val status: String,
    val created_at: String
)