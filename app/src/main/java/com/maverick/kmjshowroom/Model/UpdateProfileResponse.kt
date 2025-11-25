package com.maverick.kmjshowroom.Model

data class UpdateProfileResponse(
    val code: Int,
    val message: String?,
    val avatar_url: String?,
    val user: UserData?
)