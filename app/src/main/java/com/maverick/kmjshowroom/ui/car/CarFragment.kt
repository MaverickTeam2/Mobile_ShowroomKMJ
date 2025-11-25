package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.Model.MobilItem
import com.maverick.kmjshowroom.databinding.FragmentCarBinding

class CarFragment : Fragment() {

    private var _binding: FragmentCarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by viewModels()
    private lateinit var adapter: CarAdapter

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
        setupButtonListener()
        observeViewModel()

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
            adapter.updateData(list)
        }

        // ✅ Observe loading state
        viewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}