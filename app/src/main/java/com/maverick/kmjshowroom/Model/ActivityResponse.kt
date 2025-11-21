package com.maverick.kmjshowroom.Model

data class ActivityResponse(
    val code: Int,
    val data: List<ActivityItem>
)

data class ActivityItem(
    val id: Int,
    val activity_type: String,
    val description: String,
    val created_at: String
)