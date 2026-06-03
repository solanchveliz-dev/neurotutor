package com.neurotutor.app.mobile.ui.network

import com.neurotutor.app.mobile.ui.models.*
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ==================== RECUPERACIÓN DE CONTRASEÑA ====================

    @POST("api/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    // ==================== EXAMEN DIAGNÓSTICO ====================

    @POST("api/diagnostic/submit")
    suspend fun submitDiagnostic(@Body request: DiagnosticRequest): Response<DiagnosticResponse>

    // ==================== DASHBOARD DEL ESTUDIANTE ====================

    // 🚀 RUTA ACTUALIZADA: Debe coincidir con DashboardController.java (/api/dashboard/...)
    @GET("api/dashboard/student/{id}")
    suspend fun getStudentProfile(@Path("id") studentId: String): Response<StudentProfileResponse>
}
