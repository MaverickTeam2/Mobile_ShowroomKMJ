package com.maverick.kmjshowroom.ui.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.Model.Transaction
import com.maverick.kmjshowroom.databinding.FragmentTransaksiBinding

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transaksiViewModel =
            ViewModelProvider(this).get(TransaksiViewModel::class.java)


        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.headerInclude.textHeader.text = "TRANSAKSI"

        val dummyData = listOf(
            Transaction(
                id = "TXN-001",
                date = "15-01-2025",
                customerName = "Mailkel Smith",
                phoneNumber = "+62 812-3456-7890",
                car = "2024 BMW X5",
                description = "Deal Payment",
                price = "Rp. 50.000.000",
                dealPrice = "Rp. 48.000.000",
                status = "Completed"
            ),
            Transaction(
                id = "TXN-002",
                date = "14-01-2025",
                customerName = "Mailkel Smooth",
                phoneNumber = "+62 812-3456-7890",
                car = "2024 BMW X5",
                description = "Deal Payment",
                price = "Rp. 50.000.000",
                dealPrice = "Rp. 48.000.000",
                status = "Pending"
            ),
            Transaction(
                id = "TXN-003",
                date = "13-01-2025",
                customerName = "John Canceled",
                phoneNumber = "+62 812-0000-0000",
                car = "2024 BMW X5",
                description = "Deal Payment",
                price = "Rp. 50.000.000",
                dealPrice = "Rp. 48.000.000",
                status = "Canceled"
            )
        )
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TransactionAdapter(requireContext(), dummyData)
        }
        binding.transaksiContainer.addView(recyclerView)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}