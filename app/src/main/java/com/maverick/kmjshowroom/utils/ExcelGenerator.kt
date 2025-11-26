package com.maverick.kmjshowroom.utils

import android.content.Context
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.LaporanGabunganResponse
import com.maverick.kmjshowroom.Model.ReportPenjualanResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelGenerator {

    fun generateExcel(context: Context): File {
        val file = File(context.getExternalFilesDir(null), "Laporan_Lengkap_${System.currentTimeMillis()}.csv")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))

        FileWriter(file).use { writer ->
            writer.write("LAPORAN LENGKAP SHOWROOM KMJ\n")
            writer.write("Tanggal Export: ${dateFormat.format(Date())}\n")
            writer.write("\n")
            writer.write("=== DATA AKAN DIMUAT ===\n")
        }

        return file
    }

    fun generateExcelWithData(
        context: Context,
        onComplete: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        val file = File(context.getExternalFilesDir(null), "Laporan_Lengkap_${System.currentTimeMillis()}.csv")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))

        val salesData = StringBuilder()
        val transaksiData = StringBuilder()
        val stockData = StringBuilder()

        var salesLoaded = false
        var gabunganLoaded = false

        fun checkAndWrite() {
            if (salesLoaded && gabunganLoaded) {
                try {
                    FileWriter(file).use { writer ->
                        writer.write("LAPORAN LENGKAP SHOWROOM KMJ\n")
                        writer.write("Tanggal Export,${dateFormat.format(Date())}\n")
                        writer.write("\n")

                        // Sales Section
                        writer.write("=== LAPORAN PENJUALAN ===\n")
                        writer.write(salesData.toString())
                        writer.write("\n")

                        // Transaksi/Income Section
                        writer.write("=== LAPORAN TRANSAKSI ===\n")
                        writer.write("Kode Transaksi,Nama Pembeli,Nama Mobil,Harga Akhir,Tanggal,Status\n")
                        writer.write(transaksiData.toString())
                        writer.write("\n")

                        // Stock Section
                        writer.write("=== LAPORAN STOK MOBIL ===\n")
                        writer.write(stockData.toString())
                    }
                    onComplete(file)
                } catch (e: Exception) {
                    onError("Gagal menulis file: ${e.message}")
                }
            }
        }

        // Load Sales Data dari report_penjualan.php
        ApiClient.apiService.getLaporanPenjualan().enqueue(object : Callback<ReportPenjualanResponse> {
            override fun onResponse(call: Call<ReportPenjualanResponse>, response: Response<ReportPenjualanResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val data = response.body()?.data
                    salesData.append("Total Laporan,${data?.ringkasan?.totalLaporan ?: 0}\n")
                    salesData.append("Total Transaksi,${data?.ringkasan?.totalTransaksi ?: 0}\n")
                    salesData.append("Total Pendapatan,${data?.ringkasan?.totalPendapatan ?: 0}\n")
                    salesData.append("Rata-rata Transaksi/Bulan,${data?.ringkasan?.rataRataTransaksi ?: 0}\n")
                    salesData.append("\nDetail Per Periode:\n")
                    salesData.append("No,Periode,Total Transaksi,Total Pendapatan\n")
                    data?.items?.forEachIndexed { index, item ->
                        salesData.append("${index + 1},${item.periode},${item.totalTransaksi},${item.totalPendapatan}\n")
                    }
                } else {
                    salesData.append("Gagal memuat data penjualan\n")
                }
                salesLoaded = true
                checkAndWrite()
            }

            override fun onFailure(call: Call<ReportPenjualanResponse>, t: Throwable) {
                salesData.append("Gagal memuat data penjualan: ${t.message}\n")
                salesLoaded = true
                checkAndWrite()
            }
        })

        // Load Gabungan Data (Transaksi + Mobil) dari get_laporan_gabungan.php
        ApiClient.apiService.getLaporanGabungan().enqueue(object : Callback<LaporanGabunganResponse> {
            override fun onResponse(call: Call<LaporanGabunganResponse>, response: Response<LaporanGabunganResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    val data = response.body()?.data

                    // Transaksi
                    val transaksiList = data?.transaksi ?: emptyList()
                    var totalPendapatan = 0.0
                    transaksiList.forEach { item ->
                        val namaPembeli = item.namaPembeli.replace(",", ";")
                        val namaMobil = (item.namaMobil ?: "-").replace(",", ";")
                        transaksiData.append("${item.kodeTransaksi},$namaPembeli,$namaMobil,${item.hargaAkhir},${item.tanggal},${item.status}\n")
                        totalPendapatan += item.hargaAkhir
                    }
                    transaksiData.append("\nTotal Transaksi,${transaksiList.size}\n")
                    transaksiData.append("Total Pendapatan,$totalPendapatan\n")

                    // Mobil/Stock
                    val mobilList = data?.mobil ?: emptyList()
                    val available = mobilList.count { it.status == "available" }
                    val sold = mobilList.count { it.status == "sold" }
                    val reserved = mobilList.count { it.status == "reserved" }

                    stockData.append("Total Mobil,${mobilList.size}\n")
                    stockData.append("Tersedia,${available}\n")
                    stockData.append("Terjual,${sold}\n")
                    stockData.append("Reserved,${reserved}\n")
                    stockData.append("\nDaftar Mobil:\n")
                    stockData.append("No,Kode Mobil,Nama Mobil,Tahun,Status,Harga\n")
                    mobilList.forEachIndexed { index, item ->
                        val namaMobil = item.namaMobil.replace(",", ";")
                        stockData.append("${index + 1},${item.kodeMobil},$namaMobil,${item.tahunMobil ?: "-"},${item.status},${item.fullPrize ?: 0}\n")
                    }
                } else {
                    transaksiData.append("Gagal memuat data transaksi\n")
                    stockData.append("Gagal memuat data stok\n")
                }
                gabunganLoaded = true
                checkAndWrite()
            }

            override fun onFailure(call: Call<LaporanGabunganResponse>, t: Throwable) {
                transaksiData.append("Gagal memuat data: ${t.message}\n")
                stockData.append("Gagal memuat data: ${t.message}\n")
                gabunganLoaded = true
                checkAndWrite()
            }
        })
    }
}