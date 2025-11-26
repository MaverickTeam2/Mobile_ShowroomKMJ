package com.maverick.kmjshowroom.ui.transaksi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.Transaction
import com.maverick.kmjshowroom.Model.TransaksiListResponse
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentTransaksiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class TransaksiActivity : AppCompatActivity() {

    private lateinit var binding: FragmentTransaksiBinding
    private lateinit var transaksiAdapter: TransactionAdapter
    private val allTransactions = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        loadTransaksiFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        loadTransaksiFromDatabase()
    }

    private fun setupUI() {
        binding.headerInclude.textHeader.text = "TRANSAKSI"

        binding.btnTambah.setOnClickListener {
            val intent = Intent(this, AddTrnActivity1::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        transaksiAdapter = TransactionAdapter(this, allTransactions).apply {
            onDetailClick = { transaction ->
                showTransactionDetailDialog(transaction)
            }
        }

        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@TransaksiActivity)
            adapter = transaksiAdapter
        }
        binding.transaksiContainer.removeAllViews()
        binding.transaksiContainer.addView(recyclerView)
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

                        // Format harga berdasarkan tipe pembayaran
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
                            }
                        )
                        allTransactions.add(transaction)
                    }

                    transaksiAdapter.notifyDataSetChanged()

                    if (allTransactions.isEmpty()) {
                        Toast.makeText(
                            this@TransaksiActivity,
                            "Belum ada transaksi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorMsg = response.body()?.code ?: "Unknown error"
                    Toast.makeText(
                        this@TransaksiActivity,
                        "Gagal memuat transaksi (Code: $errorMsg)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<TransaksiListResponse>, t: Throwable) {
                Toast.makeText(
                    this@TransaksiActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showTransactionDetailDialog(transaction: Transaction) {
        val dialog = Dialog(this).apply {
            setContentView(R.layout.dialog_transaction_detail)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            findViewById<TextView>(R.id.tvId).text = transaction.id
            findViewById<TextView>(R.id.tvDate).text = transaction.date
            findViewById<TextView>(R.id.tvCustomer).text = transaction.customerName
            findViewById<TextView>(R.id.tvPhone).text = transaction.phoneNumber.ifEmpty { "-" }
            findViewById<TextView>(R.id.tvCar).text = transaction.car
            findViewById<TextView>(R.id.tvDesc).text = transaction.description.ifEmpty { "-" }
            findViewById<TextView>(R.id.tvPrice).text = transaction.price
            findViewById<TextView>(R.id.tvDeal).text = transaction.dealPrice

            val statusView = findViewById<TextView>(R.id.tvStatus)
            statusView.text = transaction.status

            when (transaction.status) {
                "Completed" -> statusView.setTextColor(
                    ContextCompat.getColor(this@TransaksiActivity, R.color.green)
                )
                "Pending" -> statusView.setTextColor(
                    ContextCompat.getColor(this@TransaksiActivity, R.color.orange)
                )
                "Canceled" -> statusView.setTextColor(
                    ContextCompat.getColor(this@TransaksiActivity, R.color.red)
                )
            }

            findViewById<Button>(R.id.btnClose).setOnClickListener { dismiss() }
            show()
        }
    }
}