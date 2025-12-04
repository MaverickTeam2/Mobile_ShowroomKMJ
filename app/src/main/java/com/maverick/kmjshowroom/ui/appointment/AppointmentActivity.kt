package com.maverick.kmjshowroom.ui.appointment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.Appointment
import com.maverick.kmjshowroom.Model.AppointmentResponse
import com.maverick.kmjshowroom.Model.UpdateStatusResponse
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.ActivityAppointmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentBinding
    private lateinit var appointmentAdapter: AppointmentAdapter
    private var currentFilter = ""
    private var allAppointments: List<Appointment> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupRecyclerView()
        setupSpinner()
        setupBackButton()
        loadAppointments()
    }

    private fun setupHeader() {
        binding.headerInclude.textHeader.text = "Janji Temu"

        val searchBar = binding.headerInclude.searchBar

        searchBar.addTextChangedListener { text ->
            val keyword = text.toString().trim()
            filterSearch(keyword)
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(
            appointments = emptyList(),
            onRespondClick = { appointment ->
                updateAppointmentStatus(appointment.idInquire, "responded")
            },
            onCancelClick = { appointment ->
                showCancelDialog(appointment)
            },
            onMarkDoneClick = { appointment ->
                updateAppointmentStatus(appointment.idInquire, "closed")
            }
        )

        binding.rvAppointments.apply {  // ← Gunakan binding
            layoutManager = LinearLayoutManager(this@AppointmentActivity)
            adapter = appointmentAdapter
        }
    }

    private fun setupSpinner() {
        val filterOptions = arrayOf("Semua", "Pending", "Responded", "Selesai", "Dibatalkan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = adapter  // ← Gunakan binding

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = when (position) {
                    0 -> "" // Semua
                    1 -> "pending"
                    2 -> "responded"
                    3 -> "closed"
                    4 -> "canceled"
                    else -> ""
                }
                loadAppointments()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadAppointments() {
        showLoading()
        val call = if (currentFilter.isEmpty()) {
            ApiClient.apiService.getAppointments()
        } else {
            ApiClient.apiService.getAppointments(currentFilter)
        }

        call.enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(
                call: Call<AppointmentResponse>,
                response: Response<AppointmentResponse>
            ) {
                hideLoading()
                if (response.isSuccessful) {
                    val appointments = response.body()?.data ?: emptyList()

                    allAppointments = appointments
                    appointmentAdapter.updateData(appointments)

                    if (appointments.isEmpty()) {
                        Toast.makeText(
                            this@AppointmentActivity,
                            "Tidak ada data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@AppointmentActivity,
                        "Gagal memuat data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                hideLoading()
                Toast.makeText(
                    this@AppointmentActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun filterSearch(keyword: String) {

        if (keyword.isEmpty()) {
            appointmentAdapter.updateData(allAppointments)
            return
        }

        val filtered = allAppointments.filter { item ->
            val name = item.namaUser?.lowercase() ?: ""
            val phone = item.noTelp?.lowercase() ?: ""

            name.contains(keyword.lowercase()) || phone.contains(keyword.lowercase())
        }

        appointmentAdapter.updateData(filtered)
    }

    private fun updateAppointmentStatus(idInquire: Int, newStatus: String) {
        ApiClient.apiService.updateAppointment(idInquire, newStatus)
            .enqueue(object : Callback<UpdateStatusResponse> {
                override fun onResponse(
                    call: Call<UpdateStatusResponse>,
                    response: Response<UpdateStatusResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.code == 200) {
                            Toast.makeText(
                                this@AppointmentActivity,
                                body.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            loadAppointments() // Refresh data
                        } else {
                            Toast.makeText(
                                this@AppointmentActivity,
                                body?.message ?: "Gagal update status",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AppointmentActivity,
                            "Gagal update status: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UpdateStatusResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AppointmentActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showCancelDialog(appointment: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Batalkan Janji")
            .setMessage("Apakah Anda yakin ingin membatalkan janji dengan ${appointment.namaUser}?")
            .setPositiveButton("Ya") { _, _ ->
                updateAppointmentStatus(appointment.idInquire, "canceled")
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun showLoading() {
        binding.loadingProgress.visibility = View.VISIBLE
        binding.loadingProgress.playAnimation()
        binding.rvAppointments.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.loadingProgress.visibility = View.GONE
        binding.loadingProgress.pauseAnimation()
        binding.rvAppointments.visibility = View.VISIBLE
    }

}