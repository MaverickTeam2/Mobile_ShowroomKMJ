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
import com.maverick.kmjshowroom.ui.setting.SettingActivity
import kotlinx.coroutines.launch

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ManageAkunAdapter
    private var akunList = mutableListOf<ManageAkun>()
    private var filteredList = mutableListOf<ManageAkun>()

    private var isToggling = false

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
        binding.headerInclude.textHeader.text = "AKUN"
        setupRecyclerView()
        setupClickListeners()
        loadAkunList()

        binding.headerInclude.iconProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = ManageAkunAdapter(
            list = filteredList,
            onClick = { akun ->
                val intent = Intent(requireContext(), DetailAkunActivity::class.java)
                intent.putExtra(DetailAkunActivity.EXTRA_KODE_USER, akun.kode_user)
                startActivity(intent)
            },
            onToggleStatus = { akun ->
                if (!isToggling) {
                    toggleStatusAkun(akun)
                }
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

        // Setup search functionality
        binding.headerInclude.searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAkunList(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun loadAkunList() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                Log.d("AkunFragment", "Loading akun list...")
                val response = ApiClient.apiService.getManageAccList()

                Log.d("AkunFragment", "Response code: ${response.code()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data
                    akunList.clear()
                    akunList.addAll(data)

                    // Update filtered list juga
                    filteredList.clear()
                    filteredList.addAll(data)

                    adapter.updateData(filteredList)

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
            } finally {
                showLoading(false)
            }
        }
    }

    private fun toggleStatusAkun(akun: ManageAkun) {
        // TIDAK ADA LOADING ANIMATION untuk toggle
        isToggling = true

        lifecycleScope.launch {
            try {
                val newStatus = if (akun.isActive) 0 else 1

                val requestBody = UpdateStatusRequest(
                    kodeUser = akun.kode_user,
                    status = newStatus
                )

                Log.d("AkunFragment", "=== TOGGLE STATUS ===")
                Log.d("AkunFragment", "Kode User: ${akun.kode_user}")
                Log.d("AkunFragment", "Current Status: ${akun.status}")
                Log.d("AkunFragment", "New Status: $newStatus")

                val response = ApiClient.apiService.updateStatusManageAccount(requestBody)

                Log.d("AkunFragment", "Response Code: ${response.code()}")
                Log.d("AkunFragment", "Response Body: ${response.body()}")

                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AkunFragment", "Error Body: $errorBody")
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    // UPDATE LOKAL SAJA - JANGAN RELOAD
                    val index = akunList.indexOfFirst { it.kode_user == akun.kode_user }
                    if (index != -1) {
                        // Buat object baru dengan status terupdate
                        val updatedAkun = akunList[index].copy(status = newStatus)
                        akunList[index] = updatedAkun

                        // Update juga di filtered list
                        val filteredIndex = filteredList.indexOfFirst { it.kode_user == akun.kode_user }
                        if (filteredIndex != -1) {
                            filteredList[filteredIndex] = updatedAkun
                            adapter.notifyItemChanged(filteredIndex)
                        }

                        Log.d("AkunFragment", "Updated status locally for ${akun.full_name}")
                    }

                    val statusText = if (newStatus == 1) "diaktifkan" else "dinonaktifkan"
                    Toast.makeText(
                        requireContext(),
                        "Akun ${akun.full_name} berhasil $statusText",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    val errorMsg = response.body()?.message ?: "Gagal mengubah status"
                    Log.e("AkunFragment", "Error: $errorMsg")
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("AkunFragment", "Exception: ${e.message}", e)
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isToggling = false
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
        showLoading(true)

        lifecycleScope.launch {
            try {
                Log.d("AkunFragment", "Deleting akun: ${akun.kode_user}")
                val response = ApiClient.apiService.deleteManageAccount(akun.kode_user)

                Log.d("AkunFragment", "Delete Response Code: ${response.code()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        requireContext(),
                        "Akun berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadAkunList()
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal menghapus akun"
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
            } finally {
                showLoading(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Filter akun list berdasarkan query
     */
    private fun filterAkunList(query: String) {
        val searchQuery = query.lowercase().trim()

        filteredList.clear()

        if (searchQuery.isEmpty()) {
            // Jika search kosong, tampilkan semua
            filteredList.addAll(akunList)
        } else {
            // Filter berdasarkan nama, email, username, atau role
            filteredList.addAll(
                akunList.filter { akun ->
                    akun.full_name.lowercase().contains(searchQuery) ||
                            akun.email.lowercase().contains(searchQuery) ||
                            akun.username?.lowercase()?.contains(searchQuery) == true ||
                            akun.role.lowercase().contains(searchQuery)
                }
            )
        }

        adapter.updateData(filteredList)

        Log.d("AkunFragment", "Search: '$query' - Found ${filteredList.size} results")
    }

    /**
     * Fungsi untuk menampilkan/menyembunyikan loading animation
     * HANYA untuk load data awal
     */
    private fun showLoading(show: Boolean) {
        if (show) {
            binding.recyclerAkun.visibility = View.GONE
            binding.btnTambah.visibility = View.GONE
            binding.loadingProgress.visibility = View.VISIBLE
            binding.loadingProgress.playAnimation()
        } else {
            binding.loadingProgress.pauseAnimation()
            binding.loadingProgress.visibility = View.GONE
            binding.recyclerAkun.visibility = View.VISIBLE
            binding.btnTambah.visibility = View.VISIBLE
        }
    }
}