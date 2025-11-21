package com.maverick.kmjshowroom.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ActivityItem
import com.maverick.kmjshowroom.Model.DashboardResponse
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
            }
        }
    }

    // Load Dashboard Stats
    private fun loadDashboardStats() {
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
}
