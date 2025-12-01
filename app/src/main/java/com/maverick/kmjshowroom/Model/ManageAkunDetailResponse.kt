package com.maverick.kmjshowroom.Model

data class ManageAkunDetailResponse(
    val kode: Int,
    val success: Boolean,
    val message: String,
    val data: ManageAkun
)