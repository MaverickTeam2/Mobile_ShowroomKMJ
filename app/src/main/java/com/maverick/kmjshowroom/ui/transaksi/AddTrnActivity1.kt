package com.maverick.kmjshowroom.ui.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.SharedCarViewModel

class AddTrnActivity1 : Fragment() {

    private val sharedCarViewModel: SharedCarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_trn_1, container, false)

        val closeButton = view.findViewById<ImageView>(R.id.icon_close)
        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val dropdown = view.findViewById<AutoCompleteTextView>(R.id.dropdown_text)
        val txtTipe = view.findViewById<TextInputEditText>(R.id.txttipe)
        val imgCar = view.findViewById<ImageView>(R.id.imgCar)
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtPrice = view.findViewById<TextView>(R.id.txtPrice)
        val txtDp = view.findViewById<TextView>(R.id.txtDp)
        val txtKm = view.findViewById<TextView>(R.id.txtKm)
        val txtYear = view.findViewById<TextView>(R.id.txtYear)

        sharedCarViewModel.carList.observe(viewLifecycleOwner) { cars ->
            if (cars.isEmpty()) return@observe

            val namaMobilList = cars.map { it.title }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, namaMobilList)
            dropdown.setAdapter(adapter)

            dropdown.setOnItemClickListener { _, _, position, _ ->
                val selectedCar = cars[position]
                sharedCarViewModel.selectedCar.value = selectedCar

                sharedCarViewModel.updateRandomTipe()
                txtTipe.setText(sharedCarViewModel.selectedTipe.value)
                imgCar.setImageResource(selectedCar.imageRes)
                txtTitle.text = selectedCar.title
                txtPrice.text = selectedCar.angsuran
                txtDp.text = selectedCar.dp
                txtKm.text = selectedCar.jarakTempuh
                txtYear.text = selectedCar.year
            }
        }

        val btnNext = view.findViewById<View>(R.id.btn_next)
        btnNext?.setOnClickListener {
            val selected = sharedCarViewModel.selectedCar.value
            if (selected != null) {
                val bundle = Bundle().apply {
                    putString("selectedCar", selected.title)
                }
                findNavController().navigate(
                    R.id.action_addTrnActivity1fragment_to_addTrnActivity2fragment,
                    bundle
                )
            } else {
                Toast.makeText(requireContext(), "Pilih mobil terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }



        return view
    }
}
