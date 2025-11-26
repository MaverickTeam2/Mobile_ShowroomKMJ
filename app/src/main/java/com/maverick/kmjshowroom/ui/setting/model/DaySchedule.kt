package com.maverick.kmjshowroom.ui.setting.model

import com.maverick.kmjshowroom.Model.ScheduleItem

data class DaySchedule(
    val dayName: String,
    var available: Boolean,
    var slots: MutableList<ScheduleItem>
)