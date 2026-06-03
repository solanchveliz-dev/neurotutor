package com.neurotutor.app.mobile.ui.models

data class AuthResponse(
    val mensaje: String,
    val email: String,
    val token: String,
    val id: String,
    val examenCompletado: Boolean // 🚀 NUEVO: Sincronizado con el Backend
)
