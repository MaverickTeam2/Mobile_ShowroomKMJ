package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName

data class UpdateStatusRequest(
    @SerializedName("kode_user")
    val kodeUser: String,

    @SerializedName("status")
    val status: Int
)
    