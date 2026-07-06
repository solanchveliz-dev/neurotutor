package com.neurotutor.app.mobile.data.model.auth

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)
