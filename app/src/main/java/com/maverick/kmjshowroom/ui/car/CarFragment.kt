package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.Model.MobilItem
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentCarBinding
import com.maverick.kmjshowroom.ui.setting.SettingActivity

class CarFragment : Fragment() {

    private var _binding: FragmentCarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by viewModels()
    private lateinit var adapter: CarAdapter

    private var loadingCount = 0

    // List untuk menyimpan data asli dan hasil filter
    private var originalList = listOf<MobilItem>()
    private var filteredList = listOf<MobilItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Setup adapter SEKALI dengan stable IDs
        adapter = CarAdapter(
            emptyList(),
            onItemClick = { mobil -> openDetailMobil(mobil) }
        )

        setupRecyclerView()
        setupSearchBar()
        setupButtonListener()
        observeViewModel()

        binding.headerInclude.iconProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        // ✅ Load data hanya jika belum pernah dimuat
        if (viewModel.mobilListLiveData.value == null) {
            Log.d("CarFragment", "=== FIRST LOAD ===")
            viewModel.loadMobilList()
        } else {
            Log.d("CarFragment", "=== DATA ALREADY LOADED (${viewModel.mobilListLiveData.value?.size} items) ===")
        }
    }

    override fun onResume() {
        super.onResume()
        // ✅ REFRESH data setiap kali fragment visible lagi
        Log.d("CarFragment", "=== onResume: REFRESHING DATA ===")
        viewModel.loadMobilList()
    }

    private fun setupRecyclerView() {
        binding.carRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CarFragment.adapter

            // ✅ Optimisasi RecyclerView
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0 // Matikan animasi untuk avoid flicker
        }
    }

    /**
     * Setup search bar dengan TextWatcher
     */
    private fun setupSearchBar() {
        val searchBar = binding.headerInclude.searchBar

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMobilList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Filter list mobil berdasarkan keyword
     */
    private fun filterMobilList(keyword: String) {
        if (keyword.isEmpty()) {
            // Jika search kosong, tampilkan semua data
            filteredList = originalList
        } else {
            // Filter berdasarkan nama mobil, tahun, warna, atau tipe bahan bakar
            filteredList = originalList.filter { mobil ->
                mobil.nama_mobil.contains(keyword, ignoreCase = true) ||
                        mobil.tahun_mobil.toString().contains(keyword) ||
                        mobil.warna_exterior.contains(keyword, ignoreCase = true) ||
                        mobil.tipe_bahan_bakar.contains(keyword, ignoreCase = true) ||
                        mobil.status.contains(keyword, ignoreCase = true)
            }
        }

        // Update adapter dengan hasil filter
        adapter.updateData(filteredList)

        Log.d("CarFragment", "Search: '$keyword' - Found ${filteredList.size} results")
    }

    private fun setupButtonListener() {
        binding.btnTambah.setOnClickListener {
            val intent = Intent(requireContext(), AddCarStep1Activity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        // ✅ PENTING: Remove observer lama dulu (kalau ada)
        viewModel.mobilListLiveData.removeObservers(viewLifecycleOwner)
        viewModel.loadingLiveData.removeObservers(viewLifecycleOwner)
        viewModel.errorLiveData.removeObservers(viewLifecycleOwner)

        // ✅ Observe data mobil
        viewModel.mobilListLiveData.observe(viewLifecycleOwner) { list ->
            Log.d("CarFragment", "=== OBSERVER TRIGGERED: ${list.size} items ===")

            // Simpan data asli
            originalList = list

            // Cek apakah ada search keyword aktif
            val currentKeyword = binding.headerInclude.searchBar.text.toString()
            if (currentKeyword.isEmpty()) {
                filteredList = originalList
            } else {
                // Re-apply filter jika ada keyword aktif
                filterMobilList(currentKeyword)
            }

            adapter.updateData(filteredList)
        }

        // ✅ Observe loading state dengan Lottie Animation
        viewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // ✅ Observe error
        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openDetailMobil(mobil: MobilItem) {
        val intent = Intent(requireContext(), DetailMobilActivity::class.java)
        intent.putExtra("kode_mobil", mobil.kode_mobil)
        startActivity(intent)
    }

    /**
     * Fungsi untuk menampilkan/menyembunyikan loading animation
     * Menggunakan loadingCount untuk handle multiple loading states
     */
    private fun showLoading(show: Boolean) {
        if (show) loadingCount++ else loadingCount--

        if (loadingCount > 0) {
            // Sembunyikan konten
            binding.carRecyclerView.visibility = View.GONE
            binding.btnTambah.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            // Tampilkan Lottie loading
            binding.loadingProgress.visibility = View.VISIBLE
            binding.loadingProgress.playAnimation()
        } else {
            // Sembunyikan Lottie loading
            binding.loadingProgress.pauseAnimation()
            binding.loadingProgress.visibility = View.GONE

            // Tampilkan konten
            binding.carRecyclerView.visibility = View.VISIBLE
            binding.btnTambah.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}