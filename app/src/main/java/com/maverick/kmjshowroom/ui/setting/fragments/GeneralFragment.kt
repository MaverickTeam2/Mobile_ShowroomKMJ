package com.maverick.kmjshowroom.ui.setting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.utils.HeaderHelper
import kotlinx.coroutines.launch

class GeneralFragment : Fragment() {

    private lateinit var switchShowroom: Switch
    private lateinit var switchSellCar: Switch
    private lateinit var switchSchedule: Switch

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        HeaderHelper.setupBackButton(requireActivity(), view.findViewById(R.id.header_include))

        switchShowroom = view.findViewById(R.id.switchShowroom)
        switchSellCar = view.findViewById(R.id.switchSellCar)
        switchSchedule = view.findViewById(R.id.switchSchedule)

        loadGeneralSettings()

        handleSwitchListener()
    }

    private fun loadGeneralSettings() {
        lifecycleScope.launch {
            try {
                isLoading = true
                val response = ApiClient.apiService.getGeneral()
                val result = response.body()

                if (response.isSuccessful && result?.code == 200 && result.data != null) {
                    val data = result.data
                    switchShowroom.isChecked = data.showroom_status == 1
                    switchSellCar.isChecked = data.jual_mobil == 1
                    switchSchedule.isChecked = data.schedule_pelanggan == 1
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    private fun handleSwitchListener() {

        switchShowroom.setOnCheckedChangeListener { _, isChecked ->
            if (!isLoading) updateSetting("showroom_status", if (isChecked) 1 else 0)
        }

        switchSellCar.setOnCheckedChangeListener { _, isChecked ->
            if (!isLoading) updateSetting("jual_mobil", if (isChecked) 1 else 0)
        }

        switchSchedule.setOnCheckedChangeListener { _, isChecked ->
            if (!isLoading) updateSetting("schedule_pelanggan", if (isChecked) 1 else 0)
        }
    }

    private fun updateSetting(field: String, value: Int) {
        lifecycleScope.launch {
            try {
                val body = mapOf(field to value)

                val response = ApiClient.apiService.updateGeneral(body)

                if (response.isSuccessful && response.body()?.code == 200) {
                    Toast.makeText(requireContext(), "Berhasil diperbarui", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Update gagal", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }
}
