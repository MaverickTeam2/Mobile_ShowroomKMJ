package com.maverick.kmjshowroom.API

import com.maverick.kmjshowroom.Model.ActivityDetailResponse
import com.maverick.kmjshowroom.Model.ActivityResponse
import MobilDetailResponse
import com.google.gson.annotations.SerializedName
import com.maverick.kmjshowroom.Model.*
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

    @GET("admin/get_user_from_token.php")
    fun getUserFromToken(
        @Header("Authorization") token: String
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

    @GET("admin/get_contacts_showrooms.php")
    suspend fun getContacts(): Response<ContactsResponse>

    @POST("admin/create_contact_showrooms.php")
    suspend fun createContact(@Body body: Map<String, String?>): Response<GenericResponse>

    @POST("admin/update_contact_showrooms.php")
    suspend fun updateContact(@Body body: Map<String, String?>): Response<GenericResponse>

    @POST("admin/get_schedule_showroom.php")
    suspend fun getSchedule(): ScheduleResponse

    @POST("admin/toggle_day_schedule_showroom.php")
    suspend fun toggleDaySchedule(@Body body: Map<String, Int>): GenericScheduleResponse

    @POST("admin/create_schedule_showroom.php")
    fun createSchedule(
        @Body req: CreateScheduleRequest
    ): Call<GenericScheduleResponse>

    @POST("admin/update_schedule_showroom.php")
    fun updateSchedule(
        @Body req: UpdateScheduleRequest
    ): Call<GenericScheduleResponse>

    @GET("admin/delete_schedule_showroom.php")
    fun deleteSchedule(
        @Query("id_schedule") id: Int
    ): Call<GenericScheduleResponse>

    @Multipart
    @POST("admin/update_profile.php")
    fun updateProfile(
        @Part("kode_user") kode_user: RequestBody,
        @Part("full_name") full_name: RequestBody?,
        @Part("no_telp") no_telp: RequestBody?,
        @Part("alamat") alamat: RequestBody?,
        @Part avatar_file: MultipartBody.Part?
    ): Call<UpdateProfileResponse>
}