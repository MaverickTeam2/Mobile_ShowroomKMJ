package com.maverick.kmjshowroom.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.databinding.ActivityRecentDetailBinding
import kotlinx.coroutines.launch


class RecentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.headerInclude.textHeader.text = "Detail Activity"

        val id = intent.getIntExtra("id", 0)
        if (id != 0) {
            loadDetail(id)
        }
    }

    private fun loadDetail(id: Int) {
        lifecycleScope.launch {
            val res = ApiClient.apiService.getActivityDetail(id)

            if (res.isSuccessful && res.body()?.code == 200) {
                val d = res.body()!!.data

                binding.tvType.text = d.activity_type
                binding.tvDesc.text = d.description
                binding.tvUser.text = d.full_name ?: "-"
                binding.tvDate.text = d.created_at
            }
        }
    }
}