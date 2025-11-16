package com.maverick.kmjshowroom.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
        adapter = CarAdapter(emptyList()) { mobil ->
            // Klik card → buka detail activity
            // Intent ke detail
        }

        binding.carRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.carRecyclerView.adapter = adapter

        observeViewModel()

        viewModel.loadMobilList() // Panggil API
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
                // bisa pakai Toast atau Snackbar
            }
        }
    }
}
