package com.maverick.kmjshowroom.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.maverick.kmjshowroom.AddCarStep1Fragment
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentCarBinding
import com.maverick.kmjshowroom.Model.CarData

class CarFragment : Fragment() {

    private var _binding: FragmentCarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //data dummy
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

        //loop membuat card setiap mobil
        for (car in cars) {
            val cardView = layoutInflater.inflate(R.layout.card_mobil, binding.carContainer, false)

            // Bind data ke card
            val imgCar = cardView.findViewById<ImageView>(R.id.imgCar)
            val txtTitle = cardView.findViewById<TextView>(R.id.txtTitle)
            val txtYear = cardView.findViewById<TextView>(R.id.txtYear)
            val txtWarna = cardView.findViewById<TextView>(R.id.txtWarnaValue)
            val txtStatus = cardView.findViewById<TextView>(R.id.txtStatus)
            val txtJarak = cardView.findViewById<TextView>(R.id.txtJaraktempuhValue)
            val txtBahan = cardView.findViewById<TextView>(R.id.txtBahanabakarValue)
            val txtAngsuran = cardView.findViewById<TextView>(R.id.txtAngsuran)
            val txtDp = cardView.findViewById<TextView>(R.id.txtDp)
            val btnEdit = cardView.findViewById<ImageButton>(R.id.btnEdit)
            val btnDelete = cardView.findViewById<ImageButton>(R.id.btnDelete)

            imgCar.setImageResource(car.imageRes)
            txtTitle.text = car.title
            txtYear.text = car.year
            txtWarna.text = car.warna
            txtStatus.text = car.status
            txtJarak.text = car.jarakTempuh
            txtBahan.text = car.bahanBakar
            txtAngsuran.text = car.angsuran
            txtDp.text = car.dp

            btnEdit.setOnClickListener {
                Toast.makeText(requireContext(), "Edit ${car.title}", Toast.LENGTH_SHORT).show()
            }
            btnDelete.setOnClickListener {
                Toast.makeText(requireContext(), "Hapus ${car.title}", Toast.LENGTH_SHORT).show()
            }

            binding.carContainer.addView(cardView)
        }

        binding.btnTambah.setOnClickListener {
            findNavController().navigate(R.id.action_carFragment_to_addCarStep1Fragment)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
