package com.maverick.kmjshowroom.Model

data class RegisterResponse(
    val code: Int,
    val message: String?,
    val role: String?,
    val kode_user: String?,
    val avatar_url: String?,
    val token: String?
)
