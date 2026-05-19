package com.neurotutor.app.mobile.ui.models


data class RegisterRequest(
    val email: String,
    val nombreCompleto: String,
    val grado: String,
    val seccion: String,
    val password: String,
    val password2: String
)