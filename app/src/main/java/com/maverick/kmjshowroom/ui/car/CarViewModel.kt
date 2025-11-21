package com.maverick.kmjshowroom.ui.car

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.MobilItem
import kotlinx.coroutines.launch

class CarViewModel(application: Application) : AndroidViewModel(application) {

    val mobilListLiveData = MutableLiveData<List<MobilItem>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String?>()

    fun loadMobilList() {
        loadingLiveData.value = true
        errorLiveData.value = null

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMobilList()
                val body = response.body()

                if (response.isSuccessful && body != null && body.success) {
                    mobilListLiveData.postValue(body.data)
                } else {
                    errorLiveData.postValue("Gagal memuat data dari server")
                }

            } catch (e: Exception) {
                errorLiveData.postValue(e.localizedMessage ?: "Terjadi kesalahan tak terduga")
            } finally {
                loadingLiveData.postValue(false)
            }
        }
    }
}
