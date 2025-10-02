package com.maverick.kmjshowroom.Model

data class Transaction(
    val id: String,
    val date: String,
    val customerName: String,
    val phoneNumber: String,
    val car: String,
    val description: String,
    val price: String,
    val dealPrice: String,
    val status: String
)
