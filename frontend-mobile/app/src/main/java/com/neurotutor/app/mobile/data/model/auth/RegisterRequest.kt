package com.neurotutor.app.mobile.data.model.auth

data class RegisterRequest(
    val email: String,
    val nombreCompleto: String,
    val grado: String,
    val seccion: String,
    val password: String,
    val password2: String
)