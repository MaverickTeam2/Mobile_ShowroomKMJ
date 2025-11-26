package com.maverick.kmjshowroom.Model

data class CreateScheduleRequest(
    val hari: String,
    val slot_index: Int,
    val jam_buka: String,
    val jam_tutup: String,
    val is_active: Int = 1
)
