package com.maverick.kmjshowroom.ui.car

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.MobilItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CarViewModel(application: Application) : AndroidViewModel(application) {

    val mobilListLiveData = MutableLiveData<List<MobilItem>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String?>()

    // ✅ Track current job untuk cancel kalau load ulang
    private var loadJob: Job? = null

    fun loadMobilList() {
        // ✅ Cancel job lama jika masih berjalan
        loadJob?.cancel()

        Log.d("CarViewModel", "=== START LOADING ===")

        loadingLiveData.value = true
        errorLiveData.value = null

        loadJob = viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMobilList()
                val body = response.body()

                Log.d("CarViewModel", "Response code: ${response.code()}")
                Log.d("CarViewModel", "Response successful: ${response.isSuccessful}")
                Log.d("CarViewModel", "Body code: ${body?.code}")
                Log.d("CarViewModel", "Data size: ${body?.data?.size}")

                if (response.isSuccessful && body != null && body.code == 200) {
                    val newData = body.data

                    // ✅ PENTING: Log detail untuk debug duplikat
                    Log.d("CarViewModel", "=== NEW DATA FROM API ===")
                    newData.forEachIndexed { index, item ->
                        Log.d("CarViewModel", "  [$index] ${item.kode_mobil} - ${item.nama_mobil}")
                    }

                    // ✅ PENTING: Clear dan set data baru (tidak append!)
                    mobilListLiveData.postValue(newData)

                    Log.d("CarViewModel", "=== DATA POSTED TO LIVEDATA ===")
                } else {
                    val errorMsg = when {
                        body == null -> "Response body kosong"
                        body.code != 200 -> "Server error: code ${body.code}"
                        else -> "Gagal memuat data dari server"
                    }
                    Log.e("CarViewModel", "Error: $errorMsg")
                    errorLiveData.postValue(errorMsg)
                }

            } catch (e: Exception) {
                Log.e("CarViewModel", "Exception: ${e.localizedMessage}", e)
                errorLiveData.postValue(e.localizedMessage ?: "Terjadi kesalahan tak terduga")
            } finally {
                loadingLiveData.postValue(false)
                Log.d("CarViewModel", "=== LOADING FINISHED ===")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadJob?.cancel()
        Log.d("CarViewModel", "=== ViewModel CLEARED ===")
    }
}