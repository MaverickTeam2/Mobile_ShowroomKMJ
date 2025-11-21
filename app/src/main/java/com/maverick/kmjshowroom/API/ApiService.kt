package com.maverick.kmjshowroom.API

import com.maverick.kmjshowroom.Model.ActivityDetailResponse
import com.maverick.kmjshowroom.Model.ActivityResponse
import MobilDetailResponse
import com.google.gson.annotations.SerializedName
import com.maverick.kmjshowroom.Model.CheckUserResponse
import com.maverick.kmjshowroom.Model.DashboardResponse
import com.maverick.kmjshowroom.Model.LoginResponse
import com.maverick.kmjshowroom.Model.MobilItem
import com.maverick.kmjshowroom.Model.MobilListResponse
import com.maverick.kmjshowroom.Model.RegisterResponse
import com.maverick.kmjshowroom.Model.GeneralResponse
import com.maverick.kmjshowroom.Model.UpdateGeneralResponse
import com.maverick.kmjshowroom.Model.GenericResponse
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
}

    @GET("admin/mobil_list.php")
    suspend fun getMobilList(): Response<MobilListResponse>

    // DETAIL MOBIL
    @GET("admin/mobil_detail.php")
    fun getMobilDetail(@Query("kode_mobil") kode: String): Call<MobilDetailResponse>

    // INSERT / UPDATE MOBIL (smart mode)
    @Multipart
    @POST("admin/mobil_tambah.php")
    fun uploadMobil(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>
    ): Call<GenericResponse>

    // DELETE MOBIL
    @FormUrlEncoded
    @POST("admin/mobil_tambah.php")
    fun deleteMobil(
        @Field("delete") delete: Boolean = true,
        @Field("kode_mobil") kodeMobil: String
    ): Call<GenericResponse>
}