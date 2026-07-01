package com.neurotutor.app.mobile.data.model.auth

data class UserProfileResponse(
    val name: String,
    val email: String,
    val level: String,
    val points: Int,
    val avatarUrl: String?,
    val modulesCompleted: Int,
    val medalsCount: Int
)
