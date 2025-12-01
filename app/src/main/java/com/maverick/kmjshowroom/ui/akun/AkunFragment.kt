package com.maverick.kmjshowroom.ui.akun

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Adapter.ManageAkunAdapter
import com.maverick.kmjshowroom.Model.ManageAkun
import com.maverick.kmjshowroom.Model.UpdateStatusRequest
import com.maverick.kmjshowroom.databinding.FragmentAkunBinding
import kotlinx.coroutines.launch

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ManageAkunAdapter
    private var akunList = mutableListOf<ManageAkun>()

    private val addAkunLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadAkunList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadAkunList()
    }

    private fun setupRecyclerView() {
        adapter = ManageAkunAdapter(
            list = akunList,
            onClick = { akun ->
                val intent = Intent(requireContext(), DetailAkunActivity::class.java)
                intent.putExtra(DetailAkunActivity.EXTRA_KODE_USER, akun.kode_user)
                startActivity(intent)
            },
            onToggleStatus = { akun ->
                toggleStatusAkun(akun)
            },
            onDelete = { akun ->
                showDeleteConfirmation(akun)
            }
        )

        binding.recyclerAkun.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AkunFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnTambah.setOnClickListener {
            val intent = Intent(requireContext(), AddAkunActivity::class.java)
            addAkunLauncher.launch(intent)
        }
    }

    private fun loadAkunList() {
        lifecycleScope.launch {
            try {
                Log.d("AkunFragment", "Loading akun list...")
                val response = ApiClient.apiService.getManageAccList()

                Log.d("AkunFragment", "Response code: ${response.code()}")
                Log.d("AkunFragment", "Response body: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data
                    akunList.clear()
                    akunList.addAll(data)
                    adapter.updateData(akunList)

                    Log.d("AkunFragment", "Loaded ${akunList.size} accounts")
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal memuat data"
                    Log.e("AkunFragment", "Error: $errorMsg")
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("AkunFragment", "Exception: ${e.message}", e)
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // In AkunFragment.kt

    private fun toggleStatusAkun(akun: ManageAkun) {
        lifecycleScope.launch {
            try {
                val newStatus = if (akun.isActive) 0 else 1

                // Create an instance of UpdateStatusRequest instead of a HashMap
                val requestBody = UpdateStatusRequest(
                    kodeUser = akun.kode_user,
                    status = newStatus
                )

                Log.d("AkunFragment", "=== TOGGLE STATUS ===")
                Log.d("AkunFragment", "Kode User: ${akun.kode_user}")
                Log.d("AkunFragment", "New Status: $newStatus")
                Log.d("AkunFragment", "Request Body: $requestBody")

                // Pass the new requestBody object to the service method
                val response = ApiClient.apiService.updateStatusManageAccount(requestBody)

                Log.d("AkunFragment", "Response Code: ${response.code()}")
                // ... rest of your code remains the same

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        requireContext(),
                        "Status berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadAkunList()
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal mengubah status"
                    Log.e("AkunFragment", "Error Message: $errorMsg")
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                    loadAkunList()
                }

            } catch (e: Exception) {
                Log.e("AkunFragment", "Exception Type: ${e.javaClass.simpleName}")
                Log.e("AkunFragment", "Exception Message: ${e.message}", e)
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                loadAkunList()
            }
        }
    }


    private fun showDeleteConfirmation(akun: ManageAkun) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage("Apakah Anda yakin ingin menghapus akun ${akun.full_name}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteAkun(akun)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteAkun(akun: ManageAkun) {
        lifecycleScope.launch {
            try {
                Log.d("AkunFragment", "Deleting akun: ${akun.kode_user}")
                val response = ApiClient.apiService.deleteManageAccount(akun.kode_user)

                Log.d("AkunFragment", "Delete Response Code: ${response.code()}")
                Log.d("AkunFragment", "Delete Response Body: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        requireContext(),
                        "Akun berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadAkunList()
                } else {
                    val errorMsg = response.body()?.message ?: response.errorBody()?.string() ?: "Gagal menghapus akun"
                    Log.e("AkunFragment", "Delete Error: $errorMsg")
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("AkunFragment", "Delete Exception: ${e.message}", e)
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAkunList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}