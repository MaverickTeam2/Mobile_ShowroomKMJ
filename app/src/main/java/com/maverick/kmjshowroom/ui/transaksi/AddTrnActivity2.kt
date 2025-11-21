package com.maverick.kmjshowroom.ui.transaksi

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.maverick.kmjshowroom.R

class AddTrnActivity2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_trn_2, container, false)

        val closeButton = view.findViewById<ImageView>(R.id.icon_close)
        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val txtNama = view.findViewById<TextInputEditText>(R.id.txtusername)
        val txtTlp = view.findViewById<TextInputEditText>(R.id.txttlp)
        val cbKTP = view.findViewById<CheckBox>(R.id.cbKTP)
        val cbKK = view.findViewById<CheckBox>(R.id.cbKK)
        val cbRekening = view.findViewById<CheckBox>(R.id.cbRekening)
        val btnNext = view.findViewById<View>(R.id.btn_next)

        val selectedCar = arguments?.getString("selectedCar")
        Toast.makeText(requireContext(), "Mobil dipilih: $selectedCar", Toast.LENGTH_SHORT).show()

        // Tombol NEXT
        btnNext.setOnClickListener {
            val nama = txtNama.text.toString().trim()
            val telp = txtTlp.text.toString().trim()

            if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(telp)) {
                Toast.makeText(requireContext(), "Nama dan Nomor Telepon wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!telp.matches(Regex("^[0-9]+$"))) {
                Toast.makeText(requireContext(), "Nomor Telepon hanya boleh angka!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dokumenDipilih = listOf(cbKTP.isChecked, cbKK.isChecked, cbRekening.isChecked).any { it }
            if (!dokumenDipilih) {
                Toast.makeText(requireContext(), "Minimal pilih 1 dokumen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putString("selectedCar", selectedCar)
                putString("namaPembeli", nama)
                putString("noTelp", telp)
            }

            findNavController().navigate(R.id.action_addTrnActivity2fragment_to_addTrnActivity3fragment, bundle)
        }


        return view
    }
}
