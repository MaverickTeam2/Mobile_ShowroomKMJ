//package com.maverick.kmjshowroom.ui.transaksi
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.navigation.fragment.findNavController
//import com.maverick.kmjshowroom.Model.Transaction
//import com.maverick.kmjshowroom.R
//import com.maverick.kmjshowroom.databinding.FragmentTransaksiBinding
//import com.maverick.kmjshowroom.ui.SharedCarViewModel
//
//class TransaksiFragment : Fragment() {
//
//    private var _binding: FragmentTransaksiBinding? = null
//    private val binding get() = _binding!!
//
//    private val sharedCarViewModel: SharedCarViewModel by activityViewModels()
//
//    private lateinit var transaksiAdapter: TransactionAdapter
//    private val allTransactions = mutableListOf<Transaction>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val transaksiViewModel = ViewModelProvider(this).get(TransaksiViewModel::class.java)
//
//        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        binding.headerInclude.textHeader.text = "TRANSAKSI"
//
//        binding.btnTambah.setOnClickListener {
//            findNavController().navigate(R.id.action_transaksiFragment_to_addTrnActivity1fragment)
//        }
//
//        val dummyData = listOf(
//            Transaction(
//                id = "TXN-001",
//                date = "15-01-2025",
//                customerName = "Mailkel Smith",
//                phoneNumber = "+62 812-3456-7890",
//                car = "2024 BMW X5",
//                description = "Deal Payment",
//                price = "Rp. 50.000.000",
//                dealPrice = "Rp. 48.000.000",
//                status = "Completed"
//            ),
//            Transaction(
//                id = "TXN-002",
//                date = "14-01-2025",
//                customerName = "Mailkel Smooth",
//                phoneNumber = "+62 812-3456-7890",
//                car = "2024 BMW X5",
//                description = "Deal Payment",
//                price = "Rp. 50.000.000",
//                dealPrice = "Rp. 48.000.000",
//                status = "Pending"
//            ),
//            Transaction(
//                id = "TXN-003",
//                date = "13-01-2025",
//                customerName = "John Canceled",
//                phoneNumber = "+62 812-0000-0000",
//                car = "2024 BMW X5",
//                description = "Deal Payment",
//                price = "Rp. 50.000.000",
//                dealPrice = "Rp. 48.000.000",
//                status = "Canceled"
//            )
//        )
//
//        allTransactions.addAll(dummyData)
//
//        transaksiAdapter = TransactionAdapter(requireContext(), allTransactions)
//        val recyclerView = RecyclerView(requireContext()).apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = transaksiAdapter
//        }
//        binding.transaksiContainer.addView(recyclerView)
//
//        sharedCarViewModel.transaksiList.observe(viewLifecycleOwner) { transaksiBaru ->
//            allTransactions.clear()
//            allTransactions.addAll(dummyData + transaksiBaru)
//            transaksiAdapter.notifyDataSetChanged()
//        }
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//
//
package com.maverick.kmjshowroom.ui.transaksi

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.maverick.kmjshowroom.Model.Transaction
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentTransaksiBinding
import com.maverick.kmjshowroom.ui.SharedCarViewModel

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!

    private val sharedCarViewModel: SharedCarViewModel by activityViewModels()

    private lateinit var transaksiAdapter: TransactionAdapter
    private val allTransactions = mutableListOf<Transaction>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transaksiViewModel = ViewModelProvider(this).get(TransaksiViewModel::class.java)

        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.headerInclude.textHeader.text = "TRANSAKSI"

        binding.btnTambah.setOnClickListener {
            findNavController().navigate(R.id.action_transaksiFragment_to_addTrnActivity1fragment)
        }

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

        allTransactions.addAll(dummyData)

        transaksiAdapter = TransactionAdapter(requireContext(), allTransactions).apply {
            onDetailClick = { transaction ->
                showTransactionDetailDialog(transaction)
            }
        }

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transaksiAdapter
        }
        binding.transaksiContainer.addView(recyclerView)

        sharedCarViewModel.transaksiList.observe(viewLifecycleOwner) { transaksiBaru ->
            allTransactions.clear()
            allTransactions.addAll(dummyData + transaksiBaru)
            transaksiAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showTransactionDetailDialog(transaction: Transaction) {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_transaction_detail)
            window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

            // Isi data
            findViewById<TextView>(R.id.tvId).text = transaction.id
            findViewById<TextView>(R.id.tvDate).text = transaction.date
            findViewById<TextView>(R.id.tvCustomer).text = transaction.customerName
            findViewById<TextView>(R.id.tvPhone).text = transaction.phoneNumber
            findViewById<TextView>(R.id.tvCar).text = transaction.car
            findViewById<TextView>(R.id.tvDesc).text = transaction.description
            findViewById<TextView>(R.id.tvPrice).text = transaction.price
            findViewById<TextView>(R.id.tvDeal).text = transaction.dealPrice
            findViewById<TextView>(R.id.tvStatus).text = transaction.status

            val statusView = findViewById<TextView>(R.id.tvStatus)
            when (transaction.status) {
                "Completed" -> statusView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                "Pending"   -> statusView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                "Canceled"  -> statusView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }

            findViewById<Button>(R.id.btnClose).setOnClickListener { dismiss() }
            show()
        }
    }
}