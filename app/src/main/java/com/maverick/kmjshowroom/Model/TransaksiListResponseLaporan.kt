package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName

data class TransaksiListResponseLaporan(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<TransaksiItemResponse>
)