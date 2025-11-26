package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName

data class TransaksiItemResponse(
    @SerializedName("kodeTransaksi")
    val kodeTransaksi: String,

    @SerializedName("namaPembeli")
    val namaPembeli: String,

    @SerializedName("namaMobil")
    val namaMobil: String?,

    @SerializedName("hargaAkhir")
    val hargaAkhir: Double,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("status")
    val status: String
)