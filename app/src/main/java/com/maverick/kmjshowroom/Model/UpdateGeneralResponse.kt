package com.maverick.kmjshowroom.Model

data class UpdateGeneralResponse(
    val code: Int,
    val message: String,
    val updated: Map<String, Int>?
)