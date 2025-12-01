package com.maverick.kmjshowroom.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "http://192.168.1.10:80/API_kmj/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    // TAMBAH HELPER FUNCTION INI
    fun getImageUrl(path: String?): String {
        if (path.isNullOrEmpty()) return ""

        return when {
            path.startsWith("http") -> path // Udah full URL
            else -> BASE_URL + path // Gabung dengan base URL
        }
    }
}