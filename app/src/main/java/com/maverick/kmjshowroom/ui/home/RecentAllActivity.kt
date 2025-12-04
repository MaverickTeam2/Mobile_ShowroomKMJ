package com.maverick.kmjshowroom.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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
    private var currentFilter = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecentAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.headerInclude.textHeader.text = "Recent Activity"

        // Get filter from intent if available
        currentFilter = intent.getStringExtra("filter") ?: "all"

        setupFilterSpinner()
        setupRecyclerView()
        setupScrollListener()
        setupSwipeRefresh()

        loadData(isRefresh = true)
    }

    private fun setupFilterSpinner() {
        // Set initial selection based on currentFilter
        val initialPosition = when(currentFilter) {
            "all" -> 0
            "mobil" -> 1
            "transaksi" -> 2
            else -> 0
        }
        binding.spinnerFilter.setSelection(initialPosition)

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newFilter = when(position) {
                    0 -> "all"
                    1 -> "mobil"
                    2 -> "transaksi"
                    else -> "all"
                }

                // Only reload if filter actually changed
                if (newFilter != currentFilter) {
                    currentFilter = newFilter
                    currentLimit = 10
                    loadData(isRefresh = true)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = RecentActivityAdapter(list) { item ->
            val i = Intent(this, RecentDetailActivity::class.java)
            i.putExtra("id", item.id)
            startActivity(i)
        }

        binding.recyclerAll.layoutManager = LinearLayoutManager(this)
        binding.recyclerAll.adapter = adapter
    }

    private fun setupScrollListener() {
        binding.recyclerAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading && list.isNotEmpty()) {
                    // Check if there might be more data
                    if (list.size == lastLoadedCount) {
                        currentLimit += 10
                        loadData(isRefresh = false)
                    }
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            currentLimit = 10
            loadData(isRefresh = true)
        }
    }

    private fun loadData(isRefresh: Boolean) {
        lifecycleScope.launch {
            try {
                isLoading = true

                // Only show swipe refresh indicator, not the empty state
                if (isRefresh) {
                    binding.swipeRefresh.isRefreshing = true
                }

                val res = ApiClient.apiService.getRecentActivity(currentLimit, currentFilter)

                if (res.isSuccessful && res.body()?.code == 200) {
                    val data = res.body()!!.data

                    // If loading more data and no new items, stop
                    if (!isRefresh && data.size == lastLoadedCount) {
                        isLoading = false
                        return@launch
                    }

                    lastLoadedCount = data.size

                    if (isRefresh) {
                        list.clear()
                    }

                    list.clear()
                    list.addAll(data)
                    adapter.notifyDataSetChanged()

                    // Show/hide empty state
                    updateEmptyState()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateEmptyState()
            } finally {
                isLoading = false
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun updateEmptyState() {
        if (list.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.recyclerAll.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.recyclerAll.visibility = View.VISIBLE
        }
    }
}