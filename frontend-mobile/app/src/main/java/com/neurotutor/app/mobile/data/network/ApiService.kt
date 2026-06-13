package com.neurotutor.app.mobile.data.network

import com.neurotutor.app.mobile.data.model.auth.*
import com.neurotutor.app.mobile.data.model.diagnostic.*
import com.neurotutor.app.mobile.data.model.learning.*
import com.neurotutor.app.mobile.data.model.common.*
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    @POST("api/diagnostic/submit")
    suspend fun submitDiagnostic(@Body request: DiagnosticRequest): Response<DiagnosticResponse>

    @GET("api/dashboard/student/{id}")
    suspend fun getStudentProfile(@Path("id") studentId: String): Response<StudentProfileResponse>

    @GET("api/learning/topic-ruta/{moduloId}")
    suspend fun getTopicRuta(
        @Path("moduloId") moduloId: String,
        @Query("studentId") studentId: String
    ): Response<List<ModuleItem>>

    @GET("api/learning/content/{moduloId}")
    suspend fun getLevelContent(@Path("moduloId") moduloId: String): Response<LearningContentResponse>

    @GET("api/learning/exam/{moduloId}")
    suspend fun getFinalExam(@Path("moduloId") moduloId: String): Response<List<Exercise>>

    @POST("api/learning/submit-exam")
    suspend fun submitExam(
        @Query("studentId") studentId: String,
        @Query("moduloId") moduloId: String,
        @Query("score") score: Int
    ): Response<String>

    @POST("api/learning/add-points")
    suspend fun addPoints(
        @Query("studentId") studentId: String,
        @Query("points") points: Int
    ): Response<Void>
}
