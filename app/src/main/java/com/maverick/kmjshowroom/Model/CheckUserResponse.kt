package com.maverick.kmjshowroom.Model

data class CheckUserResponse(
    val code: Int,
    val message: String?,
    val user_count: Int?
)