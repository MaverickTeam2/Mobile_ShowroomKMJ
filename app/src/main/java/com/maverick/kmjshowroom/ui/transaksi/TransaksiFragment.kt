package com.maverick.kmjshowroom.ui.transaksi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.Transaction
import com.maverick.kmjshowroom.Model.TransaksiListResponse
import com.maverick.kmjshowroom.Model.CreateTransaksiResponse
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentTransaksiBinding
import com.maverick.kmjshowroom.ui.setting.SettingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!

    private lateinit var transaksiAdapter: TransactionAdapter
    private val allTransactions = mutableListOf<Transaction>()
    private val filteredTransactions = mutableListOf<Transaction>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        setupRecyclerView()
        setupSearch()
        loadTransaksiFromDatabase()

        binding.headerInclude.iconProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        loadTransaksiFromDatabase()
    }

    private fun setupUI() {
        binding.headerInclude.textHeader.text = "TRANSAKSI"

        // Tampilkan search bar
        binding.headerInclude.searchBar.visibility = View.VISIBLE
        binding.headerInclude.searchBar.inputType = android.text.InputType.TYPE_CLASS_TEXT

        binding.btnTambah.setOnClickListener {
            val intent = Intent(requireContext(), AddTrnActivity1::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        transaksiAdapter = TransactionAdapter(requireContext(), filteredTransactions).apply {
            onDetailClick = { transaction ->
                showTransactionDetailDialog(transaction)
            }
        }

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transaksiAdapter
        }

        binding.transaksiContainer.removeAllViews()
        binding.transaksiContainer.addView(recyclerView)
    }

    private fun setupSearch() {
        binding.headerInclude.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTransactions(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterTransactions(query: String) {
        filteredTransactions.clear()

        if (query.isEmpty()) {
            filteredTransactions.addAll(allTransactions)
        } else {
            val searchQuery = query.lowercase().trim()

            allTransactions.forEach { transaction ->
                val matchesKode = transaction.id.lowercase().contains(searchQuery)
                val matchesNama = transaction.customerName.lowercase().contains(searchQuery)
                val matchesMobil = transaction.car.lowercase().contains(searchQuery)
                val matchesKasir = transaction.kasir.lowercase().contains(searchQuery)

                if (matchesKode || matchesNama || matchesMobil || matchesKasir) {
                    filteredTransactions.add(transaction)
                }
            }
        }

        transaksiAdapter.notifyDataSetChanged()

        if (filteredTransactions.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                "Tidak ada transaksi yang sesuai dengan pencarian",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadTransaksiFromDatabase() {
        ApiClient.apiService.getTransaksiList().enqueue(object : Callback<TransaksiListResponse> {
            override fun onResponse(
                call: Call<TransaksiListResponse>,
                response: Response<TransaksiListResponse>
            ) {
                if (response.isSuccessful && response.body()?.code == "200") {
                    val transaksiItems = response.body()?.data ?: emptyList()

                    allTransactions.clear()
                    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                    transaksiItems.forEach { item ->
                        val isKredit = item.tipePembayaran?.lowercase() == "kredit"
                        val tenor = item.tenor ?: 48

                        val priceDisplay: String
                        val dealPriceDisplay: String

                        if (isKredit) {
                            val angsuranAsli = item.angsuran ?: (item.hargaAsli?.div(tenor) ?: 0.0)
                            val angsuranDeal = item.hargaAkhir / tenor

                            priceDisplay = "${formatter.format(angsuranAsli)} x $tenor"
                            dealPriceDisplay = "${formatter.format(angsuranDeal)} x $tenor"
                        } else {
                            priceDisplay = formatter.format(item.hargaAsli ?: item.hargaAkhir)
                            dealPriceDisplay = formatter.format(item.hargaAkhir)
                        }

                        val transaction = Transaction(
                            id = item.kodeTransaksi,
                            date = item.tanggal,
                            customerName = item.namaPembeli,
                            phoneNumber = item.noHp ?: "-",
                            car = item.namaMobil ?: "-",
                            description = if (isKredit) "Kredit ($tenor bulan)" else "Cash",
                            price = priceDisplay,
                            dealPrice = dealPriceDisplay,
                            status = when (item.status.lowercase()) {
                                "completed" -> "Completed"
                                "pending" -> "Pending"
                                "cancelled", "canceled" -> "Canceled"
                                else -> item.status.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                }
                            },
                            kasir = item.kasir ?: "-"
                        )
                        allTransactions.add(transaction)
                    }

                    // Apply current search filter
                    val currentQuery = binding.headerInclude.searchBar.text.toString()
                    filterTransactions(currentQuery)

                    if (allTransactions.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Belum ada transaksi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal memuat transaksi: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<TransaksiListResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showTransactionDetailDialog(transaction: Transaction) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_transaction_detail)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set data ke views
        dialog.findViewById<TextView>(R.id.tvId).text = transaction.id
        dialog.findViewById<TextView>(R.id.tvDate).text = transaction.date
        dialog.findViewById<TextView>(R.id.tvCustomer).text = transaction.customerName
        dialog.findViewById<TextView>(R.id.tvPhone).text = transaction.phoneNumber.ifEmpty { "-" }
        dialog.findViewById<TextView>(R.id.tvCar).text = transaction.car
        dialog.findViewById<TextView>(R.id.tvDesc).text = transaction.description.ifEmpty { "-" }
        dialog.findViewById<TextView>(R.id.tvPrice).text = transaction.price
        dialog.findViewById<TextView>(R.id.tvDeal).text = transaction.dealPrice
        dialog.findViewById<TextView>(R.id.tvKasir).text = transaction.kasir

        val statusSpinner = dialog.findViewById<Spinner>(R.id.statusSpinner)
        val statusOptions = arrayOf("Pending", "Completed", "Canceled")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = spinnerAdapter

        val currentPosition = when (transaction.status) {
            "Pending" -> 0
            "Completed" -> 1
            "Canceled" -> 2
            else -> 0
        }
        statusSpinner.setSelection(currentPosition)

        var isInitialSelection = true

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isInitialSelection) {
                    isInitialSelection = false
                    return
                }

                val newStatus = statusOptions[position]

                if (newStatus != transaction.status) {
                    updateTransactionStatus(transaction.id, newStatus) { success ->
                        if (success) {
                            transaction.status = newStatus
                            loadTransaksiFromDatabase()
                            dialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Status berhasil diubah menjadi $newStatus",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            statusSpinner.setSelection(currentPosition)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        dialog.findViewById<Button>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateTransactionStatus(
        kodeTransaksi: String,
        newStatus: String,
        callback: (success: Boolean) -> Unit
    ) {
        val statusApi = newStatus.lowercase()

        ApiClient.apiService.getTransaksiDetail(kodeTransaksi = kodeTransaksi)
            .enqueue(object : Callback<com.maverick.kmjshowroom.Model.TransaksiDetailResponse> {
                override fun onResponse(
                    call: Call<com.maverick.kmjshowroom.Model.TransaksiDetailResponse>,
                    response: Response<com.maverick.kmjshowroom.Model.TransaksiDetailResponse>
                ) {
                    if (response.isSuccessful && response.body()?.code == "200") {
                        val detail = response.body()?.data
                        detail?.let { d ->
                            val kodeUser = d.kasir?.takeIf { it.length <= 10 && it.isNotEmpty() } ?: "US001"

                            ApiClient.apiService.updateTransaksi(
                                kodeTransaksi = kodeTransaksi,
                                namaPembeli = d.namaPembeli,
                                noHp = d.noHp ?: "",
                                tipePembayaran = d.tipePembayaran,
                                hargaAkhir = d.hargaAkhir,
                                kodeMobil = d.kodeMobil,
                                kodeUser = kodeUser,
                                status = statusApi,
                                note = d.note ?: "",
                                namaKredit = d.namaKredit ?: "",
                                jaminanKtp = d.jaminan?.ktp ?: 0,
                                jaminanKk = d.jaminan?.kk ?: 0,
                                jaminanRekening = d.jaminan?.rekening ?: 0
                            ).enqueue(object : Callback<CreateTransaksiResponse> {
                                override fun onResponse(
                                    call: Call<CreateTransaksiResponse>,
                                    response: Response<CreateTransaksiResponse>
                                ) {
                                    if (response.isSuccessful && response.body()?.code == "200") {
                                        callback(true)
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Gagal update status: ${response.body()?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        callback(false)
                                    }
                                }

                                override fun onFailure(call: Call<CreateTransaksiResponse>, t: Throwable) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    callback(false)
                                }
                            })
                        } ?: callback(false)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal mendapatkan detail transaksi",
                            Toast.LENGTH_SHORT
                        ).show()
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<com.maverick.kmjshowroom.Model.TransaksiDetailResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    callback(false)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}