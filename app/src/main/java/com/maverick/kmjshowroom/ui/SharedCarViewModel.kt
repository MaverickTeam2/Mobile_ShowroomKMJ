package com.maverick.kmjshowroom.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.CarData
import com.maverick.kmjshowroom.Model.Transaction
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class SharedCarViewModel : ViewModel() {

    val carList = MutableLiveData<List<CarData>>(emptyList())
    val selectedCar = MutableLiveData<CarData?>()
    val selectedTipe = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    private val _transaksiList = MutableLiveData<MutableList<Transaction>>(mutableListOf())
    val transaksiList: MutableLiveData<MutableList<Transaction>> = _transaksiList

    private val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    init {
        loadMobilFromApi()
    }

    fun loadMobilFromApi() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val response = ApiClient.apiService.getMobilList()

                if (response.isSuccessful && response.body()?.code == 200) {
                    val mobilItems = response.body()?.data ?: emptyList()

                    val carDataList = mobilItems.map { item ->
                        CarData(
                            kodeMobil = item.kode_mobil,
                            title = item.nama_mobil,
                            year = item.tahun_mobil.toString(),
                            warna = item.warna_exterior,
                            fotoUrl = item.foto,
                            status = item.status,
                            jarakTempuh = "${formatNumber(item.jarak_tempuh)} km",
                            bahanBakar = item.tipe_bahan_bakar,
                            tipeKendaraan = "", // Bisa ditambah di API jika perlu
                            angsuran = formatter.format(item.angsuran),
                            dp = formatter.format(item.dp),
                            fullPrice = item.full_prize
                        )
                    }

                    carList.value = carDataList
                    Log.d("SharedCarViewModel", "Loaded ${carDataList.size} cars from API")
                } else {
                    errorMessage.value = "Gagal memuat data mobil"
                    Log.e("SharedCarViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.message}"
                Log.e("SharedCarViewModel", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun formatNumber(number: Int): String {
        return NumberFormat.getNumberInstance(Locale("in", "ID")).format(number)
    }

    fun updateRandomTipe() {
        val tipeList = listOf("Sport", "SUV", "Coupe", "Sedan", "Hypercar")
        selectedTipe.value = tipeList.random()
    }

    fun addDraft(transaction: Transaction) {
        val currentList = _transaksiList.value ?: mutableListOf()
        currentList.add(transaction.copy(status = "Pending"))
        _transaksiList.value = currentList
    }

    fun completeTransaction(id: String) {
        val currentList = _transaksiList.value ?: return
        val updatedList = currentList.map { txn ->
            if (txn.id == id) txn.copy(status = "Completed") else txn
        }
        _transaksiList.value = updatedList as MutableList<Transaction>
    }

    fun tambahTransaksi(transaksi: Transaction) {
        val currentList = _transaksiList.value ?: mutableListOf()
        currentList.add(transaksi)
        _transaksiList.value = currentList
    }

    fun clearSelectedCar() {
        selectedCar.value = null
    }
}