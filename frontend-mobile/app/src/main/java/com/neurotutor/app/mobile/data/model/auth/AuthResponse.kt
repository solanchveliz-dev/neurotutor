package com.neurotutor.app.mobile.data.model.auth

data class AuthResponse(
    val mensaje: String,
    val email: String,
    val token: String,
    val id: String,
    val examenCompletado: Boolean
)
