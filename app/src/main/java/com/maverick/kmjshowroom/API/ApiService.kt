package com.maverick.kmjshowroom.API

import com.maverick.kmjshowroom.Model.*
import MobilDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("admin/login.php")
    fun login(
        @Body body: Map<String, String>
    ): Call<LoginResponse>

    @POST("admin/register.php")
    fun registerUser(
        @Body body: Map<String, String>
    ): Call<RegisterResponse>

    @GET("admin/check_user.php")
    suspend fun checkUser(): Response<CheckUserResponse>

    @GET("admin/get_dashboard_stats.php")
    suspend fun getDashboardStats(): Response<DashboardResponse>

    @GET("admin/get_recent_activity.php")
    suspend fun getRecentActivity(
        @Query("limit") limit: Int
    ): Response<ActivityResponse>

    @GET("admin/get_activity_detail.php")
    suspend fun getActivityDetail(
        @Query("id") id: Int
    ): Response<ActivityDetailResponse>

    @GET("admin/get_general.php")
    suspend fun getGeneral(): Response<GeneralResponse>

    @POST("admin/update_general.php")
    suspend fun updateGeneral(@Body body: Map<String, Int>): Response<UpdateGeneralResponse>

    @GET("admin/mobil_list.php")
    suspend fun getMobilList(): Response<MobilListResponse>

    @GET("admin/mobil_detail.php")
    fun getMobilDetail(@Query("kode_mobil") kode: String): Call<MobilDetailResponse>

    @Multipart
    @POST("admin/mobil_tambah.php")
    fun uploadMobil(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("admin/mobil_tambah.php")
    fun deleteMobil(
        @Field("delete") delete: Boolean = true,
        @Field("kode_mobil") kodeMobil: String
    ): Call<GenericResponse>

    // ==================== TRANSAKSI ENDPOINTS (UPDATED) ====================

    // GET LIST TRANSAKSI
    @GET("admin/transaksi_get.php")
    fun getTransaksiList(
        @Query("action") action: String = "list"
    ): Call<TransaksiListResponse>

    // GET DETAIL TRANSAKSI
    @GET("admin/transaksi_get.php")
    fun getTransaksiDetail(
        @Query("action") action: String = "detail",
        @Query("id") kodeTransaksi: String
    ): Call<TransaksiDetailResponse>

    // CREATE TRANSAKSI (dengan jaminan)
    @FormUrlEncoded
    @POST("admin/transaksi_post.php")
    fun createTransaksi(
        @Field("action") action: String = "create",
        @Field("nama_pembeli") namaPembeli: String,
        @Field("no_hp") noHp: String,
        @Field("tipe_pembayaran") tipePembayaran: String,
        @Field("harga_akhir") hargaAkhir: Double,
        @Field("kode_mobil") kodeMobil: String,
        @Field("kode_user") kodeUser: String,
        @Field("status") status: String = "pending",
        @Field("note") note: String = "",
        @Field("nama_kredit") namaKredit: String = "",
        @Field("jaminan_ktp") jaminanKtp: Int = 0,
        @Field("jaminan_kk") jaminanKk: Int = 0,
        @Field("jaminan_rekening") jaminanRekening: Int = 0
    ): Call<CreateTransaksiResponse>

    @FormUrlEncoded
    @POST("admin/transaksi_post.php")
    fun updateTransaksi(
        @Field("action") action: String = "update",
        @Field("kode_transaksi") kodeTransaksi: String,
        @Field("nama_pembeli") namaPembeli: String,
        @Field("no_hp") noHp: String,
        @Field("tipe_pembayaran") tipePembayaran: String,
        @Field("harga_akhir") hargaAkhir: Double,
        @Field("kode_mobil") kodeMobil: String,
        @Field("kode_user") kodeUser: String,
        @Field("status") status: String,
        @Field("note") note: String = "",
        @Field("nama_kredit") namaKredit: String = "",
        @Field("jaminan_ktp") jaminanKtp: Int = 0,
        @Field("jaminan_kk") jaminanKk: Int = 0,
        @Field("jaminan_rekening") jaminanRekening: Int = 0
    ): Call<CreateTransaksiResponse>

    @GET("admin/get_laporan_gabungan.php")
    fun getLaporanGabungan(): Call<LaporanGabunganResponse>

    // LAPORAN PENJUALAN (sudah ada API-nya)
    @GET("admin/report_penjualan.php")
    fun getLaporanPenjualan(
        @Query("status") status: String = "completed"
    ): Call<ReportPenjualanResponse>
}