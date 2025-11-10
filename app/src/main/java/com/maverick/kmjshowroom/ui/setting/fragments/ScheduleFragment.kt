package com.maverick.kmjshowroom.ui.setting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.setting.adapter.DayAdapter
import com.maverick.kmjshowroom.ui.setting.model.DaySchedule
import com.maverick.kmjshowroom.utils.HeaderHelper

class ScheduleFragment : Fragment() {

    private lateinit var rvScheduleDays: RecyclerView
    private lateinit var dayAdapter: DayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting_schedule, container, false)
        rvScheduleDays = view.findViewById(R.id.rvScheduleDays)

        val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        val schedules = days.map { DaySchedule(it, true, mutableListOf("09:00-17:00")) }

        dayAdapter = DayAdapter(requireContext(), schedules)
        rvScheduleDays.layoutManager = LinearLayoutManager(requireContext())
        rvScheduleDays.adapter = dayAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        HeaderHelper.setupBackButton(requireActivity(), view.findViewById(R.id.header_include))
    }

    override fun onResume() {
        super.onResume()

        // Sembunyikan status bar dan navigation bar
        requireActivity().window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }


}
