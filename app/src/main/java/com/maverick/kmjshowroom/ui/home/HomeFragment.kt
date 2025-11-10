package com.maverick.kmjshowroom.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maverick.kmjshowroom.databinding.FragmentHomeBinding
import com.maverick.kmjshowroom.R
import android.content.Intent
import com.maverick.kmjshowroom.ui.setting.SettingActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    data class RecentActivityItem(val title: String, val description: String, val time: String)

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
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        val dummyData = listOf(
            RecentActivityItem("Mobil Ditambahkan", "2024 BMW X5 ditambahkan ke inventory", "2 jam lalu"),
            RecentActivityItem("Mobil Terjual", "2020 Avanza berhasil dijual", "Kemarin"),
            RecentActivityItem("Laporan Dibuat", "Laporan bulanan telah di-generate", "3 hari lalu"),
            RecentActivityItem("Unit Service", "Mitsubishi Xpander masuk service", "5 hari lalu"),
            RecentActivityItem("Mobil Ditambahkan", "2023 Honda HR-V masuk ke sistem", "1 minggu lalu")
        )

        populateRecentActivity(dummyData)

        return root
    }

    private fun populateRecentActivity(items: List<RecentActivityItem>) {
        val container = binding.root.findViewById<LinearLayout>(R.id.recent_container)
        container.removeAllViews()

        if (items.isEmpty()) {
            container.visibility = View.GONE
            return
        }

        container.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(requireContext())

        for (item in items.take(10)) {
            val view = inflater.inflate(R.layout.item_recent_activity, container, false)

            view.findViewById<TextView>(R.id.tv_title).text = item.title
            view.findViewById<TextView>(R.id.tv_description).text = item.description
            view.findViewById<TextView>(R.id.tv_time).text = item.time

            container.addView(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
