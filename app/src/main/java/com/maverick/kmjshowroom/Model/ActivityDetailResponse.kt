package com.maverick.kmjshowroom.Model

data class ActivityDetailResponse(
    val code: Int,
    val data: ActivityDetail
)

data class ActivityDetail(
    val id: Int,
    val kode_user: String,
    val full_name: String?,
    val activity_type: String,
    val description: String,
    val created_at: String
)