package com.maverick.kmjshowroom.Model

data class LoginResponse(
    val code: Int,
    val message: String?,
    val token: String?,
    val user: UserData?
)

data class UserData(
    val kode_user: String?,
    val username: String?,
    val full_name: String?,
    val email: String?,
    val no_telp: String?,
    val alamat: String?,
    val role: String?,
    val avatar_url: String?,
    val provider_type: String?,
    val status: Int?
)
