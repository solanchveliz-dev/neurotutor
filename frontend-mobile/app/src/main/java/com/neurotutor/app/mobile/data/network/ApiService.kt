package com.neurotutor.app.mobile.data.network

import com.neurotutor.app.mobile.data.model.auth.AuthResponse
import com.neurotutor.app.mobile.data.model.auth.ForgotPasswordRequest
import com.neurotutor.app.mobile.data.model.auth.LoginRequest
import com.neurotutor.app.mobile.data.model.auth.RegisterRequest
import com.neurotutor.app.mobile.data.model.auth.ResetPasswordRequest
import com.neurotutor.app.mobile.data.model.auth.StudentProfileResponse
import com.neurotutor.app.mobile.data.model.common.AiTutorRequest
import com.neurotutor.app.mobile.data.model.common.AiTutorResponse
import com.neurotutor.app.mobile.data.model.common.MessageResponse
import com.neurotutor.app.mobile.data.model.diagnostic.DiagnosticRequest
import com.neurotutor.app.mobile.data.model.diagnostic.DiagnosticResponse
import com.neurotutor.app.mobile.data.model.learning.Exercise
import com.neurotutor.app.mobile.data.model.learning.LearningContentResponse
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.data.model.learning.ExamPassedResponse
import com.neurotutor.app.mobile.data.model.learning.SubmitExamRequest
import com.neurotutor.app.mobile.data.model.learning.SubmitExamResponse
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

    @GET("api/dashboard/student/{id}")
    suspend fun getStudentProfile(@Path("id") studentId: String): Response<StudentProfileResponse>

    // ==================== ÉPICA 3: CONTENIDOS DE APRENDIZAJE ====================

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

    // ==================== EXÁMENES MEJORADOS (HU-25) ====================

    @GET("api/learning/exam-passed")
    suspend fun checkExamPassed(
        @Query("studentId") studentId: String,
        @Query("moduloId") moduloId: String
    ): Response<ExamPassedResponse>

    @POST("api/learning/submit-exam-v2")
    suspend fun submitExamV2(
        @Body request: SubmitExamRequest
    ): Response<SubmitExamResponse>


    @POST("api/ai/tutor")
    suspend fun askTutor(
        @Body request: AiTutorRequest
    ): Response<AiTutorResponse>

}