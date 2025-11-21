package com.maverick.kmjshowroom.ui.report

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentReportBinding
import java.io.File
import android.widget.Toast

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var reportViewModel: ReportViewModel
    private var reportDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        reportViewModel = ViewModelProvider(this)[ReportViewModel::class.java]
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.btnGenerate.setOnClickListener { showReportDialog(generateSalesReport()) }
        binding.btnGenerateIncome.setOnClickListener { showReportDialog(generateIncomeReport()) }
        binding.btnGenerateStock?.setOnClickListener { showReportDialog(generateStockReport()) } // TAMBAH ?
        binding.ivDownload.setOnClickListener {
            generateAndDownloadPdf(generateSalesReport(), "Laporan_Penjualan_Bulanan.pdf")
        }
        binding.ivDownloadIncome.setOnClickListener {
            generateAndDownloadPdf(generateIncomeReport(), "Analisis_Pendapatan.pdf")
        }
        binding.ivDownloadStock?.setOnClickListener {
            generateAndDownloadPdf(generateStockReport(), "Laporan_Stok_Mobil.pdf")
        }

        binding.btnExportPDF.setOnClickListener { exportPdf() }
        binding.btnExportExcel.setOnClickListener { exportExcel() }

        return root
    }

    private fun generateAndDownloadPdf(content: String, fileName: String) {
        try {
            val file = com.maverick.kmjshowroom.utils.PdfGenerator.generatePdf(requireContext(), content, fileName)
            openFile(file, "application/pdf")
            Toast.makeText(requireContext(), "Download selesai: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal download: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        reportDialog?.dismiss()
    }

    // === FUNGSI GENERATE LAPORAN ===
    private fun generateIncomeReport(): String {
        val data = com.maverick.kmjshowroom.Model.PendapatanDummy.data
        return buildString {
            append("=== ANALISIS PENDAPATAN ===\n\n")
            data.forEach {
                append("${it.kode_transaksi} - ${it.nama_pembeli} - Rp${it.harga_akhir}\n")
            }
        }
    }

    private fun generateSalesReport(): String {
        return buildString {
            append("=== LAPORAN PENJUALAN BULANAN ===\n\n")
            append("Bulan: November 2025\n")
            append("Total Transaksi: 45\n")
            append("Pendapatan Kotor: Rp 1.250.000.000\n")
            append("Mobil Terjual: 12 unit\n")
            append("Rata-rata Harga: Rp 104.166.667\n\n")
            append("Detail:\n")
            append("- Toyota Avanza: 5 unit\n")
            append("- Honda HR-V: 4 unit\n")
            append("- Mitsubishi Xpander: 3 unit\n")
        }
    }

    private fun generateStockReport(): String {
        return buildString {
            append("=== LAPORAN STOK MOBIL ===\n\n")
            append("Total Stok: 28 unit\n")
            append("Tersedia: 22 unit\n")
            append("Terjual: 6 unit\n")
            append("Booking: 3 unit\n\n")
            append("Daftar Mobil:\n")
            append("- Toyota Avanza [Sisa: 8]\n")
            append("- Honda HR-V [Sisa: 6]\n")
            append("- Mitsubishi Xpander [Sisa: 5]\n")
            append("- Suzuki Ertiga [Sisa: 3]\n")
        }
    }

    // === TAMPILKAN DI DIALOG ===
    private fun showReportDialog(content: String) {
        reportDialog?.dismiss()
        reportDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_report)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            findViewById<TextView>(R.id.tvReportContent).text = content
            findViewById<Button>(R.id.btnClose).setOnClickListener { dismiss() }
            show()
        }
    }

    private fun exportPdf() {
        val fullReport = buildString {
            append(generateSalesReport())
            append("\n\n")
            append(generateIncomeReport())
            append("\n\n")
            append(generateStockReport())
        }

        val fileName = "Laporan_Lengkap_KMJ_Showroom.pdf"
        val file = com.maverick.kmjshowroom.utils.PdfGenerator.generatePdf(
            requireContext(),
            fullReport,
            fileName
        )
        openFile(file, "application/pdf")
        Toast.makeText(requireContext(), "PDF lengkap berhasil diekspor!", Toast.LENGTH_LONG).show()
    }

    private fun exportExcel() {
        val file = com.maverick.kmjshowroom.utils.ExcelGenerator.generateExcel(requireContext())
        openFile(file, "application/vnd.ms-excel")
    }

    private fun openFile(file: File, type: String) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, type)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}