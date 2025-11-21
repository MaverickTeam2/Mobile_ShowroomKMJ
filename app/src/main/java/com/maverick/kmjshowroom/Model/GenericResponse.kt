package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName

/**
 * Response umum dari API seperti:
 * - tambah mobil
 * - update mobil
 * - delete mobil
 */

data class GenericResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("code")
    val code: Int = 0,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("kode_mobil")
    val kode_mobil: String? = null
)