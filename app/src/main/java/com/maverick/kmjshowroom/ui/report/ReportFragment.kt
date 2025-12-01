package com.maverick.kmjshowroom.ui.report

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.LaporanGabunganResponse
import com.maverick.kmjshowroom.Model.ReportPenjualanResponse
import com.maverick.kmjshowroom.Model.TransaksiLaporan
import com.maverick.kmjshowroom.Model.MobilLaporan
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentReportBinding
import com.maverick.kmjshowroom.utils.FileUtils
import com.maverick.kmjshowroom.utils.LoadingDialog
import com.maverick.kmjshowroom.utils.PdfGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private var reportDialog: Dialog? = null
    private var salesReportContent: String = ""
    private var incomeReportContent: String = ""
    private var stockReportContent: String = ""
    private var cachedTransaksi: List<TransaksiLaporan> = emptyList()
    private var cachedMobil: List<MobilLaporan> = emptyList()
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireContext())
        setupListeners()
        loadInitialStats()
        return binding.root
    }

    @SuppressLint("NewApi")
    private fun setupListeners() {
        binding.btnGenerate.setOnClickListener { loadAndShowSalesReport() }
        binding.btnGenerateIncome.setOnClickListener { loadAndShowIncomeReport() }
        binding.btnGenerateStock?.setOnClickListener { loadAndShowStockReport() }

        binding.ivDownload.setOnClickListener { downloadReportPdf("sales") }
        binding.ivDownloadIncome.setOnClickListener { downloadReportPdf("income") }
        binding.ivDownloadStock?.setOnClickListener { downloadReportPdf("stock") }

        binding.btnExportPDF.setOnClickListener { exportAllReportsToPdf() }
        binding.btnExportExcel.setOnClickListener { exportAllReportsToExcel() }
    }

    private fun loadInitialStats() {
        // Load dari laporan gabungan
        ApiClient.apiService.getLaporanGabungan().enqueue(object : Callback<LaporanGabunganResponse> {
            override fun onResponse(call: Call<LaporanGabunganResponse>, response: Response<LaporanGabunganResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    val data = response.body()?.data
                    cachedTransaksi = data?.transaksi ?: emptyList()
                    cachedMobil = data?.mobil ?: emptyList()

                    // Hitung stats
                    val completedTransaksi = cachedTransaksi.filter { it.status == "completed" }
                    val totalPendapatan = completedTransaksi.sumOf { it.hargaAkhir }
                    val availableMobil = cachedMobil.filter { it.status == "available" }

                    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                    binding.tvStat.text = "${completedTransaksi.size} Sales"
                    binding.tvStatIncome.text = formatter.format(totalPendapatan)
                    binding.tvStatStock?.text = "${availableMobil.size} Cars"
                } else {
                    setDefaultStats()
                }
            }

            override fun onFailure(call: Call<LaporanGabunganResponse>, t: Throwable) {
                setDefaultStats()
            }
        })
    }

    private fun setDefaultStats() {
        binding.tvStat.text = "0 Sales"
        binding.tvStatIncome.text = "Rp 0"
        binding.tvStatStock?.text = "0 Cars"
    }

    // ==================== SALES REPORT ====================
    private fun loadAndShowSalesReport() {
        showLoading("Memuat laporan penjualan...")
        ApiClient.apiService.getLaporanPenjualan().enqueue(object : Callback<ReportPenjualanResponse> {
            override fun onResponse(call: Call<ReportPenjualanResponse>, response: Response<ReportPenjualanResponse>) {
                hideLoading()
                if (response.isSuccessful && response.body()?.status == true) {
                    val data = response.body()?.data
                    salesReportContent = generateSalesReportContent(data)
                    showReportDialog("Laporan Penjualan Bulanan", salesReportContent)
                } else {
                    showError("Gagal memuat laporan: ${response.body()?.message ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: Call<ReportPenjualanResponse>, t: Throwable) {
                hideLoading()
                showError("Error: ${t.message}")
            }
        })
    }

    private fun generateSalesReportContent(data: com.maverick.kmjshowroom.Model.ReportPenjualanData?): String {
        if (data == null) return "Data tidak tersedia"
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        return buildString {
            append("LAPORAN PENJUALAN BULANAN\n")
            append("═".repeat(40))
            append("\n\n")
            append("Total Laporan  : ${data.ringkasan.totalLaporan}\n")
            append("Total Transaksi: ${data.ringkasan.totalTransaksi}\n")
            append("Total Pendapatan: ${formatter.format(data.ringkasan.totalPendapatan)}\n")
            append("Rata-rata/Bulan: ${String.format("%.1f", data.ringkasan.rataRataTransaksi)} transaksi\n\n")

            if (data.items.isNotEmpty()) {
                append("Detail Per Periode:\n")
                append("─".repeat(40))
                append("\n")
                data.items.forEachIndexed { i, item ->
                    append("${i + 1}. ${item.periode}\n")
                    append("   Transaksi: ${item.totalTransaksi}\n")
                    append("   Pendapatan: ${formatter.format(item.totalPendapatan)}\n\n")
                }
            } else {
                append("Belum ada data penjualan.\n")
            }
        }
    }

    // ==================== INCOME REPORT ====================
    private fun loadAndShowIncomeReport() {
        showLoading("Memuat analisis pendapatan...")

        if (cachedTransaksi.isNotEmpty()) {
            hideLoading()
            incomeReportContent = generateIncomeReportContent(cachedTransaksi)
            showReportDialog("Analisis Pendapatan", incomeReportContent)
            return
        }

        ApiClient.apiService.getLaporanGabungan().enqueue(object : Callback<LaporanGabunganResponse> {
            override fun onResponse(call: Call<LaporanGabunganResponse>, response: Response<LaporanGabunganResponse>) {
                hideLoading()
                if (response.isSuccessful && response.body()?.status == 200) {
                    cachedTransaksi = response.body()?.data?.transaksi ?: emptyList()
                    incomeReportContent = generateIncomeReportContent(cachedTransaksi)
                    showReportDialog("Analisis Pendapatan", incomeReportContent)
                } else {
                    showError("Gagal memuat laporan pendapatan")
                }
            }

            override fun onFailure(call: Call<LaporanGabunganResponse>, t: Throwable) {
                hideLoading()
                showError("Error: ${t.message}")
            }
        })
    }

    private fun generateIncomeReportContent(data: List<TransaksiLaporan>): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        return buildString {
            append("ANALISIS PENDAPATAN\n")
            append("═".repeat(40))
            append("\n\n")

            if (data.isEmpty()) {
                append("Belum ada transaksi.\n")
            } else {
                var total = 0.0
                append("Daftar Transaksi:\n")
                append("─".repeat(40))
                append("\n")

                data.forEachIndexed { i, item ->
                    append("${i + 1}. ${item.kodeTransaksi}\n")
                    append("   Pembeli: ${item.namaPembeli}\n")
                    append("   Mobil  : ${item.namaMobil ?: "-"}\n")
                    append("   Harga  : ${formatter.format(item.hargaAkhir)}\n")
                    append("   Tanggal: ${item.tanggal}\n")
                    append("   Status : ${item.status}\n\n")
                    total += item.hargaAkhir
                }

                append("─".repeat(40))
                append("\n")
                append("TOTAL: ${formatter.format(total)}\n")
                append("Jumlah: ${data.size} transaksi\n")
                if (data.isNotEmpty()) {
                    append("Rata-rata: ${formatter.format(total / data.size)}\n")
                }
            }
        }
    }

    // ==================== STOCK REPORT ====================
    private fun loadAndShowStockReport() {
        showLoading("Memuat laporan stok...")

        if (cachedMobil.isNotEmpty()) {
            hideLoading()
            stockReportContent = generateStockReportContent(cachedMobil)
            showReportDialog("Laporan Stok", stockReportContent)
            return
        }

        ApiClient.apiService.getLaporanGabungan().enqueue(object : Callback<LaporanGabunganResponse> {
            override fun onResponse(call: Call<LaporanGabunganResponse>, response: Response<LaporanGabunganResponse>) {
                hideLoading()
                if (response.isSuccessful && response.body()?.status == 200) {
                    cachedMobil = response.body()?.data?.mobil ?: emptyList()
                    stockReportContent = generateStockReportContent(cachedMobil)
                    showReportDialog("Laporan Stok", stockReportContent)
                } else {
                    showError("Gagal memuat laporan stok")
                }
            }

            override fun onFailure(call: Call<LaporanGabunganResponse>, t: Throwable) {
                hideLoading()
                showError("Error: ${t.message}")
            }
        })
    }

    private fun generateStockReportContent(data: List<MobilLaporan>): String {
        val available = data.filter { it.status == "available" }
        val sold = data.filter { it.status == "sold" }
        val reserved = data.filter { it.status == "reserved" }

        return buildString {
            append("LAPORAN STOK MOBIL\n")
            append("═".repeat(40))
            append("\n\n")
            append("Total Stok : ${data.size} unit\n")
            append("Tersedia   : ${available.size} unit\n")
            append("Terjual    : ${sold.size} unit\n")
            append("Reserved   : ${reserved.size} unit\n\n")

            if (data.isNotEmpty()) {
                append("Daftar Mobil:\n")
                append("─".repeat(40))
                append("\n")
                data.forEachIndexed { i, item ->
                    append("${i + 1}. ${item.namaMobil}\n")
                    append("   Tahun: ${item.tahunMobil ?: "-"} | Status: ${item.status}\n\n")
                }
            } else {
                append("Belum ada data mobil.\n")
            }
        }
    }

    // ==================== DOWNLOAD & EXPORT ====================
    @SuppressLint("NewApi")
    private fun downloadReportPdf(type: String) {
        val content = when (type) {
            "sales" -> salesReportContent
            "income" -> incomeReportContent
            "stock" -> stockReportContent
            else -> ""
        }

        if (content.isEmpty()) {
            showError("Generate laporan terlebih dahulu")
            return
        }

        val fileName = when (type) {
            "sales" -> "Laporan_Penjualan.pdf"
            "income" -> "Analisis_Pendapatan.pdf"
            "stock" -> "Laporan_Stok.pdf"
            else -> "Laporan.pdf"
        }

        generateAndOpenPdf(content, fileName)
    }

    private fun exportAllReportsToPdf() {
        showLoading("Mengumpulkan semua data...")

        ApiClient.apiService.getLaporanGabungan().enqueue(object : Callback<LaporanGabunganResponse> {
            override fun onResponse(call: Call<LaporanGabunganResponse>, response: Response<LaporanGabunganResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    cachedTransaksi = response.body()?.data?.transaksi ?: emptyList()
                    cachedMobil = response.body()?.data?.mobil ?: emptyList()

                    incomeReportContent = generateIncomeReportContent(cachedTransaksi)
                    stockReportContent = generateStockReportContent(cachedMobil)

                    // Load sales report
                    ApiClient.apiService.getLaporanPenjualan().enqueue(object : Callback<ReportPenjualanResponse> {
                        override fun onResponse(call: Call<ReportPenjualanResponse>, response: Response<ReportPenjualanResponse>) {
                            hideLoading()
                            salesReportContent = if (response.isSuccessful && response.body()?.status == true) {
                                generateSalesReportContent(response.body()?.data)
                            } else {
                                "Data penjualan tidak tersedia"
                            }

                            val combined = buildString {
                                append("LAPORAN LENGKAP SHOWROOM KMJ\n")
                                append("═".repeat(50))
                                append("\n\n")
                                append(salesReportContent)
                                append("\n\n")
                                append("═".repeat(50))
                                append("\n\n")
                                append(incomeReportContent)
                                append("\n\n")
                                append("═".repeat(50))
                                append("\n\n")
                                append(stockReportContent)
                            }

                            generateAndOpenPdf(combined, "Laporan_Lengkap_${System.currentTimeMillis()}.pdf")
                        }

                        override fun onFailure(call: Call<ReportPenjualanResponse>, t: Throwable) {
                            hideLoading()
                            showError("Gagal memuat laporan penjualan")
                        }
                    })
                } else {
                    hideLoading()
                    showError("Gagal memuat data")
                }
            }

            override fun onFailure(call: Call<LaporanGabunganResponse>, t: Throwable) {
                hideLoading()
                showError("Error: ${t.message}")
            }
        })
    }

    private fun exportAllReportsToExcel() {
        showLoading("Mengekspor ke CSV...")

        ApiClient.apiService.getLaporanGabungan().enqueue(object : Callback<LaporanGabunganResponse> {
            @SuppressLint("NewApi")
            override fun onResponse(call: Call<LaporanGabunganResponse>, response: Response<LaporanGabunganResponse>) {
                hideLoading()
                if (response.isSuccessful && response.body()?.status == 200) {
                    val transaksi = response.body()?.data?.transaksi ?: emptyList()
                    val mobil = response.body()?.data?.mobil ?: emptyList()

                    try {
                        val csvString = generateCsvContent(transaksi, mobil)
                        val fileName = "Laporan_Lengkap_${System.currentTimeMillis()}.csv"

                        val file = FileUtils.saveToDownload(
                            requireContext(),
                            fileName,
                            csvString.toByteArray()
                        )

                        if (file == null) {
                            showError("File CSV gagal disimpan!")
                            return
                        }

                        openFile(file, "application/vnd.ms-excel")
                        Toast.makeText(requireContext(), "Export CSV berhasil: ${file.name}", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        showError("Gagal export: ${e.message}")
                    }
                } else {
                    showError("Gagal memuat data")
                }
            }

            override fun onFailure(call: Call<LaporanGabunganResponse>, t: Throwable) {
                hideLoading()
                showError("Error: ${t.message}")
            }
        })
    }

    private fun generateCsvContent(
        transaksi: List<TransaksiLaporan>,
        mobil: List<MobilLaporan>
    ): String {
        val builder = StringBuilder()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))

        builder.append("LAPORAN LENGKAP SHOWROOM KMJ\n")
        builder.append("Tanggal Export,${dateFormat.format(Date())}\n\n")

        builder.append("=== LAPORAN TRANSAKSI ===\n")
        builder.append("Kode Transaksi,Nama Pembeli,Nama Mobil,Harga Akhir,Tanggal,Status\n")
        transaksi.forEach { item ->
            builder.append("${item.kodeTransaksi},${item.namaPembeli},${item.namaMobil ?: "-"},${item.hargaAkhir},${item.tanggal},${item.status}\n")
        }
        builder.append("\n")

        builder.append("=== LAPORAN STOK MOBIL ===\n")
        builder.append("Kode Mobil,Nama Mobil,Tahun,Status,Harga\n")
        mobil.forEach { item ->
            builder.append("${item.kodeMobil},${item.namaMobil},${item.tahunMobil ?: "-"},${item.status},${item.fullPrize ?: 0}\n")
        }

        return builder.toString()
    }

    // ==================== HELPER FUNCTIONS ====================
    @SuppressLint("NewApi")
    private fun generateAndOpenPdf(content: String, fileName: String) {
        try {
            showLoading("Membuat PDF...")

            val pdfBytes = PdfGenerator.generatePdf(requireContext(), content)
            val file = FileUtils.saveToDownload(requireContext(), fileName, pdfBytes)
            if (file == null) {
                showError("File gagal dibuat!")
                return
            }
            hideLoading()
            openFile(file, "application/pdf")

            Toast.makeText(requireContext(), "PDF tersimpan di folder Download", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            hideLoading()
            showError("Gagal membuat PDF: ${e.message}")
        }
    }


    private fun showReportDialog(title: String, content: String) {
        reportDialog?.dismiss()
        reportDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_report)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            findViewById<TextView>(R.id.tvReportContent).text = content
            findViewById<Button>(R.id.btnClose).setOnClickListener { dismiss() }
            show()
        }
    }

    private fun openFile(file: File, mimeType: String) {
        try {
            val context = requireContext()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

            // Cek jika ada aplikasi yang bisa buka file
            val chooser = Intent.createChooser(intent, "Buka dengan")
            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(context, "Tidak ada aplikasi untuk membuka file ini", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "File tersimpan: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }


    private fun showLoading(msg: String = "Memuat...") {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun hideLoading() {}

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reportDialog?.dismiss()
        reportDialog = null
        _binding = null
    }
}