package com.maverick.kmjshowroom.Model

data class ContactsResponse(
    val code: Int,
    val data: List<ContactItem>
)

data class ContactItem(
    val id_contact: String,
    val whatsapp: String?,
    val instagram_url: String?,
    val facebook_url: String?,
    val tiktok_url: String?,
    val youtube_url: String?
)