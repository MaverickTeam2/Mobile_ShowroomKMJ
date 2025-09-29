package com.maverick.kmjshowroom.Model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

data class User(
    val id: String,
    val username: String
)

