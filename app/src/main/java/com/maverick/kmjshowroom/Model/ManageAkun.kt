package com.maverick.kmjshowroom.Model

data class ManageAkun(
    val kode_user: String,
    val full_name: String,
    val email: String,
    val username: String? = null,
    val role: String,
    val avatar_url: String? = null,
    val no_telp: String? = null,
    val alamat: String? = null,
    val status: Int,
    val last_login: String? = null,
    val updated_at: String? = null,
    val created_at: String? = null
) {
    val isActive: Boolean
        get() = status == 1

    // Helper untuk format last login
    val formattedLastLogin: String
        get() = if (!last_login.isNullOrEmpty()) {
            "Last login: $last_login"
        } else {
            "Last login: Belum pernah"
        }

    // Helper untuk badge status
    val statusText: String
        get() = if (isActive) "Aktif" else "Nonaktif"
}