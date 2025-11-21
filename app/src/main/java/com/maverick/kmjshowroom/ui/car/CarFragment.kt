package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.os.Bundle
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

    private lateinit var binding: FragmentCarBinding
    private val viewModel: CarViewModel by viewModels()
    private lateinit var adapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = CarAdapter(
            emptyList(),
            onItemClick = { mobil -> openDetailMobil(mobil) }
        )

        setupRecyclerView()
        setupButtonListener()
        observeViewModel()

        viewModel.loadMobilList()
    }

    private fun setupRecyclerView() {
        binding.carRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.carRecyclerView.adapter = adapter
    }

    private fun setupButtonListener() {
        binding.btnTambah.setOnClickListener {
            val intent = Intent(requireContext(), AddCarStep1Activity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.mobilListLiveData.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        viewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔥 Klik card langsung buka DetailMobilActivity
    private fun openDetailMobil(mobil: MobilItem) {
        val intent = Intent(requireContext(), DetailMobilActivity::class.java)
        intent.putExtra("kode_mobil", mobil.kode_mobil)
        startActivity(intent)
    }
}
