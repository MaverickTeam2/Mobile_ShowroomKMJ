package com.maverick.kmjshowroom.ui.setting.model

data class DaySchedule(
    val dayName: String,
    var available: Boolean,
    val slots: MutableList<String>
)