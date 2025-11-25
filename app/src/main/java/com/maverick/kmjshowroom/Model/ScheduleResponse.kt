package com.maverick.kmjshowroom.Model

data class ScheduleItem(
    val id_schedule: Int,
    val hari: String,
    val slot_index: Int,
    var jam_buka: String,
    var jam_tutup: String,
    var is_active: Int
)

data class ScheduleResponse(
    val code: Int,
    val message: String,
    val data: List<ScheduleItem>
)

data class GenericScheduleResponse(
    val code: Int,
    val message: String
)
