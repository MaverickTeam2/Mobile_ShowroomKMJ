package com.maverick.kmjshowroom.API

import com.maverick.kmjshowroom.Model.CheckUserResponse
import com.maverick.kmjshowroom.Model.LoginResponse
import com.maverick.kmjshowroom.Model.RegisterResponse
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
}
