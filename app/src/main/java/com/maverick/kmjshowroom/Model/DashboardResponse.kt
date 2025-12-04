package com.maverick.kmjshowroom.Model

data class DashboardResponse(
    val code: Int,
    val data: DashboardData?
)

data class DashboardData(
    val total_mobil_available: Int,
    val total_transaksi_bulan_ini: Int,
    val total_pendapatan_bulan_ini: Long,
    val total_mobil_reserved: Int
)
