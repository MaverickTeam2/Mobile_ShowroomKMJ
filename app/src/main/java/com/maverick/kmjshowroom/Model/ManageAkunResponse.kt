package com.maverick.kmjshowroom.Model

data class ManageAkunResponse(
    val kode: Int,
    val success: Boolean,
    val message: String,
    val data: List<ManageAkun>
)