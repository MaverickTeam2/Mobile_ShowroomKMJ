package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.Model.CarData
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentCarBinding
import androidx.fragment.app.activityViewModels
import com.maverick.kmjshowroom.ui.SharedCarViewModel



//class CarFragment : Fragment() {
//
//    private var _binding: FragmentCarBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var carAdapter: CarAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentCarBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val cars = listOf(
//            CarData(
//                "Bugatti Tourbillon Widebody", "2023", "Black",
//                R.drawable.bugatti, "Available", "20.000 km", "Bensin",
//                "Rp. 30.000.000 x 20", "Rp. 20.000.000"
//            ),
//            CarData(
//                "Lamborghini Aventador", "2022", "Yellow",
//                R.drawable.lamborghini, "Available", "15.000 km", "Bensin",
//                "Rp. 25.000.000 x 18", "Rp. 18.000.000"
//            ),
//            CarData(
//                "Ferrari F8 Tributo", "2021", "Red",
//                R.drawable.ferrari, "Sold Out", "30.000 km", "Bensin",
//                "Rp. 22.000.000 x 24", "Rp. 15.000.000"
//            )
//        )
//
//        carAdapter = CarAdapter(cars)
//
//        binding.carRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = carAdapter
//        }
//
//        binding.btnTambah.setOnClickListener {
//            val intent = Intent(requireContext(), AddCarStep1Activity::class.java)
//            startActivity(intent)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
class CarFragment : Fragment() {

    private var _binding: FragmentCarBinding? = null
    private val binding get() = _binding!!

    private val sharedCarViewModel: SharedCarViewModel by activityViewModels()

    private lateinit var carAdapter: CarAdapter

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

        // Buat data di CarFragment
        val cars = listOf(
            CarData(
                "Bugatti Tourbillon Widebody", "2023", "Black",
                R.drawable.bugatti, "Available", "20.000 km", "Bensin",
                "Rp. 30.000.000 x 20", "Rp. 20.000.000"
            ),
            CarData(
                "Lamborghini Aventador", "2022", "Yellow",
                R.drawable.lamborghini, "Available", "15.000 km", "Bensin",
                "Rp. 25.000.000 x 18", "Rp. 18.000.000"
            ),
            CarData(
                "Ferrari F8 Tributo", "2021", "Red",
                R.drawable.ferrari, "Sold Out", "30.000 km", "Bensin",
                "Rp. 22.000.000 x 24", "Rp. 15.000.000"
            )
        )

        // ✅ Kirim data ke ViewModel
        sharedCarViewModel.carList.value = cars

        carAdapter = CarAdapter(cars)

        binding.carRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carAdapter
        }

        binding.btnTambah.setOnClickListener {
            val intent = Intent(requireContext(), AddCarStep1Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
