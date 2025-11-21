package com.maverick.kmjshowroom.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ActivityItem
import com.maverick.kmjshowroom.databinding.ActivityRecentAllBinding
import kotlinx.coroutines.launch

class RecentAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecentAllBinding
    private lateinit var adapter: RecentActivityAdapter
    private val list = mutableListOf<ActivityItem>()
    private var isLoading = false
    private var currentLimit = 10
    private var lastLoadedCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecentAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.headerInclude.textHeader.text = "Recent Activity"

        adapter = RecentActivityAdapter(list) { item ->
            val i = Intent(this, RecentDetailActivity::class.java)
            i.putExtra("id", item.id)
            startActivity(i)
        }

        binding.recyclerAll.layoutManager = LinearLayoutManager(this)
        binding.recyclerAll.adapter = adapter

        loadData(isRefresh = true)

        binding.recyclerAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading) {
                    currentLimit += 10
                    loadData(isRefresh = false)
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            currentLimit = 10
            loadData(isRefresh = true)
        }
    }

    private fun loadData(isRefresh: Boolean) {
        lifecycleScope.launch {
            try {
                isLoading = true
                binding.swipeRefresh.isRefreshing = true

                val res = ApiClient.apiService.getRecentActivity(currentLimit)

                if (res.isSuccessful && res.body()?.code == 200) {
                    val data = res.body()!!.data

                    // Jika tidak ada data baru → hentikan loading + stop refresh
                    if (data.size == lastLoadedCount) {
                        isLoading = false
                        binding.swipeRefresh.isRefreshing = false
                        return@launch
                    }

                    lastLoadedCount = data.size

                    if (isRefresh) {
                        list.clear()
                    }

                    list.clear()
                    list.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }


}
