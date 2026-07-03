package com.neurotutor.app.mobile.data.model.auth

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val id: Long,
    val name: String,
    val email: String,
    val grade: String,
    val section: String,
    val level: String,
    val points: Int,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    val gender: String,
    @SerializedName("diagnostic_completed")
    val diagnosticCompleted: Boolean,
    @SerializedName("created_at")
    val createdAt: String
)
