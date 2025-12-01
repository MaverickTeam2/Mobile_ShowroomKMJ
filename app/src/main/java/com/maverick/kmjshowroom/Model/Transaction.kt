package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName


// Response untuk list transaksi
data class TransaksiListResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("data")
    val data: List<TransaksiItem>
)

// Item transaksi dari API
data class TransaksiItem(
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,

    @SerializedName("nama_pembeli")
    val namaPembeli: String,

    @SerializedName("no_hp")
    val noHp: String?,

    @SerializedName("tipe_pembayaran")
    val tipePembayaran: String?,

    @SerializedName("nama_mobil")
    val namaMobil: String?,

    @SerializedName("harga_asli")
    val hargaAsli: Double?,

    @SerializedName("tenor")
    val tenor: Int?,

    @SerializedName("angsuran")
    val angsuran: Double?,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("harga_akhir")
    val hargaAkhir: Double,

    @SerializedName("kasir")
    val kasir: String?
)

// Response untuk detail transaksi
data class TransaksiDetailResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("data")
    val data: TransaksiDetail
)

// Detail lengkap transaksi
data class TransaksiDetail(
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,

    @SerializedName("kode_mobil")
    val kodeMobil: String,

    @SerializedName("nama_pembeli")
    val namaPembeli: String,

    @SerializedName("no_hp")
    val noHp: String?,

    @SerializedName("tipe_pembayaran")
    val tipePembayaran: String,

    @SerializedName("nama_kredit")
    val namaKredit: String?,

    @SerializedName("harga_akhir")
    val hargaAkhir: Double,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("note")
    val note: String?,

    @SerializedName("nama_mobil")
    val namaMobil: String?,

    @SerializedName("tahun_mobil")
    val tahunMobil: Int?,

    @SerializedName("tipe_mobil")
    val tipeMobil: String?,

    @SerializedName("full_price")
    val fullPrice: Double?,

    @SerializedName("kasir")
    val kasir: String?,

    @SerializedName("jaminan")
    val jaminan: JaminanFlags?
)

// Flags untuk jaminan
data class JaminanFlags(
    @SerializedName("ktp")
    val ktp: Int = 0,

    @SerializedName("kk")
    val kk: Int = 0,

    @SerializedName("rekening")
    val rekening: Int = 0
)

data class CreateTransaksiResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CreateTransaksiData?
)

data class CreateTransaksiData(
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,

    @SerializedName("status")
    val status: String
)

data class Transaction(
    val id: String,
    val date: String,
    val customerName: String,
    val phoneNumber: String,
    val car: String,
    val description: String,
    val price: String,
    val dealPrice: String,
    var status: String
)