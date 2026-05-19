package com.neurotutor.app.mobile.ui.models

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)