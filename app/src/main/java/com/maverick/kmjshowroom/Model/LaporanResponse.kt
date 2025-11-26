package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName

data class LaporanGabunganResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LaporanGabunganData?
)

data class LaporanGabunganData(
    @SerializedName("transaksi")
    val transaksi: List<TransaksiLaporan>,

    @SerializedName("mobil")
    val mobil: List<MobilLaporan>
)

data class TransaksiLaporan(
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,

    @SerializedName("nama_pembeli")
    val namaPembeli: String,

    @SerializedName("tipe_pembayaran")
    val tipePembayaran: String?,

    @SerializedName("harga_akhir")
    val hargaAkhir: Double,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("nama_mobil")
    val namaMobil: String?
)

data class MobilLaporan(
    @SerializedName("kode_mobil")
    val kodeMobil: String,

    @SerializedName("nama_mobil")
    val namaMobil: String,

    @SerializedName("tahun_mobil")
    val tahunMobil: Int?,

    @SerializedName("jenis_kendaraan")
    val jenisKendaraan: String?,

    @SerializedName("status")
    val status: String,

    @SerializedName("full_prize")
    val fullPrize: Double?
)

data class ReportPenjualanResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReportPenjualanData?
)

data class ReportPenjualanData(
    @SerializedName("ringkasan")
    val ringkasan: RingkasanPenjualan,

    @SerializedName("items")
    val items: List<ItemPenjualan>
)

data class RingkasanPenjualan(
    @SerializedName("total_laporan")
    val totalLaporan: Int,

    @SerializedName("total_pendapatan")
    val totalPendapatan: Double,

    @SerializedName("total_transaksi")
    val totalTransaksi: Int,

    @SerializedName("rata_rata_transaksi")
    val rataRataTransaksi: Double
)

data class ItemPenjualan(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama_laporan")
    val namaLaporan: String,

    @SerializedName("periode")
    val periode: String,

    @SerializedName("tanggal_generate")
    val tanggalGenerate: String,

    @SerializedName("total_transaksi")
    val totalTransaksi: Int,

    @SerializedName("total_pendapatan")
    val totalPendapatan: Double
)