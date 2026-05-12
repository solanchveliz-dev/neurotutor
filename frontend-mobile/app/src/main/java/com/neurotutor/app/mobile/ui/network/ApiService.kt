package com.neurotutor.app.mobile.ui.network
import com.neurotutor.app.mobile.ui.models.AuthResponse
import com.neurotutor.app.mobile.ui.models.LoginRequest
import com.neurotutor.app.mobile.ui.models.RegisterRequest
import retrofit2.http.*
import retrofit2.Response

interface ApiService {
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}