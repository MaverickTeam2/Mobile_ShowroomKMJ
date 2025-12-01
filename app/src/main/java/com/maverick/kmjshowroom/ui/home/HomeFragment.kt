package com.maverick.kmjshowroom.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.Model.MenuModel
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ActivityItem
import com.maverick.kmjshowroom.Model.DashboardResponse
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentHomeBinding
import com.maverick.kmjshowroom.ui.setting.SettingActivity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecentActivityAdapter
    private val recentList = mutableListOf<ActivityItem>()
    private var loadingCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.headerInclude.textHeader.text = "HOME"

        binding.headerInclude.iconProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
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

        initRecycler()
        loadRecentActivity()
        loadDashboardStats()

        return root
    }

    // Setup RecyclerView
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
                val res = ApiClient.apiService.getRecentActivity(1000)
                if (res.isSuccessful && res.body()?.code == 200) {

                    val all = res.body()!!.data
                    recentList.clear()
                    recentList.addAll(all.take(5))
                    adapter.notifyDataSetChanged()

                    binding.btnShowMore.visibility =
                        if (all.size > 5) View.VISIBLE else View.GONE

                    binding.btnShowMore.setOnClickListener {
                        startActivity(
                            Intent(requireContext(), RecentAllActivity::class.java)
                        )
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
                "Profil" -> {
                    startActivity(Intent(requireContext(), SettingActivity::class.java))
                    requireActivity().overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
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

    // Format rupiah
    private fun formatRupiah(value: Int): String {
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

}
