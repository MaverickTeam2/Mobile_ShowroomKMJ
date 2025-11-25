package com.maverick.kmjshowroom.Model

data class Akun(
    val nama: String,
    val email: String,
    val lastLogin: String,
    val role: String,
    val status: Int,
    val aktif: Boolean
)