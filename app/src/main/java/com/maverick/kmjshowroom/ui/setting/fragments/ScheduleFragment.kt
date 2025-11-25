package com.maverick.kmjshowroom.ui.setting.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.CreateScheduleRequest
import com.maverick.kmjshowroom.Model.ScheduleItem
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.setting.adapter.DayAdapter
import com.maverick.kmjshowroom.ui.setting.model.DaySchedule
import com.maverick.kmjshowroom.utils.HeaderHelper
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {

    private lateinit var rvScheduleDays: RecyclerView
    private lateinit var dayAdapter: DayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting_schedule, container, false)
        val headerView = view.findViewById<View>(R.id.header_include)
        val tvHeader: TextView = headerView.findViewById(R.id.text_header)
        tvHeader.text = "Jadwal Operasional Showroom"
        rvScheduleDays = view.findViewById(R.id.rvScheduleDays)
        rvScheduleDays.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HeaderHelper.setupBackButton(requireActivity(), view.findViewById(R.id.header_include))
        lifecycleScope.launch { loadSchedule() }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    private suspend fun loadSchedule() {
        try {
            val response = ApiClient.apiService.getSchedule()
            if (response.code == 200) {
                val savedData = response.data
                val days = listOf("Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu")

                val schedules = days.map { day ->
                    val filtered = savedData.filter { it.hari == day }
                    DaySchedule(
                        dayName = day,
                        available = filtered.any { it.is_active == 1 },
                        slots = filtered.toMutableList()
                    )
                }

                dayAdapter = DayAdapter(requireContext(), schedules, ::toggleDay, ::refreshSchedule)
                rvScheduleDays.adapter = dayAdapter
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal memuat jadwal", Toast.LENGTH_SHORT).show()
            Log.e("ScheduleFragment", e.message ?: "Unknown error")
        }
    }

    private fun toggleDay(day: String, isActive: Boolean, callback: (Boolean) -> Unit) {
        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "hari" to day,
                    "is_active" to if (isActive) 1 else 0
                )

                val response = ApiClient.apiService.toggleDaySchedule(body as Map<String, Int>)

                if (response.code == 200) {

                    if (isActive) {
                        addDefaultSlotIfEmpty(day)
                    }

                    callback(true)
                    refreshSchedule()

                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    callback(false)
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal mengupdate status", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }
    }

    private suspend fun addDefaultSlotIfEmpty(day: String) {
        val response = ApiClient.apiService.getSchedule()
        val filtered = response.data.filter { it.hari == day }

        if (filtered.isEmpty()) {
            val req = CreateScheduleRequest(
                hari = day,
                slot_index = 1,
                jam_buka = "09:00",
                jam_tutup = "10:00",
                is_active = 1
            )
            ApiClient.apiService.createSchedule(req)
        }
    }

    private fun refreshSchedule() {
        lifecycleScope.launch { loadSchedule() }
    }
}
