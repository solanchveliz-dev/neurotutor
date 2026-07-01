package com.neurotutor.app.mobile.data.model.auth

data class ProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val grade: String,
    val section: String,
    val level: String,
    val points: Int,
    val avatarUrl: String?,
    val gender: String,
    val diagnosticCompleted: Boolean,
    val createdAt: String
)
