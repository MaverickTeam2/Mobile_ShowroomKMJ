package com.maverick.kmjshowroom.API

import com.maverick.kmjshowroom.Model.ActivityDetailResponse
import com.maverick.kmjshowroom.Model.ActivityResponse
import com.maverick.kmjshowroom.Model.CheckUserResponse
import com.maverick.kmjshowroom.Model.DashboardResponse
import com.maverick.kmjshowroom.Model.LoginResponse
import com.maverick.kmjshowroom.Model.RegisterResponse
import com.maverick.kmjshowroom.Model.GeneralResponse
import com.maverick.kmjshowroom.Model.UpdateGeneralResponse
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
