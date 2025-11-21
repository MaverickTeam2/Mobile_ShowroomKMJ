package com.maverick.kmjshowroom.Model

data class GeneralResponse(
    val code: Int,
    val message: String,
    val data: GeneralData?
)

data class GeneralData(
    val id_general: Int,
    val showroom_status: Int,
    val jual_mobil: Int,
    val schedule_pelanggan: Int
)