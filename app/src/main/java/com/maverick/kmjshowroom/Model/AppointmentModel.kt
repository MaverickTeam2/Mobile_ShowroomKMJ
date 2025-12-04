package com.maverick.kmjshowroom.Model

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<Appointment>?
)

data class Appointment(
    @SerializedName("id_inquire") val idInquire: Int,
    @SerializedName("kode_user") val kodeUser: String,
    @SerializedName("kode_mobil") val kodeMobil: String,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("waktu") val waktu: String,
    @SerializedName("no_telp") val noTelp: String,
    @SerializedName("note") val note: String?,
    @SerializedName("status") val status: String,
    @SerializedName("uji_beli") val ujiBeli: String,
    @SerializedName("jenis_janji") val jenisJanji: String,
    @SerializedName("nama_user") val namaUser: String?,
    @SerializedName("email_user") val emailUser: String?,
    @SerializedName("nama_mobil") val namaMobil: String?
)

data class UpdateAppointmentStatusRequest(
    @SerializedName("id_inquire") val idInquire: Int,
    @SerializedName("status") val status: String
)

data class UpdateStatusResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Any?
)