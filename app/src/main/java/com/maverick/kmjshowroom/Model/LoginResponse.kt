package com.maverick.kmjshowroom.Model

data class LoginResponse(
    val code: Int,
    val message: String?,
    val user: UserData?
)

data class UserData(
    val kode_user: String?,
    val username: String?,
    val full_name: String?,
    val email: String?,
    val role: String?,
    val provider_type: String?
)
