package com.maverick.kmjshowroom.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.Model.MenuModel
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ActivityItem
import com.maverick.kmjshowroom.Model.DashboardResponse
import com.maverick.kmjshowroom.Model.LaporanGabunganResponse
import com.maverick.kmjshowroom.Model.MobilLaporan
import com.maverick.kmjshowroom.Model.ReportPenjualanResponse
import com.maverick.kmjshowroom.Model.TransaksiLaporan
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentHomeBinding
import com.maverick.kmjshowroom.ui.appointment.AppointmentActivity
import com.maverick.kmjshowroom.ui.car.AddCarStep1Activity
import com.maverick.kmjshowroom.ui.setting.SettingActivity
import com.maverick.kmjshowroom.ui.transaksi.AddTrnActivity1
import com.maverick.kmjshowroom.utils.FileUtils
import com.maverick.kmjshowroom.utils.PdfGenerator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var salesReportContent: String = ""
    private lateinit var adapter: RecentActivityAdapter
    private val recentList = mutableListOf<ActivityItem>()
    private var loadingCount = 0
    private var cachedTransaksi: List<TransaksiLaporan> = emptyList()
    private var cachedMobil: List<MobilLaporan> = emptyList()
    private var incomeReportContent: String = ""
    private var stockReportContent: String = ""

    // Current filter
    private var currentFilter = "all"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.headerInclude.textHeader.text = "HOME"
        binding.headerInclude.searchBar.visibility = View.GONE

        binding.headerInclude.iconProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        binding.btnJanjiTemu.setOnClickListener {
            try {
                startActivity(Intent(requireContext(), AppointmentActivity::class.java))
                requireActivity().overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error membuka appointment: ${e.message}", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnTambahMobil.setOnClickListener {
            val intent = Intent(requireContext(), AddCarStep1Activity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        binding.btnTransaksi.setOnClickListener {
            val intent = Intent(requireContext(), AddTrnActivity1::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        binding.btnSemuaMenu.setOnClickListener {
            showAllMenuSheet()
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

        setupFilterSpinner()
        initRecycler()
        loadRecentActivity()
        loadDashboardStats()

        return root
    }

    private fun setupFilterSpinner() {
        val filterAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_options_recent,
            android.R.layout.simple_spinner_item
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = filterAdapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = when(position) {
                    0 -> "all"
                    1 -> "mobil"
                    2 -> "transaksi"
                    else -> "all"
                }
                loadRecentActivity()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun initRecycler() {
        adapter = RecentActivityAdapter(recentList) { item ->
            val intent = Intent(requireContext(), RecentDetailActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(intent)
        }

        binding.recyclerRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecent.adapter = adapter
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                loadRecentActivity()
                loadDashboardStats()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun loadRecentActivity() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val res = ApiClient.apiService.getRecentActivity(1000, currentFilter)
                if (res.isSuccessful && res.body()?.code == 200) {

                    val all = res.body()!!.data
                    recentList.clear()
                    recentList.addAll(all.take(5))
                    adapter.notifyDataSetChanged()

                    binding.btnShowMore.visibility =
                        if (all.size > 5) View.VISIBLE else View.GONE

                    binding.btnShowMore.setOnClickListener {
                        val intent = Intent(requireContext(), RecentAllActivity::class.java)
                        intent.putExtra("filter", currentFilter)
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showAllMenuSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val view = layoutInflater.inflate(R.layout.layout_bottom_all_menu, null)
        dialog.setContentView(view)

        val listMenu = listOf(
            MenuModel(R.drawable.ic_chat, "Janji Temu"),
            MenuModel(R.drawable.ic_add_car, "Tambah Mobil"),
            MenuModel(R.drawable.ic_trn, "Tambah Transaksi"),
            MenuModel(R.drawable.ic_lprn, "Download Laporan"),
            MenuModel(R.drawable.ic_profile, "Profil"),
            MenuModel(R.drawable.ic_set_general, "Setting General"),
            MenuModel(R.drawable.ic_set_profil, "Edit Profil"),
            MenuModel(R.drawable.ic_set_schedule, "Jadwalkan"),
            MenuModel(R.drawable.ic_set_url, "URL Media Sosial"),
            MenuModel(R.drawable.ic_activity, "Aktifitas"),
        )

        val rv = view.findViewById<RecyclerView>(R.id.rvMenu)
        rv.layoutManager = GridLayoutManager(requireContext(), 4)
        rv.addItemDecoration(GridSpacingItemDecoration(4, 5, true))
        rv.adapter = AllMenuAdapter(listMenu) { menu ->
            when(menu.name) {
                "Edit Profil" -> {
                    val intent = Intent(requireContext(), SettingActivity::class.java)
                    intent.putExtra("open_page", "edit_profile")
                    startActivity(intent)
                    dialog.dismiss()
                }

                "Setting General" -> {
                    val intent = Intent(requireContext(), SettingActivity::class.java)
                    intent.putExtra("open_page", "general")
                    startActivity(intent)
                    dialog.dismiss()
                }

                "URL Media Sosial" -> {
                    val intent = Intent(requireContext(), SettingActivity::class.java)
                    intent.putExtra("open_page", "contact")
                    startActivity(intent)
                    dialog.dismiss()
                }

                "Profil" -> {
                    startActivity(Intent(requireContext(), SettingActivity::class.java))
                    requireActivity().overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    dialog.dismiss()
                }

                "Aktifitas" -> {
                    startActivity(Intent(requireContext(), RecentAllActivity::class.java))
                    requireActivity().overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    dialog.dismiss()
                }

                "Tambah Mobil" -> {
                    val intent = Intent(requireContext(), AddCarStep1Activity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    dialog.dismiss()
                }

                "Tambah Transaksi" -> {
                    val intent = Intent(requireContext(), AddTrnActivity1::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    dialog.dismiss()
                }

                "Download Laporan" -> {
                    exportAllReportsToPdf()
                    requireActivity().overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    dialog.dismiss()
                }

                "Janji Temu" -> {
                    Log.d("HomeFragment", "Membuka AppointmentActivity")
                    try {
                        startActivity(Intent(requireContext(), AppointmentActivity::class.java))
                        requireActivity().overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                        dialog.dismiss()
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Error membuka appointment: ${e.message}", e)
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val parent = view.parent as View
        parent.setBackgroundResource(android.R.color.transparent)
    }

    private fun loadDashboardStats() {
        showLoading(true)
        showLoadingState(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getDashboardStats()
                if (response.isSuccessful) {
                    val result: DashboardResponse? = response.body()
                    result?.data?.let { d ->
                        binding.lTotalCar.text = d.total_mobil_available.toString()
                        binding.lTotalPenjualan.text = d.total_transaksi_bulan_ini.toString()
                        binding.lIncome.text = formatRupiah(d.total_pendapatan_bulan_ini)
                        binding.lReserved.text = d.total_mobil_reserved.toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                showLoading(false)
                showLoadingState(false)
            }
        }
    }

    private fun formatRupiah(value: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(value).replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(show: Boolean) {
        if (show) loadingCount++ else loadingCount--

        if (loadingCount > 0) {
            binding.recyclerRecent.visibility = View.GONE
            binding.btnShowMore.visibility = View.GONE

            binding.loadingProgress.visibility = View.VISIBLE
            binding.loadingProgress.playAnimation()
        } else {
            binding.loadingProgress.pauseAnimation()
            binding.loadingProgress.visibility = View.GONE

            binding.recyclerRecent.visibility = View.VISIBLE
        }
    }

    private fun showLoadingState(show: Boolean) {
        val visibility = if (show) View.GONE else View.VISIBLE
        binding.lTotalCar.visibility = visibility
        binding.lTotalPenjualan.visibility = visibility
        binding.lIncome.visibility = visibility
        binding.lReserved.visibility = visibility
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

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(msg: String = "Memuat...") {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun hideLoading() {}

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
}